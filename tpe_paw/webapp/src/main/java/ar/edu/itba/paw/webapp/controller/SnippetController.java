package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.ErrorMessageDto;
import ar.edu.itba.paw.webapp.dto.SnippetCreateDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import ar.edu.itba.paw.webapp.exception.SnippetNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

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
    @Context UriInfo uriInfo;

    private static final Logger LOGGER = LoggerFactory.getLogger(ar.edu.itba.paw.webapp.old_controller.SnippetController.class);

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response snippetDetail(@PathParam(value="id") long id) {
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);

        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, securityContext.getUserPrincipal().getName());
        }

        SnippetDto snippetDto = SnippetDto.fromSnippet(snippet);
        return Response.ok(snippetDto).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSnippet(@PathParam(value="id") long id) {
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        Optional<Snippet> snippet = this.snippetService.findSnippetById(id);

        //TODO: Check what should we respond in the body in case of an error
        if (!snippet.isPresent()) {
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, securityContext.getUserPrincipal().getName());
        }
        if (user == null || user.getUsername().compareTo(snippet.get().getOwner().getUsername()) != 0) {
            return buildErrorResponse("error.403.snippet", Response.Status.FORBIDDEN, securityContext.getUserPrincipal().getName());
        } else {
            //TODO: Adapt to be able to restore snippet
            if (!this.snippetService.deleteOrRestoreSnippet(snippet.get(), user.getId(), true)) {
                /* Operation was unsuccessful */
                return buildErrorResponse("error.409.snippet", Response.Status.CONFLICT, securityContext.getUserPrincipal().getName());
            }
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/{id}/votes")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response votePositive(
            @PathParam(value="id") long id,
            final @QueryParam("type") boolean type,
            final @QueryParam("selected") boolean selected
    ) {

        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.403.snippet", Response.Status.FORBIDDEN, securityContext.getUserPrincipal().getName());
        } else {
            Snippet snippet = snippetService.findSnippetById(id).orElse(null);
            if(snippet == null){
                return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, securityContext.getUserPrincipal().getName());
            }
            this.voteService.performVote(snippet.getOwner().getId(), user.getId(), id, selected, type);
        }
        return Response.ok().build();
    }


    @PUT
    @Path("/{id}/favorite")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response favSnippet(
            @PathParam(value="id") long id,
            final @QueryParam("type") boolean favorite
    ) {
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);

        if (user == null) {
            return buildErrorResponse("error.403.snippet.fav", Response.Status.FORBIDDEN, null);
        } else {
            this.favService.updateFavorites(user.getId(), id, favorite);
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
            @QueryParam(value="detail") String detail,
            @QueryParam(value="baseUrl") String baseUrl
    ) {
        //TODO: Check from where we gonna obtain the base url of the report.
        // Getting the url of the server
        //final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, securityContext.getUserPrincipal().getName());
        }
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);

        if (user == null || user.equals(snippet.getOwner())) {
            return buildErrorResponse("error.403.snippet.report.owner", Response.Status.FORBIDDEN, null);
        } else if (!this.reportService.canReport(user)) {
            return buildErrorResponse("error.403.snippet.report.reputation", Response.Status.FORBIDDEN, null);
        }

        try {
            reportService.reportSnippet(user, snippet, detail, baseUrl);
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
        User user = loginAuthentication.getLoggedInUser();
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        long snippetId = snippetService.createSnippet(user, snippetDto.getTitle(), snippetDto.getDescription(), snippetDto.getCode(), Instant.now(), snippetDto.getLanguageId(), Collections.emptyList());
        return Response.created(uriInfo.getBaseUriBuilder().path("snippet").path(String.valueOf(snippetId)).build()).build();
    }

    //TODO: Check if delete is appropriate for this operation.
    @DELETE
    @Path("{id}/report/delete")
    public Response reportSnippet(@PathParam(value="id") long id) {
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        Snippet snippet = snippetService.findSnippetById(id).orElse(null);
        if(snippet == null){
            return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, securityContext.getUserPrincipal().getName());
        }

        if (user == null || !user.equals(snippet.getOwner())) {
            return buildErrorResponse("error.403.report.dismiss", Response.Status.FORBIDDEN, null);
        }
        this.reportService.dismissReportsForSnippet(id);
        return Response.ok().build();
    }

    @PUT
    @Path(value="/{id}/flag")
    public Response flagSnippet(
            @PathParam(value="id") long id,
            @QueryParam(value="isFlagged") boolean isFlagged,
            @QueryParam(value="baseUrl") String baseUrl
    ) {
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);

        if (user == null || !roleService.isAdmin(user.getId())) {
            return buildErrorResponse("error.403.snippet.flag", Response.Status.UNAUTHORIZED, null);
        } else {
            Snippet snippet = snippetService.findSnippetById(id).orElse(null);
            if(snippet == null){
                return buildErrorResponse("error.404.snippet", Response.Status.NOT_FOUND, securityContext.getUserPrincipal().getName());
            }

            // Getting the url of the server
            //TODO: Check how to handle baseUrl
            //final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            try {
                // Updating the flagged variable of snippet
                this.snippetService.updateFlagged(snippet, snippet.getOwner(), isFlagged, baseUrl);
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
