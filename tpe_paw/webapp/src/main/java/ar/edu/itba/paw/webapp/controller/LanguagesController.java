package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.ErrorMessageDto;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.LANGUAGE_PAGE_SIZE;

//TODO: Repeated code: modularize

@Component
@Path("languages")
public class LanguagesController {

    @Autowired private LanguageService languageService;
    @Autowired private SnippetService snippetService;
    @Autowired private LoginAuthentication loginAuthentication;
    @Autowired private TagService tagService;
    @Autowired private RoleService roleService;
    @Autowired private UserService userService;
    @Autowired private MessageSource messageSource;
    @Autowired private SearchHelper searchHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguagesController.class);

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("{langId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getLanguage(final @PathParam(value="langId") long langId,
                                final @Context Request request) {
        final Language lang = languageService.findById(langId).orElse(null);
        if (lang == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.language", new Object[]{langId}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }

        LanguageDto languageDto = LanguageDto.fromLanguage(lang);

        CacheControl cc = new CacheControl();
        EntityTag eTag = new EntityTag(String.valueOf(languageDto.hashCode()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);

        if (builder == null) {
            builder = Response.ok(languageDto).tag(eTag);
        }
        return builder.cacheControl(cc).build();
    }

    @GET
    @Path("/{langId}/snippets")
    public Response searchInLanguage(final @QueryParam("q") String query,
                                     final @QueryParam("t") String type,
                                     final @QueryParam("uid") String userId,
                                     final @QueryParam("s") String sort,
                                     final @QueryParam("page") @DefaultValue("1") int page,
                                     final @PathParam(value = "langId") long langId) {

        Language language = this.languageService.findById(langId).orElse(null);
        if (language == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.language", new Object[]{langId}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.LANGUAGES, sort, null, langId, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.LANGUAGES, null, langId);

        return searchHelper.getResponse(query, type, userId, sort, page, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("/")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response searchInAllLanguages(@QueryParam("page") @DefaultValue("1") int page,
                                         @QueryParam("showEmpty") @DefaultValue("true") boolean showEmpty,
                                         @QueryParam("q") @DefaultValue("") String q){

        Collection<Language> allLanguages = this.languageService.findAllLanguagesByName(q, showEmpty, page, LANGUAGE_PAGE_SIZE);
        int languageCount = this.languageService.getAllLanguagesCountByName(q, showEmpty);

        for (Language language : allLanguages) {
            this.snippetService.analizeSnippetsUsing(language);
        }

        int pageCount = (languageCount/LANGUAGE_PAGE_SIZE) + (languageCount % LANGUAGE_PAGE_SIZE == 0 ? 0 : 1);
        final List<LanguageDto> languagesDto = allLanguages.stream()
                .map(LanguageDto::fromLanguage).collect(Collectors.toList());

        Response.ResponseBuilder builder =  Response.ok(new GenericEntity<List<LanguageDto>>(languagesDto) {});
        builder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).queryParam("showEmpty", showEmpty).queryParam("q", q).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).queryParam("showEmpty", showEmpty).queryParam("q", q).build(), "last");
        if (page > 1)
            builder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).queryParam("showEmpty", showEmpty).queryParam("q", q).build(), "prev");
        if (page < pageCount)
            builder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).queryParam("showEmpty", showEmpty).queryParam("q", q).build(), "next");

        return builder
                .build();
    }

    @POST
    @PreAuthorize("hasRole('ADMIN')")
    @Path("/")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response addLanguage(final LanguageDto languageDto) {
        Language newLanguage = languageService.addLanguage(languageDto.getName());
        if (newLanguage == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.500.languages.create", new Object[]{languageDto.getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessageDto).build();
        }
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(newLanguage.getId())).build()).build();
    }

    @DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @Path("/{langId}")
    public Response deleteLanguage (@PathParam("langId") long langId) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);

        if (user != null && roleService.isAdmin(user.getId())){
            this.languageService.removeLanguage(langId);
            LOGGER.debug("Admin removed language with id {}", langId);
        } else {
            LOGGER.error("No user logged in or logged in user not admin but language {} is trying to be deleted", langId);
            return Response.status(HttpStatus.UNAUTHORIZED.value()).build();
        }
        return Response.noContent().build();
    }




}