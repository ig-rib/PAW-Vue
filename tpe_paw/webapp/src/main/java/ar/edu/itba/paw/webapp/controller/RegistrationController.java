package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.AuthTokenDto;
import ar.edu.itba.paw.webapp.dto.LoginDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//@Path("/registration")
//@Controller
public class RegistrationController {

//    @POST
//    @Path("/login")
//    @Produces(value = {MediaType.APPLICATION_JSON})
//    @Consumes(value = {MediaType.APPLICATION_JSON})
//    public Response login(final LoginDto user) {
////        try {
////            authTokenDto.setToken(token);
////            return Response.accepted(authTokenDto).build();
////        } catch (JWTCreationException exception) {
////            // TODO handle exception
////            return Response.serverError().build();
////        }
//        return Response.ok().build();
//    }
}
