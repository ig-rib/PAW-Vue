package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.CryptoService;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.Instant;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.utility.Constants.BASE_URL_LOCAL;
import static ar.edu.itba.paw.webapp.utility.Constants.BASE_URL_DEPLOY;

@Path("registration")
@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CryptoService cryptoService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private EmailService emailService;

    @Autowired private LoginAuthentication loginAuthentication;

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    @GET
    @Path("/username-exists")
    public Response userExists(final @QueryParam("uname") String username) {
        if (username == null || userService.isUsernameUnique(username))
            return Response.ok(ExistsDto.of(false)).build();
        return Response.ok(ExistsDto.of(true)).build();
    }

    @GET
    @Path("/email-exists")
    public Response emailExists(final @QueryParam("email") String email) {
        if (email == null || !userService.emailExists(email))
            return Response.ok(ExistsDto.of(false)).build();
        return Response.ok(ExistsDto.of(true)).build();
    }

    @POST
    @Path("/register")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response register(@Valid RegistrationDto registrationData) {
        // Check if user and email exist
        boolean emailExists = userService.emailExists(registrationData.getEmail());
        boolean usernameExists = !userService.isUsernameUnique(registrationData.getUsername());
        if (emailExists || usernameExists) {
            RegistrationErrorDto reDto = new RegistrationErrorDto();
            reDto.setEmailExists(emailExists);
            reDto.setUsernameExists(usernameExists);
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(reDto)
                    .build();
        }
        userService.register(registrationData.getUsername(), passwordEncoder.encode(registrationData.getPassword()), registrationData.getEmail(), Instant.now(), LocaleContextHolder.getLocale());
        return Response.accepted().build();
    }

    @PUT
    @Path(value = "/verify-email")
    public Response verifyEmail() {
        User currentUser = loginAuthentication.getLoggedInUser().orElse(null);
        if (currentUser == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessageDto).build();
        }
        try {
            this.emailService.sendVerificationEmail(currentUser);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "Failed to send verification email to user {}", currentUser.getUsername());
        }
        return Response.ok().build();
    }

    @POST
    @Path(value = "/verify-email")
    public Response completeVerifyEmail(@Valid VerificationDto verificationDto) {
        User currentUser = userService.findUserByUsername(loginAuthentication.getLoggedInUsername()).orElse(null);
        if (currentUser == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessageDto).build();
        }
        // Checking the code sent by the user is valid
        if (!this.cryptoService.checkValidTOTP(currentUser, verificationDto.getCode())) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.400.invalidVerificationCode", null, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessageDto).build();
        }

        this.userService.verifyUserEmail(currentUser.getId());
        return Response.accepted().build();
    }

    @PUT
    @Path("/send-recovery-email")
    public Response sendResetEmail(@Valid RecoveryDto recoveryDto) {
        User user = this.userService.findUserByEmail(recoveryDto.getEmail()).orElse(null);
        if (user == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{recoveryDto.getEmail()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessageDto).build();
        }
        try {
            // TODO change to deploy (better, automatize)
            // this.emailService.sendRecoveryEmail(user, BASE_URL_LOCAL);
           this.emailService.sendRecoveryEmail(user, BASE_URL_DEPLOY);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "Failed to send recovery email to user {}", recoveryDto.getEmail());
            return Response.serverError().build();
        }
        return Response.accepted().build();
    }

    @PUT
    @Path("/reset-password")
    public Response changePassword(@QueryParam("id") final long id,
                                      @Valid ResetPasswordDto resetPasswordDto) {
        Optional<User> userOpt = userService.findUserById(id);
        if(!userOpt.isPresent()) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{id}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessageDto).build();
        }
        User user = userOpt.get();
        boolean pass = this.cryptoService.checkValidRecoverToken(user, resetPasswordDto.getToken());
        /* If link is no longer valid */
        if (!pass) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.400.linkNoLongerValid", null, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessageDto).build();
        }
        userService.changePassword(user.getEmail(), resetPasswordDto.getPassword());
        // No content suits this case better than accepted
        return Response.noContent().build();
    }
}
