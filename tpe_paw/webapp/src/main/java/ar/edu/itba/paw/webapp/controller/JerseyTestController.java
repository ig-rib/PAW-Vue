package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.webapp.dto.LanguageDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/jersey-test")
public class JerseyTestController {

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response test() {
        LanguageDto langDto = new LanguageDto();
        langDto.setName("JAVA");
        return Response
                .status(Response.Status.ACCEPTED)
                .header("Access-Control-Allow-Origin", "*")
                .entity(langDto)
                .build();
    }

}
