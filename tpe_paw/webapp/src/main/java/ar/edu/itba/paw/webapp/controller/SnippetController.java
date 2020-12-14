package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Vote;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Path("/snippet")
public class SnippetController {

    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    @Autowired private SnippetService snippetService;
    @Autowired private VoteService voteService;
    @Autowired private FavoriteService favService;
    @Autowired private LoginAuthentication loginAuthentication;
    @Autowired private TagService tagService;
    @Autowired private MessageSource messageSource;
    @Autowired private ReportService reportService;
    @Autowired private LanguageService languageService;
    @Autowired private SearchHelper searchHelper;
    @Context UriInfo uriInfo;

    private static final Logger LOGGER = LoggerFactory.getLogger(ar.edu.itba.paw.webapp.controller.SnippetController.class);
    

    @GET
    @Path("/")
    public Response searchInHome(final @QueryParam("q")  @DefaultValue("") String query,
                                 final @QueryParam("t") String type,
                                 final @QueryParam("uid") String userId,
                                 final @QueryParam("s") String sort,
                                 final @QueryParam("page") @DefaultValue("1") int page,
                                 final @Context Request request) {

        String escapedQuery = query.replaceAll("%", "\\\\%");

        List<SnippetDto> snippets = searchHelper.findByCriteria(type, escapedQuery, SnippetDao.Locations.HOME, sort, null, null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, escapedQuery, SnippetDao.Locations.HOME, null, null);

        return searchHelper.getResponse(query, type, userId, sort, page, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response snippetDetail(@PathParam(value="id") long id,
                                  @Context Request request) {

        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }

        SnippetDto snippetDto = null;
        User loggedInUser = this.loginAuthentication.getLoggedInUser().orElse(null);
        if (loggedInUser != null) {
            Vote vote = voteService.getVote(loggedInUser.getId(), snippet.getId()).orElse(null);
            Report report = reportService.getReport(loggedInUser.getId(), snippet.getId()).orElse(null);
            boolean reported = report != null;
            boolean reportedDismissed = report != null && report.isOwnerDismissed();
            boolean favorite = loggedInUser.getFavorites().contains(snippet);
            boolean showReportedWarning = this.reportService.showReportedWarning(snippet, loggedInUser);
            snippetDto = SnippetDto.fromSnippetWithDetail(snippet, uriInfo, voteService.getVoteBalance(snippet.getId()), vote, reported, favorite, reportedDismissed, showReportedWarning);
        } else {
            snippetDto = SnippetDto.fromSnippet(snippet, uriInfo);
        }

        // Checking eTag for this
        CacheControl cc = new CacheControl();
        EntityTag eTag = new EntityTag(Integer.toString(snippetDto.hashCode()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
        if (builder == null) {
            builder = Response.ok(snippetDto);
            builder.tag(eTag);
        }
        return builder.cacheControl(cc).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSnippet(final @PathParam(value="id") long id) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        Snippet snippet = this.snippetService.findSnippetById(id).orElse(null);

        if (snippet == null) {
            return buildErrorResponse("error.401.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }
        if (user == null){
            // Same message only 401 vs 403
            return buildErrorResponse("error.403.snippet.delete", Response.Status.UNAUTHORIZED, snippet.getId());
        }
        else if (user.getUsername().compareTo(snippet.getOwner().getUsername()) != 0) {
            return buildErrorResponse("error.403.snippet.delete", Response.Status.FORBIDDEN, snippet.getId());
        } else {
            if (!this.snippetService.deleteOrRestoreSnippet(snippet, user.getId(), true)) {
                /* Operation was unsuccessful */
                return buildErrorResponse("error.409.snippet", Response.Status.CONFLICT, loginAuthentication.getLoggedInUsername());
            }
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/{id}/restore")
    public Response restoreDeletedSnippet(final @PathParam("id") long id) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        Snippet snippet = this.snippetService.findSnippetById(id).orElse(null);

        if (snippet == null) {
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }
        if(user == null){
            return buildErrorResponse("error.403.snippet.restore", Response.Status.UNAUTHORIZED, snippet.getId());
        }
        if ( user.getUsername().compareTo(snippet.getOwner().getUsername()) != 0) {
            // Same message only 401 vs 403
            return buildErrorResponse("error.403.snippet.restore", Response.Status.FORBIDDEN, snippet.getId());
        } else {
            if (!this.snippetService.deleteOrRestoreSnippet(snippet, user.getId(), false)) {
                /* Operation was unsuccessful */
                return buildErrorResponse("error.409.snippet", Response.Status.CONFLICT, loginAuthentication.getLoggedInUsername());
            }
        }
        return Response.ok().build();
    }

    // Needed for admin to search snippets flagged by him/herself
    @GET
    @Path("/flagged")
    public Response searchInFlagged(final @QueryParam("q")  @DefaultValue("") String query,
                                    final @QueryParam("t") String type,
                                    final @QueryParam("uid") String userId,
                                    final @QueryParam("s") String sort,
                                    final @QueryParam("page") @DefaultValue("1") int page,
                                    final @Context Request request) {

        String escapedQuery = query.replaceAll("%", "\\\\%");

        List<SnippetDto> snippets = searchHelper.findByCriteria(type, escapedQuery, SnippetDao.Locations.FLAGGED, sort, null, null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, escapedQuery, SnippetDao.Locations.FLAGGED, null, null);
        return searchHelper.getResponse(query, type, userId, sort, page, snippets, totalSnippetCount, uriInfo);
    }

    @PUT
    @Path("/{id}/vote")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response votePositive(
            @PathParam(value="id") long id,
            VoteDto voteDto
    ) {

        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.401.snippet.vote", Response.Status.UNAUTHORIZED, null);
        } else {
            Snippet snippet = snippetService.findSnippetById(id).orElse(null);
            if(snippet == null){
                return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
            }
            this.voteService.performVote(snippet.getOwner().getId(), user.getId(), id, voteDto.getSelected(), voteDto.getPositive());
            VoteResponseDto vrDto = VoteResponseDto.createVoteResponse(voteService.getVoteBalance(snippet.getId()), snippet.getOwner().getReputation(), voteService.getVote(user.getId(), snippet.getId()).orElse(null));
            return Response.ok(vrDto).build();
        }

    }


    @PUT
    @Path("/{id}/favorite")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response favSnippet(
            @PathParam(value="id") long id
    ) {
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.401.snippet.fav", Response.Status.UNAUTHORIZED, null);
        } else {
            this.favService.updateFavorites(user.getId(), id, true);
            LOGGER.debug("User {} updated favorite on snippet {}", user.getUsername(), id);
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}/favorite")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response unfavSnippet(
            @PathParam(value="id") long id
    ) {
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.401.snippet.fav", Response.Status.UNAUTHORIZED, null);
        } else {
            this.favService.updateFavorites(user.getId(), id, false);
            LOGGER.debug("User {} updated favorite on snippet {}", user.getUsername(), id);
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/{id}/report")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response reportSnippet(
            @PathParam(value="id") long id,
            @Valid ReportDto reportDto
    ) {
        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if(user == null){
            return buildErrorResponse("error.401.snippet.report", Response.Status.UNAUTHORIZED, null);
        }
        else if(user.equals(snippet.getOwner())) {
            // same message but 403
            return buildErrorResponse("error.403.snippet.report.owner", Response.Status.FORBIDDEN, null);
        } else if (!this.reportService.canReport(user)) {
            return buildErrorResponse("error.403.snippet.report.reputation", Response.Status.FORBIDDEN, null);
        }

        try {
            final boolean result = reportService.reportSnippet(user, snippet, reportDto.getDetail(), reportDto.getBaseUri());
            // This happens if user already reported
            if(!result){
                return buildErrorResponse("error.403.snippet.report.repeated", Response.Status.FORBIDDEN, null);
            }
        } catch (Exception e) {
            return buildErrorResponse("error.500.snippet.report.failure", Response.Status.INTERNAL_SERVER_ERROR,snippet.getOwner().getUsername());
            //LOGGER.error(e.getMessage() + "Failed report snippet: user {} about their snippet {}", snippet.getOwner().getUsername(), snippet.getId());
        }
        LOGGER.debug("User {} reported snippet {} with message {}", user.getUsername(), id, reportService);

        return Response.ok().build();
    }

    @POST
    @PreAuthorize("hasRole('USER')")
    @Path("/")
    public Response createSnippet(@Valid SnippetCreateDto snippetDto) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.401.snippet.create", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessageDto).build();
        }
        else if( roleService.isAdmin(user.getId())){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.403.snippet.create", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.FORBIDDEN).entity(errorMessageDto).build();
        }
        long snippetId = snippetService.createSnippet(user, snippetDto.getTitle(), snippetDto.getDescription(), snippetDto.getCode(), Instant.now(), snippetDto.getLanguageId(), snippetDto.getTags());
        return Response.created(uriInfo.getBaseUriBuilder().path("snippet").path(String.valueOf(snippetId)).build()).build();
    }

    @DELETE
    @Path("/{id}/report")
    public Response unreportSnippet(@PathParam(value="id") long id) {
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);
        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, id);
        }
        if (user == null) {
            return buildErrorResponse("error.401.snippet.report", Response.Status.UNAUTHORIZED, loginAuthentication.getLoggedInUsername());
        }
        // Owner is dismissing all reports for snippet
        if (user.getId().equals(snippet.getOwner().getId()))
            this.reportService.dismissAllReportsForSnippet(id);
        else
            this.reportService.deleteReportsForSnippet(id, user.getId());
        return Response.ok().build();
    }

    @PUT
    @Path(value="/{id}/flag")
    public Response flagSnippet(
            @PathParam(value="id") long id,
            String baseUri
    ) {
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.401.snippet.flag", Response.Status.UNAUTHORIZED, loginAuthentication.getLoggedInUsername());
        } else {
            Snippet snippet = snippetService.findSnippetById(id).orElse(null);
            if(snippet == null){
                return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
            }

            try {
                // Updating the flagged variable of snippet
                this.snippetService.updateFlagged(snippet, snippet.getOwner(), true, baseUri    );
            } catch (Exception e) {
                LOGGER.error(e.getMessage() + "Failed to flag snippet {}", snippet.getId());
                return buildErrorResponse("error.500", Response.Status.INTERNAL_SERVER_ERROR, null);
            }
            LOGGER.debug("Marked snippet {} as flagged by admin", id);

        }
        return Response.ok().build();
    }

    @DELETE
    @Path(value="/{id}/flag")
    public Response unflagSnippet(
            @PathParam(value="id") long id,
            String baseUri
    ) {
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.401.snippet.flag", Response.Status.UNAUTHORIZED, loginAuthentication.getLoggedInUsername());
        } else {
            Snippet snippet = snippetService.findSnippetById(id).orElse(null);
            if(snippet == null){
                return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
            }

            try {
                // Updating the flagged variable of snippet
                this.snippetService.updateFlagged(snippet, snippet.getOwner(), false, baseUri);
            } catch (Exception e) {
                LOGGER.error(e.getMessage() + "Failed to flag snippet {}", snippet.getId());
                return buildErrorResponse("error.500", Response.Status.INTERNAL_SERVER_ERROR, null);
            }
            LOGGER.debug("Marked snippet {} as flagged by admin", id);

        }
        return Response.ok().build();
    }

    private Response buildErrorResponse(String errorMessage, Response.StatusType errorStatus, Object errorObject){
        ErrorMessageDto errorMessageDto = new ErrorMessageDto();
        errorMessageDto.setMessage(messageSource.getMessage(errorMessage, new Object[]{errorObject}, LocaleContextHolder.getLocale()));
        Response resp = Response.status(errorStatus).entity(errorMessageDto).build();
        return resp;
    }

}
