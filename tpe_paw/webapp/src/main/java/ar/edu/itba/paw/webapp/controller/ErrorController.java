package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.exception.RemovingLanguageInUseException;
import ar.edu.itba.paw.webapp.form.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@ControllerAdvice
public class ErrorController {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LoginAuthentication loginAuthentication;
    @Autowired
    private TagService tagService;

    private Set<Integer> supportedErrorPages = new HashSet<Integer>(){{
        add(403);
        add(404);
        add(500);
    }};

    @RequestMapping("/error")
    public ModelAndView customError(HttpServletRequest request, @ModelAttribute("searchForm") SearchForm searchForm) {
        int errorCode = this.getErrorCode(request);
        ModelAndView mav = new ModelAndView("errors/default");
        String message = messageSource.getMessage("error.unknown",null, LocaleContextHolder.getLocale());
        if (this.supportedErrorPages.contains(errorCode)){
            message =  messageSource.getMessage("error." + String.valueOf(errorCode),null, LocaleContextHolder.getLocale());
        }
        mav.addObject("err", errorCode);
        mav.addObject("msg", message);
        return mav;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView noSuchUser(HttpServletRequest request) {
        return new ModelAndView("errors/404");
    }

    @ExceptionHandler(RemovingLanguageInUseException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView cannotRemoveLanguage(RemovingLanguageInUseException ex) {
        // TODO make own error

        return new ModelAndView("errors/404");
    }


//
//    @ModelAttribute
//    public void addAttributes(Model model) {
//        User currentUser = this.loginAuthentication.getLoggedInUser();
//        Collection<Tag> userTags = currentUser != null ? this.tagService.getFollowedTagsForUser(currentUser.getId()) : new ArrayList<>();
//        model.addAttribute("currentUser", currentUser);
//        model.addAttribute("userTags", userTags);
//        model.addAttribute("searchContext", "");
//    }

}
