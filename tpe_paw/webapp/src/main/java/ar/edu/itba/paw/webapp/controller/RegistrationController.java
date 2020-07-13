package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.CryptoService;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import jdk.internal.org.xml.sax.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Optional;

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

    @PUT
    @Path("/send-recovery-email")
    public Response sendResetEmail(RecoveryDto recoveryDto) {
        User user = this.userService.findUserByEmail(recoveryDto.getEmail()).orElse(null);
        if (user == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{recoveryDto.getEmail()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        // Getting the URL for the server
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        try {
            this.emailService.sendRecoveryEmail(user, baseUrl);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "Failed to send recovery email to user {}", recoveryDto.getEmail());
            return Response.serverError().build();
        }
        return Response.accepted().build();
    }

    @PUT
    @Path("/change-password")
    public Response changePassword(@QueryParam("id") final long id,
                                        @QueryParam("token") final String token,
                                      ResetPasswordDto resetPasswordDto) {
        Optional<User> userOpt = userService.findUserById(id);
        if(!userOpt.isPresent()) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{id}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User user = userOpt.get();
        boolean pass = this.cryptoService.checkValidRecoverToken(user, token);
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
