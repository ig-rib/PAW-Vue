package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.ErrorMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.beans.ExceptionListener;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(NotFoundException e) {
        ErrorMessageDto messageDto = new ErrorMessageDto();
        messageDto.setMessage(messageSource.getMessage("error.404", null, LocaleContextHolder.getLocale()));
        return Response.status(Response.Status.NOT_FOUND)
                .entity(messageDto)
                .build();
    }
}
