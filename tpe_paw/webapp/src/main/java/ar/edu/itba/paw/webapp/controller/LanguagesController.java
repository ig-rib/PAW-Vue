package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import ar.edu.itba.paw.webapp.exception.LanguageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.LANGUAGE_PAGE_SIZE;
import static ar.edu.itba.paw.webapp.utility.Constants.SNIPPET_PAGE_SIZE;

//TODO: Repeated code: modularize

@Component
@Path("/")
public class LanguagesController {

    @Autowired private LanguageService languageService;
    @Autowired private SnippetService snippetService;
    @Autowired private LoginAuthentication loginAuthentication;
    @Autowired private TagService tagService;
    @Autowired private RoleService roleService;
    @Autowired private UserService userService;
    @Autowired private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguagesController.class);

    @Context
    private UriInfo uriInfo;

    //TODO: See if better to use loginAuthentication directly
    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/languages")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response showAllLanguages(final @QueryParam("page") @DefaultValue("1") int page) {

        Collection<Language> allLanguages = this.languageService.getAllLanguages(true, page, LANGUAGE_PAGE_SIZE);

        for (Language language : allLanguages) {
            this.snippetService.analizeSnippetsUsing(language);
        }

        int languageCount = this.languageService.getAllLanguagesCount(true);
        int pageCount = (languageCount/LANGUAGE_PAGE_SIZE) + (languageCount % LANGUAGE_PAGE_SIZE == 0 ? 0 : 1);
        final List<LanguageDto> languagesDto = allLanguages.stream()
            .map(LanguageDto::fromLanguage).collect(Collectors.toList());

        Response.ResponseBuilder responseBuilder =  Response.ok(new GenericEntity<List<LanguageDto>>(languagesDto) {})
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).build(), "last");
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).build(), "next");

        return responseBuilder.build();
    }

    @GET
    @Path("languages/search")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response searchInAllLanguages(@QueryParam("page") @DefaultValue("1") int page,
                                         @QueryParam("showEmpty") @DefaultValue("true") boolean showEmpty,
                                         @QueryParam("q") String q){

        Collection<Language> allLanguages = this.languageService.findAllLanguagesByName(q, showEmpty, page, LANGUAGE_PAGE_SIZE);
        int languageCount = this.languageService.getAllLanguagesCountByName(q, showEmpty);

        for (Language language : allLanguages) {
            this.snippetService.analizeSnippetsUsing(language);
        }

        int pageCount = (languageCount/LANGUAGE_PAGE_SIZE) + (languageCount % LANGUAGE_PAGE_SIZE == 0 ? 0 : 1);
        final List<LanguageDto> languagesDto = allLanguages.stream()
                .map(LanguageDto::fromLanguage).collect(Collectors.toList());

        Response.ResponseBuilder responseBuilder =  Response.ok(new GenericEntity<List<LanguageDto>>(languagesDto) {})
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).queryParam("showEmpty", showEmpty).queryParam("name", q).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).queryParam("showEmpty", showEmpty).queryParam("name", q).build(), "last");
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).queryParam("showEmpty", showEmpty).queryParam("name", q).build(), "prev");
        if (page < pageCount)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).queryParam("showEmpty", showEmpty).queryParam("name", q).build(), "next");

        return responseBuilder.build();
    }

    @GET
    @Path("languages/{langId}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response showSnippetsForLang(@PathParam(value="langId") long langId,
        final @QueryParam("page") @DefaultValue("1") int page){

        /* Retrieve the language */
        Optional<Language> language = languageService.findById(langId);
        if (!language.isPresent()) {
            LOGGER.warn("No language found with id {}", langId);
            // TODO: Check how to handle exception
            throw new LanguageNotFoundException(messageSource.getMessage("error.404.language", new Object[]{langId}, LocaleContextHolder.getLocale()));
        }
        Collection<Snippet> snippets = snippetService.getSnippetsWithLanguage(langId, page, SNIPPET_PAGE_SIZE);
        int snippetsCount = this.snippetService.getAllSnippetsByLanguageCount(langId);

        List<SnippetDto> snippetsDto = snippets.stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int pageCount = (snippetsCount/SNIPPET_PAGE_SIZE) + ((snippetsCount % SNIPPET_PAGE_SIZE == 0) ? 0 : 1);

        Response.ResponseBuilder respBuilder = Response.ok(new GenericEntity<List<SnippetDto>>(snippetsDto) {})
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).build(), "last");
        if (page > 1)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).build(), "next");
        return respBuilder.build();
    }

    @POST
    @Path("languages/{langId}/delete")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response deleteLanguage (@PathParam("langId") long langId) {
        Long userId = null;
        Optional<User> userOpt = Optional.empty();
        if (securityContext.getUserPrincipal() != null) {
            userOpt = userService.findUserByUsername(securityContext.getUserPrincipal().getName());
            if(userOpt.isPresent()){
                userId = userOpt.get().getId();
            }
        }

        if ( userOpt.isPresent() && roleService.isAdmin(userId)){
            this.languageService.removeLanguage(langId);
            LOGGER.debug("Admin removed language with id {}", langId);
        } else {
            LOGGER.error("No user logged in or logged in user not admin but language {} is trying to be deleted", langId);
            return Response.status(HttpStatus.UNAUTHORIZED.value()).build();
        }
        return Response.ok().build();
    }




}