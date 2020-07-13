package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.CryptoService;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.EmailVerificationForm;
import ar.edu.itba.paw.webapp.form.SearchForm;
import ar.edu.itba.paw.webapp.validations.ValidatorHelper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.Instant;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.utility.Constants.FRONT_BASE_URL_LOCAL;

@Path("/")
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
    @Context
    private SecurityContext securityContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    @POST
    @Path("/register")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response register(LoginDto registrationData) {
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

    @GET
    @Path(value = "/verify-email")
    public Response verifyEmail() {
        User currentUser = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (currentUser == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
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
    public Response completeVerifyEmail(VerificationDto verificationDto) {
        User currentUser = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (currentUser == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
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
    public Response sendResetEmail(RecoveryDto recoveryDto) {
        User user = this.userService.findUserByEmail(recoveryDto.getEmail()).orElse(null);
        if (user == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{recoveryDto.getEmail()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        try {
            this.emailService.sendRecoveryEmail(user, FRONT_BASE_URL_LOCAL);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "Failed to send recovery email to user {}", recoveryDto.getEmail());
            return Response.serverError().build();
        }
        return Response.accepted().build();
    }

    @PUT
    @Path("/reset-password")
    public Response changePassword(@QueryParam("id") final long id,
                                      ResetPasswordDto resetPasswordDto) {
        Optional<User> userOpt = userService.findUserById(id);
        if(!userOpt.isPresent()) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{id}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
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
