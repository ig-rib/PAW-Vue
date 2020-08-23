package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.RoleService;
import ar.edu.itba.paw.interfaces.service.SnippetService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.ErrorMessageDto;
import ar.edu.itba.paw.webapp.dto.ProfilePhotoDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.SNIPPET_PAGE_SIZE;

@Path("/")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private SnippetService snippetService;
    @Autowired
    private TagService tagService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LoginAuthentication loginAuthentication;
    @Context
    private UriInfo uriInfo;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GET
    @Path("user/{id}/active")
    public Response activeUserSnippets(
            final @PathParam(value="id") long id,
            final @QueryParam(value = "page") @DefaultValue("1") int page
    ) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }

        List<SnippetDto> snippets = this.snippetService.getAllSnippetsByOwner(user.getId(), page, SNIPPET_PAGE_SIZE)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int snippetCount = this.snippetService.getAllSnippetsByOwnerCount(user.getId());
        int pageCount = (snippetCount/SNIPPET_PAGE_SIZE) + ((snippetCount % SNIPPET_PAGE_SIZE == 0) ? 0 : 1);

        Response.ResponseBuilder respBuilder = Response.ok(new GenericEntity<List<SnippetDto>>(snippets) {})
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).build(), "last");
        if (page > 1)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).build(), "next");
        return respBuilder.build();
    }

    @GET
    @Path("user/{id}/deleted")
    public Response deletedUserSnippets(
            final @PathParam(value="id") long id,
            final @QueryParam(value = "page") @DefaultValue("1") int page
    ) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{id}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User currentUser =  loginAuthentication.getLoggedInUser().orElse(null);
        if (currentUser == null || !user.equals(currentUser)) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.403.profile.owner", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.FORBIDDEN).entity(errorMessageDto).build();
        }

        List<SnippetDto> snippets = this.snippetService.getAllDeletedSnippetsByOwner(user.getId(), page, SNIPPET_PAGE_SIZE)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int snippetCount = this.snippetService.getAllDeletedSnippetsByOwnerCount(user.getId());
        int pageCount = (snippetCount/SNIPPET_PAGE_SIZE) + ((snippetCount % SNIPPET_PAGE_SIZE == 0) ? 0 : 1);

        Response.ResponseBuilder respBuilder = Response.ok(new GenericEntity<List<SnippetDto>>(snippets) {})
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).build(), "last");
        if (page > 1)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).build(), "next");
        return respBuilder.build();
    }

    @GET
    @Path("user/{id}")
    public Response getUser(final @PathParam(value="id") long id) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        UserDto userDto = UserDto.fromUser(user);
        return Response.ok(userDto).build();
    }

    @PUT
    @Path("user/{id}/profile-photo")
    public Response uploadPhoto(@PathParam(value="id") final long id, ProfilePhotoDto profilePhotoDto) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{id}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User currentUser =  loginAuthentication.getLoggedInUser().orElse(null);
        if (currentUser == null || !user.equals(currentUser)) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.403.profile.owner", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.FORBIDDEN).entity(errorMessageDto).build();
        }
        userService.changeProfilePhotoBase64(currentUser.getId(), profilePhotoDto.getEncodedPhoto());
        return Response.accepted().build();
    }

    @PUT
    @Path("user/{id}/user-data")
    public Response updateUserData(@PathParam(value="id") final long id, UserDto userDto) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{id}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User currentUser =  loginAuthentication.getLoggedInUser().orElse(null);
        if (currentUser == null || !user.equals(currentUser)) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.403.profile.owner", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.FORBIDDEN).entity(errorMessageDto).build();
        }
        userService.changeDescription(id, userDto.getDescription());
        return Response.accepted().build();
    }
}
