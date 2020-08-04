//package ar.edu.itba.paw.webapp.old_controller;
//
//import ar.edu.itba.paw.interfaces.service.*;
//import ar.edu.itba.paw.models.Tag;
//import ar.edu.itba.paw.models.User;
//import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
//import ar.edu.itba.paw.webapp.auth.SignUpAuthentication;
//import ar.edu.itba.paw.webapp.utility.Constants;
//import ar.edu.itba.paw.webapp.exception.ForbiddenAccessException;
//import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
//import ar.edu.itba.paw.webapp.form.*;
//import ar.edu.itba.paw.webapp.validations.ValidatorHelper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.context.i18n.LocaleContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.Valid;
//import java.time.Instant;
//import java.util.Collection;
//import java.util.Optional;
//
//@Controller
//public class RegistrationController {
//
//    @Autowired private PasswordEncoder passwordEncoder;
//    @Autowired private UserService userService;
//    @Autowired private EmailService emailService;
//    @Autowired private SignUpAuthentication signUpAuthentication;
//    @Autowired private LoginAuthentication loginAuthentication;
//    @Autowired private RoleService roleService;
//    @Autowired private CryptoService cryptoService;
//    @Autowired private MessageSource messageSource;
//    @Autowired private TagService tagService;
//    @Autowired private ValidatorHelper validatorHelper;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);
//
//    @RequestMapping(value = Constants.LOGIN)
//    public ModelAndView login(HttpServletRequest request) {
//        this.throwIfUserIsLoggedIn();
//
//        loginAuthentication.setLoginRedirect(request);
//
//        final ModelAndView mav = new ModelAndView("user/login");
//        mav.addObject("error", false);
//        return mav;
//    }
//
//    @RequestMapping(value = "/login_error")
//    public ModelAndView loginError() {
//        final ModelAndView mav = new ModelAndView("user/login");
//        mav.addObject("error", true);
//        return mav;
//    }
//
//    @RequestMapping(value = Constants.GOODBYE)
//    public ModelAndView logout() {
//        return new ModelAndView("user/logout");
//    }
//
//    @RequestMapping(value = Constants.SIGNUP, method = {RequestMethod.GET})
//    public ModelAndView signUpForm(HttpServletRequest request, @ModelAttribute("registerForm") final RegisterForm form) {
//        this.throwIfUserIsLoggedIn();
//
//        signUpAuthentication.setRegisterRedirect(request);
//
//        return new ModelAndView("user/signUpForm");
//    }
//
//    @RequestMapping(value = Constants.SIGNUP, method = {RequestMethod.POST})
//    public ModelAndView signUp(@Valid @ModelAttribute("registerForm") final RegisterForm registerForm, final BindingResult errors, HttpServletRequest request, HttpServletResponse response) {
//        if (errors.hasErrors()) {
//            return signUpForm(request, registerForm);
//        }
//
//        User user = this.userService.register(registerForm.getUsername(), this.passwordEncoder.encode(registerForm.getPassword()), registerForm.getEmail(), Instant.now(), LocaleContextHolder.getLocale());
//        try {
//            this.userService.registerFollowUp(user);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage() + "Failed to send registration email to user {}", registerForm.getUsername());
//        }
//
//        this.signUpAuthentication.authWithAuthManager(request, registerForm.getUsername(), registerForm.getPassword());
//        String redirectUrl = this.signUpAuthentication.redirectionAuthenticationSuccess(request);
//        return new ModelAndView("redirect:" + redirectUrl);
//    }
//
//    @RequestMapping(value = "/verify-email")
//    public ModelAndView verifyEmail(final @RequestParam(value="id") long id, @ModelAttribute("verificationForm") final EmailVerificationForm verificationForm, @ModelAttribute("searchForm") final SearchForm searchForm) {
//        ModelAndView mav = new ModelAndView("user/verifyEmail");
//        User currentUser = this.loginAuthentication.getLoggedInUser();
//        if (currentUser == null){
//            this.throwNoUser(id);
//            return null; // Unreachable since the function above will throw an exception
//        }
//        try {
//            this.emailService.sendVerificationEmail(currentUser);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage() + "Failed to send verification email to user {}", currentUser.getUsername());
//        }
//        mav.addObject("searchForm", searchForm);
//        this.addUserAttributes(currentUser, mav);
//        return mav;
//    }
//
//    @RequestMapping(value = "/verify-email", method = RequestMethod.POST)
//    public ModelAndView completeVerifyEmail(final @RequestParam(value="id") long id, @Valid @ModelAttribute("verificationForm") final EmailVerificationForm verificationForm, BindingResult errors, @ModelAttribute("searchForm") final SearchForm searchForm) {
//        ModelAndView mav = new ModelAndView("redirect:/user/" + id);
//        if (errors.hasErrors()){
//            return this.verifyEmail(id, verificationForm, searchForm);
//        }
//        // Getting the current user
//        User currentUser = this.loginAuthentication.getLoggedInUser();
//        if (currentUser == null || currentUser.getId() != id){
//            this.throwNoUser(id);
//            return mav; // Unreachable since the function above will throw an exception
//        }
//        // Checking the code sent by the user is valid
//        if (!validatorHelper.checkValidTOTP(currentUser, verificationForm.getCode(), errors, LocaleContextHolder.getLocale())) {
//            return this.verifyEmail(id, verificationForm, searchForm);
//        }
//
//        this.userService.verifyUserEmail(currentUser.getId());
//        return mav;
//    }
//
//    @RequestMapping(value = "/resend-email-verification", method = RequestMethod.POST)
//    public ModelAndView resendVerificationEmail(final @RequestParam(value="id") long id, @ModelAttribute("verificationForm") final EmailVerificationForm verificationForm, @ModelAttribute("searchForm") final SearchForm searchForm) {
//        ModelAndView mav = new ModelAndView("user/verifyEmail");
//        // Getting the current user
//        User currentUser = this.loginAuthentication.getLoggedInUser();
//        if (currentUser == null || currentUser.getId() != id){
//            this.throwNoUser(id);
//            return mav;
//        }
//        try {
//            this.emailService.sendVerificationEmail(currentUser);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage() + "Failed to send verification email to user {}", currentUser.getUsername());
//        }
//        mav.addObject("searchForm", searchForm);
//        this.addUserAttributes(currentUser, mav);
//        return mav;
//    }
//
//    @RequestMapping(value = Constants.RECOVER_PASSWORD)
//    public ModelAndView recoverPassword(@ModelAttribute("recoveryForm") final RecoveryForm recoveryForm, BindingResult errors) {
//        this.throwIfUserIsLoggedIn();
//        return new ModelAndView("user/recoverPassword");
//    }
//
//    @RequestMapping(value = Constants.RECOVER_PASSWORD, method = RequestMethod.POST)
//    public ModelAndView sendRecoveryMail(@Valid @ModelAttribute("recoveryForm") final RecoveryForm recoveryForm, BindingResult errors) {
//        if (errors.hasErrors()){
//            return recoverPassword(recoveryForm, errors);
//        }
//        User user = this.userService.findUserByEmail(recoveryForm.getEmail()).orElse(null);
//        if (user == null) {
//            throw new UserNotFoundException(messageSource.getMessage("error.404.user", new Object[]{recoveryForm.getEmail()}, LocaleContextHolder.getLocale()));
//        }
//        // Getting the URL for the server
//        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
//        try {
//            this.emailService.sendRecoveryEmail(user, baseUrl);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage() + "Failed to send recovery email to user {}", recoveryForm.getEmail());
//        }
//        return new ModelAndView("user/emailSent");
//    }
//
//    @RequestMapping(value = Constants.RESET_PASSWORD, method = RequestMethod.GET)
//    public ModelAndView resetPassword(final @RequestParam(value="id") long id,
//                                      final @RequestParam(value="token") String token,
//                                      @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm) {
//        this.throwIfUserIsLoggedIn();
//        Optional<User> userOpt = userService.findUserById(id);
//        if(!userOpt.isPresent()) {
//            throw new UserNotFoundException(messageSource.getMessage("error.404.user", new Object[]{id}, LocaleContextHolder.getLocale()));
//        }
//        User user = userOpt.get();
//        boolean pass = this.cryptoService.checkValidRecoverToken(user, token);
//        /* If link is no longer valid */
//        if (!pass) {
//            return new ModelAndView("user/recoveryLinkInvalid");
//        }
//        //in order to avoid calling db twice for user email
//        resetPasswordForm.setEmail(user.getEmail());
//        return new ModelAndView("user/resetPassword");
//    }
//
//    @RequestMapping(value = Constants.RESET_PASSWORD, method = RequestMethod.POST)
//    public ModelAndView endResetPassword (final @RequestParam(value="id") long id,
//                                          final @RequestParam(value="token") String token,
//                                          @ModelAttribute("resetPasswordForm") @Valid final ResetPasswordForm resetPasswordForm,
//                                          BindingResult errors){
//        if(errors.hasErrors()) {
//            return resetPassword(id, token, resetPasswordForm);
//        }
//        userService.changePassword(resetPasswordForm.getEmail(), passwordEncoder.encode(resetPasswordForm.getNewPassword()));
//        return new ModelAndView("user/passwordReset");
//    }
//
//    @ModelAttribute
//    public void addAttributes(Model model, @Valid final SearchForm searchForm) {
//        model.addAttribute("searchForm", searchForm);
//    }
//
//    private void addUserAttributes(User currentUser, ModelAndView mav){
//        Collection<Tag> userTags = this.tagService.getMostPopularFollowedTagsForUser(currentUser.getId(), Constants.MENU_FOLLOWING_TAG_AMOUNT);
//        Collection<Tag> allFollowedTags = this.tagService.getFollowedTagsForUser(currentUser.getId());
//        this.userService.updateLocale(currentUser.getId(), LocaleContextHolder.getLocale());
//        mav.addObject("currentUser", currentUser);
//        mav.addObject("userTags", userTags);
//        mav.addObject("userTagsCount", userTags.isEmpty() ? 0 : allFollowedTags.size() - userTags.size());
//        mav.addObject("userRoles", this.roleService.getUserRoles(currentUser.getId()));
//        mav.addObject("searchContext", "");
//    }
//
//    private void throwNoUser(long id) {
//        LOGGER.error("User with id {} doesn't exist", id);
//        throw new UserNotFoundException(messageSource.getMessage("error.404.user", new Object[]{id}, LocaleContextHolder.getLocale()));
//    }
//
//    private void throwIfUserIsLoggedIn() {
//        if (this.loginAuthentication.getLoggedInUser() != null) {
//            throw new ForbiddenAccessException(messageSource.getMessage("error.403.anonymous", null, LocaleContextHolder.getLocale()));
//        }
//    }
//}