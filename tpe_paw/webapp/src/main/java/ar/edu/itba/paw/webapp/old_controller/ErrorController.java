//package ar.edu.itba.paw.webapp.old_controller;
//
//import ar.edu.itba.paw.interfaces.service.RoleService;
//import ar.edu.itba.paw.interfaces.service.TagService;
//import ar.edu.itba.paw.models.User;
//import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
//import ar.edu.itba.paw.webapp.form.SearchForm;
//import ar.edu.itba.paw.webapp.utility.MavHelper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.context.i18n.LocaleContextHolder;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.*;
//
//@Controller
//public class ErrorController {
//
//    @Autowired
//    private MessageSource messageSource;
//    @Autowired
//    private LoginAuthentication loginAuthentication;
//    @Autowired
//    private TagService tagService;
//    @Autowired
//    private RoleService roleService;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);
//
//    private final static Set<Integer> supportedErrorPages;
//    static {
//        final Set<Integer> errorSet = new HashSet<Integer>();
//        errorSet.add(404);
//        errorSet.add(413);
//        errorSet.add(414);
//        errorSet.add(500);
//        supportedErrorPages = Collections.unmodifiableSet(errorSet);
//    }
//
//    @RequestMapping("/error")
//    public ModelAndView customError(HttpServletRequest request, @ModelAttribute("searchForm") SearchForm searchForm) {
//        int errorCode = this.getErrorCode(request);
//        ModelAndView mav = new ModelAndView("errors/default");
//        String message;
//        String errorName;
//
//        if (supportedErrorPages.contains(errorCode)){
//            message =  messageSource.getMessage("error." + String.valueOf(errorCode),null, LocaleContextHolder.getLocale());
//            errorName = messageSource.getMessage("error." + String.valueOf(errorCode) +".name",null, LocaleContextHolder.getLocale());
//        } else {
//            message = messageSource.getMessage("error.unknown",null, LocaleContextHolder.getLocale());
//            errorName = messageSource.getMessage("error.unknown.name",null, LocaleContextHolder.getLocale());
//        }
//        mav.addObject("err", errorCode);
//        mav.addObject("errName", errorName);
//        mav.addObject("msg", message);
//        return mav;
//    }
//
//    private int getErrorCode(HttpServletRequest httpRequest) {
//        return (Integer) httpRequest
//                    .getAttribute("javax.servlet.error.status_code");
//    }
//
//    @ModelAttribute
//    public void addAttributes(Model model, HttpServletRequest request) {
//        User currentUser = this.loginAuthentication.getLoggedInUser(request);
//        MavHelper.addCurrentUserAttributes(model, currentUser, tagService, roleService);
//        model.addAttribute("searchContext", "error/");
//        LOGGER.error("Unknown error for user {}", currentUser != null ? currentUser.getId() : "-");
//    }
//}