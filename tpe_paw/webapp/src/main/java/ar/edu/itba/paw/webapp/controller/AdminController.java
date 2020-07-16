package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.LanguageService;
import ar.edu.itba.paw.interfaces.service.RoleService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.AdminAddDto;
import ar.edu.itba.paw.webapp.validations.ValidatorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Path("/admin")
public class AdminController {

    @Autowired private LanguageService languageService;
    @Autowired private TagService tagService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @POST
    @Path("/add")
    public Response adminAdd(AdminAddDto adminAddDto) {
        /* Language List */
        List<String> languages = adminAddDto.getLanguages() != null ? adminAddDto.getLanguages() : Collections.emptyList();
        languages.removeAll(Arrays.asList("", null));

        /* Tag List */
        List<String> tags = adminAddDto.getTags() != null ? adminAddDto.getTags() : Collections.emptyList();
        tags.removeAll(Arrays.asList("", null));

        if (!languages.isEmpty()) languageService.addLanguages(languages);
        if (!tags.isEmpty()) tagService.addTags(tags);

        LOGGER.debug("Admin added languages and tags -> {} and {}", languages.toString(), tags.toString());

        return Response.ok().build();
    }
}
