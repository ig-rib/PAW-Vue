package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Vote;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.exception.SnippetNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

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
    public Response searchInHome(final @QueryParam("q") String query,
                                 final @QueryParam("t") String type,
                                 final @QueryParam("uid") String userId,
                                 final @QueryParam("s") String sort,
                                 final @QueryParam("page") @DefaultValue("1") int page) {
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.HOME, sort, null, null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.HOME, null, null);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("q", query);
        queryParams.put("t", type);
        queryParams.put("uid", userId);
        queryParams.put("s", sort);

        return searchHelper.generateResponseWithLinks(page, queryParams, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response snippetDetail(@PathParam(value="id") long id) {

        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }

        SnippetDto snippetDto = null;
        User loggedInUser = this.loginAuthentication.getLoggedInUser().orElse(null);
        if (loggedInUser != null) {
            Vote vote = voteService.getVote(loggedInUser.getId(), snippet.getId()).orElse(null);
            boolean reported = reportService.getReport(loggedInUser.getId(), snippet.getId()).isPresent();
            boolean favorite = loggedInUser.getFavorites().contains(snippet);
            snippetDto = SnippetDto.fromSnippetWithDetail(snippet, uriInfo, voteService.getVoteBalance(snippet.getId()), vote, reported, favorite);
        } else {
            snippetDto = SnippetDto.fromSnippet(snippet, uriInfo);
        }
        return Response.ok(snippetDto).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSnippet(final @PathParam(value="id") long id) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        Snippet snippet = this.snippetService.findSnippetById(id).orElse(null);

        //TODO: Check what should we respond in the body in case of an error
        if (snippet == null) {
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }
        if (user == null || user.getUsername().compareTo(snippet.getOwner().getUsername()) != 0) {
            return buildErrorResponse("error.403.snippet", Response.Status.FORBIDDEN, snippet.getId());
        } else {
            //TODO: Adapt to be able to restore snippet
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

        //TODO: Check what should we respond in the body in case of an error
        if (snippet == null) {
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }
        if (user == null || user.getUsername().compareTo(snippet.getOwner().getUsername()) != 0) {
            return buildErrorResponse("error.403.snippet", Response.Status.FORBIDDEN, snippet.getId());
        } else {
            //TODO: Adapt to be able to restore snippet
            if (!this.snippetService.deleteOrRestoreSnippet(snippet, user.getId(), true)) {
                /* Operation was unsuccessful */
                return buildErrorResponse("error.409.snippet", Response.Status.CONFLICT, loginAuthentication.getLoggedInUsername());
            }
        }
        return Response.ok().build();
    }

    // Needed for admin to search snippets flagged by him/herself
    @GET
    @Path("/flagged")
    public Response searchInFlagged(final @QueryParam("q") String query,
                                    final @QueryParam("t") String type,
                                    final @QueryParam("uid") String userId,
                                    final @QueryParam("s") String sort,
                                    final @QueryParam("page") @DefaultValue("1") int page) {

        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.FLAGGED, sort, null, null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.FLAGGED, null, null);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("q", query);
        queryParams.put("t", type);
        queryParams.put("uid", userId);
        queryParams.put("s", sort);

        return searchHelper.generateResponseWithLinks(page, queryParams, snippets, totalSnippetCount, uriInfo);
    }

    @POST
    @Path("/{id}/vote")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response votePositive(
            @PathParam(value="id") long id,
            VoteDto voteDto
    ) {

        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.403.snippet", Response.Status.FORBIDDEN, loginAuthentication.getLoggedInUsername());
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
            return buildErrorResponse("error.403.snippet.fav", Response.Status.FORBIDDEN, null);
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
            return buildErrorResponse("error.403.snippet.fav", Response.Status.FORBIDDEN, null);
        } else {
            this.favService.updateFavorites(user.getId(), id, true);
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
            ReportDto reportDto
    ) {
        //TODO: Check from where we gonna obtain the base url of the report.
        // Getting the url of the server
        //final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null || user.equals(snippet.getOwner())) {
            return buildErrorResponse("error.403.snippet.report.owner", Response.Status.FORBIDDEN, null);
        } else if (!this.reportService.canReport(user)) {
            // TODO uncomment when not testing
//            return buildErrorResponse("error.403.snippet.report.reputation", Response.Status.FORBIDDEN, null);
        }

        try {
            reportService.reportSnippet(user, snippet, reportDto.getDetail(), reportDto.getBaseUri());
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "Failed report snippet: user {} about their snippet {}", snippet.getOwner().getUsername(), snippet.getId());
            return buildErrorResponse("error.500.snippet.report.failure", Response.Status.INTERNAL_SERVER_ERROR,snippet.getOwner().getUsername());
        }
        LOGGER.debug("User {} reported snippet {} with message {}", user.getUsername(), id, reportService);

        return Response.ok().build();
    }

    @POST
    @Path("/create")
    public Response createSnippet(SnippetCreateDto snippetDto) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.username", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        long snippetId = snippetService.createSnippet(user, snippetDto.getTitle(), snippetDto.getDescription(), snippetDto.getCode(), Instant.now(), snippetDto.getLanguageId(), Collections.emptyList());
        return Response.created(uriInfo.getBaseUriBuilder().path("snippet").path(String.valueOf(snippetId)).build()).build();
    }

    //TODO: Check if delete is appropriate for this operation.
    @DELETE
    @Path("/{id}/report")
    public Response unreportSnippet(@PathParam(value="id") long id) {
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);
        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, id);
        }
        // TODO add OR !user.equals(report.owner)
        if (user == null) {
            return buildErrorResponse("error.404.username", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        }
        this.reportService.dismissReportsForSnippet(id);
        return Response.ok().build();
    }

    // TODO check flagging endpoints
    @PUT
    @Path(value="/{id}/flag")
    public Response flagSnippet(
            @PathParam(value="id") long id
    ) {
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.404.user", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        } else {
            Snippet snippet = snippetService.findSnippetById(id).orElse(null);
            if(snippet == null){
                return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
            }

            // Getting the url of the server
            //TODO: Check how to handle baseUrl
            //final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            try {
                // Updating the flagged variable of snippet
                this.snippetService.updateFlagged(snippet, snippet.getOwner(), true, "");
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
            @PathParam(value="id") long id
    ) {
        User user = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.404.user", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
        } else {
            Snippet snippet = snippetService.findSnippetById(id).orElse(null);
            if(snippet == null){
                return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, loginAuthentication.getLoggedInUsername());
            }

            // Getting the url of the server
            //TODO: Check how to handle baseUrl
            //final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            try {
                // Updating the flagged variable of snippet
                this.snippetService.updateFlagged(snippet, snippet.getOwner(), false, "");
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
        return Response.status(errorStatus).entity(errorMessageDto).build();
    }

    //TODO: Delete
    @Deprecated
    private Snippet getSnippet(final long snippetId) {
        Optional<Snippet> retrievedSnippet = this.snippetService.findSnippetById(snippetId);
        if (!retrievedSnippet.isPresent()) {
            logAndThrow(snippetId);
        }
        return retrievedSnippet.get();
    }

    @Deprecated
    private void logAndThrow(final long snippetId) {
        LOGGER.warn("No snippet found for id {}", snippetId);
        throw new SnippetNotFoundException(messageSource.getMessage("error.404.snippet", new Object[]{snippetId}, LocaleContextHolder.getLocale()));
    }
}
