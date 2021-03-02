package ar.edu.itba.paw.webapp.provider;

import ar.edu.itba.paw.webapp.dto.ValidationErrorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.internal.guava.Lists;
import org.springframework.http.ResponseEntity;

import javax.print.attribute.standard.Media;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements
        ExceptionMapper<ConstraintViolationException> {

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(ConstraintViolationException e) {

        List<ValidationErrorDto> errors = new ArrayList<>();
        for (final ConstraintViolation<?> error : e.getConstraintViolations()) {
            String[] path = error.getPropertyPath().toString().split("\\.");
            String elementName = path[path.length-1];
            ValidationErrorDto validationErrorDto = new ValidationErrorDto();
            validationErrorDto.setFieldName(elementName);
            validationErrorDto.setErrorMessage(error.getMessage());
            errors.add(validationErrorDto);
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(new GenericEntity<List<ValidationErrorDto>>(errors){}).build();
    }
}