package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.LANGUAGE_PAGE_SIZE;

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

        //TODO: add search form value
        Collection<Language> allLanguages = this.languageService.getAllLanguages(true, page, LANGUAGE_PAGE_SIZE);

        for (Language language : allLanguages) {
            this.snippetService.analizeSnippetsUsing(language);
        }

        //TODO: add form value
        int languageCount = this.languageService.getAllLanguagesCount(true);
        int pageCount = (languageCount/LANGUAGE_PAGE_SIZE) + (languageCount % LANGUAGE_PAGE_SIZE == 0 ? 0 : 1);


        final List<LanguageDto> languagesDto = allLanguages.stream()
            .map(LanguageDto::fromLanguage).collect(Collectors.toList());

        Response.ResponseBuilder responseBuilder =  Response.ok(new GenericEntity<List<LanguageDto>>(languagesDto) {})
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Expose-Headers", "Link")
                .header("Access-Control-Allow-Methods","GET, POST, PUT, DELETE")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).build(), "last");
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).build(), "next");

        return responseBuilder.build();
    }

}