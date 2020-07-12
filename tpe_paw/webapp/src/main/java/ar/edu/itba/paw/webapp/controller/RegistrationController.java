package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.AuthTokenDto;
import ar.edu.itba.paw.webapp.dto.LoginDto;
import ar.edu.itba.paw.webapp.dto.RegistrationErrorDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;

@Path("/")
@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
}
