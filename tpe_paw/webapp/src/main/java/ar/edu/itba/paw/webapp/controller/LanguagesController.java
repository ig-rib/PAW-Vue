package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.ItemSearchDto;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import ar.edu.itba.paw.webapp.exception.ForbiddenAccessException;
import ar.edu.itba.paw.webapp.exception.LanguageNotFoundException;
import ar.edu.itba.paw.webapp.form.DeleteForm;
import ar.edu.itba.paw.webapp.form.ItemSearchForm;
import ar.edu.itba.paw.webapp.utility.MavHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
@Path("/languages")
public class LanguagesControllerJ {

    @Autowired private LanguageService languageService;
    @Autowired private SnippetService snippetService;
    @Autowired private LoginAuthentication loginAuthentication;
    @Autowired private TagService tagService;
    @Autowired private RoleService roleService;
    @Autowired private UserService userService;
    @Autowired private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguagesControllerJ.class);

    @Context
    private UriInfo uriInfo;

    @GET
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

    @POST
    @Path("/search")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response searchInAllLanguages(@QueryParam("page") @DefaultValue("1") int page,
                                         @QueryParam("showEmpty") @DefaultValue("true") boolean showEmpty,
                                         @QueryParam("showOnlyFollowing") @DefaultValue("false") boolean showOnlyFollowing,
                                         @QueryParam("name") String name){

        Collection<Language> allLanguages = this.languageService.findAllLanguagesByName(name, showEmpty, page, LANGUAGE_PAGE_SIZE);
        int languageCount = this.languageService.getAllLanguagesCountByName(name, showEmpty);

        for (Language language : allLanguages) {
            this.snippetService.analizeSnippetsUsing(language);
        }

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
    @Path("/{langId}")
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
    @Path("/{langId}/delete")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response deleteLanguage (@PathParam("langId") long langId) {
        User currentUser = loginAuthentication.getLoggedInUser();
        if ( currentUser != null && roleService.isAdmin(currentUser.getId())){
            this.languageService.removeLanguage(langId);
            LOGGER.debug("Admin removed language with id {}", langId);
        } else {
            LOGGER.error("No user logged in or logged in user not admin but language {} is trying to be deleted", langId);
            return Response.status(HttpStatus.FORBIDDEN.value()).build();
        }
        return Response.ok().build();
    }




}