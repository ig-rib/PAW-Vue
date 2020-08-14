package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.ErrorMessageDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import ar.edu.itba.paw.webapp.utility.Constants;
import ar.edu.itba.paw.webapp.exception.ElementDeletionException;
import ar.edu.itba.paw.webapp.exception.ForbiddenAccessException;
import ar.edu.itba.paw.webapp.exception.SnippetNotFoundException;
import ar.edu.itba.paw.webapp.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Path("/snippets")
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ar.edu.itba.paw.webapp.old_controller.SnippetController.class);

    @Context
    private UriInfo uriInfo;

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
            Snippet snippet = this.getSnippet(id);
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
        Snippet snippet = this.getSnippet(id);
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

    @RequestMapping(value="/snippet/{id}/report/dismiss", method={RequestMethod.POST})
    public ModelAndView reportSnippet(
            @ModelAttribute("snippetId") @PathVariable("id") long id,
            @ModelAttribute("dismissReportForm") final DeleteForm dismissReportForm
    ) {
        User currentUser = loginAuthentication.getLoggedInUser();
        Snippet snippet = this.getSnippet(id);

        if (currentUser == null || !currentUser.equals(snippet.getOwner())) {
            throw new ForbiddenAccessException(messageSource.getMessage("error.403.snippet.report.dismiss", null, LocaleContextHolder.getLocale()));
        }
        this.reportService.dismissReportsForSnippet(id);
        return new ModelAndView("redirect:/snippet/" + id);
    }

    @RequestMapping(value="/snippet/{id}/flag", method=RequestMethod.POST)
    public ModelAndView flagSnippet(
            @ModelAttribute("snippetId") @PathVariable("id") long id,
            @ModelAttribute("adminFlagForm") final FlagSnippetForm adminFlagForm
    ) {
        User currentUser = this.loginAuthentication.getLoggedInUser();
        if (currentUser == null || !roleService.isAdmin(currentUser.getId())) {
            throw new ForbiddenAccessException(messageSource.getMessage("error.403.snippet.flag", null, LocaleContextHolder.getLocale()));
        } else {
            Snippet snippet = this.getSnippet(id);

            // Getting the url of the server
            final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            try {
                // Updating the flagged variable of snippet
                this.snippetService.updateFlagged(snippet, snippet.getOwner(), adminFlagForm.isFlagged(), baseUrl);
            } catch (Exception e) {
                LOGGER.error(e.getMessage() + "Failed to flag snippet {}", snippet.getId());
            }
            LOGGER.debug("Marked snippet {} as flagged by admin", id);

        }
        return new ModelAndView("redirect:/snippet/" + id);
    }

    private Snippet getSnippet(final long snippetId) {
        Optional<Snippet> retrievedSnippet = this.snippetService.findSnippetById(snippetId);
        if (!retrievedSnippet.isPresent()) {
            logAndThrow(snippetId);
        }
        return retrievedSnippet.get();
    }

    private Response buildErrorResponse(String errorMessage, Response.StatusType errorStatus, Object errorObject){
        ErrorMessageDto errorMessageDto = new ErrorMessageDto();
        errorMessageDto.setMessage(messageSource.getMessage(errorMessage, new Object[]{errorObject}, LocaleContextHolder.getLocale()));
        return Response.status(errorStatus).entity(errorMessageDto).build();
    }

    @Deprecated
    private void logAndThrow(final long snippetId) {
        LOGGER.warn("No snippet found for id {}", snippetId);
        throw new SnippetNotFoundException(messageSource.getMessage("error.404.snippet", new Object[]{snippetId}, LocaleContextHolder.getLocale()));
    }
}
