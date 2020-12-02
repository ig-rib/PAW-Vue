package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.RoleService;
import ar.edu.itba.paw.interfaces.service.SnippetService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.*;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/user")
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
    @Autowired
    private SearchHelper searchHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GET
    @Path("/current")
    public Response getLoggedInUser(final @QueryParam("uname") String username) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        return Response.ok(UserDto.fromUser(user, roleService.isAdmin(user.getId()), uriInfo)).build();
    }

    @GET
    @Path("/snippets/upvoted")
    public Response searchInUpvoted(final @QueryParam("q") String query,
                                    final @QueryParam("t") String type,
                                    final @QueryParam("s") String sort,
                                    final @QueryParam("page") @DefaultValue("1") int page,
                                    final @Context Request request) {

        User user = loginAuthentication.getLoggedInUser().orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.UPVOTED, sort, user.getId(), null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.UPVOTED, user.getId(), null);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("q", query);
        queryParams.put("t", type);
        queryParams.put("s", sort);

        return searchHelper.generateResponseWithLinks(page, queryParams, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("{id}/snippets/deleted")
    public Response searchInDeletedUserSnippets(final @QueryParam("q") String query,
                                                final @QueryParam("t") String type,
                                                final @QueryParam("uid") String userId,
                                                final @QueryParam("s") String sort,
                                                final @QueryParam("page") @DefaultValue("1") int page,
                                                final @PathParam(value = "id") long id,
                                                final @Context Request request) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User currentUser = loginAuthentication.getLoggedInUser().orElse(null);
        if (currentUser == null || !user.equals(currentUser)) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.403.profile.owner", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.FORBIDDEN).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.DELETED, sort, id, null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.DELETED, id, null);

        return searchHelper.getResponse(query, type, userId, sort, page, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("/{id}/snippets/active")
    public Response searchInActiveUserSnippets(final @QueryParam("q") String query,
                                               final @QueryParam("t") String type,
                                               final @QueryParam("uid") String userId,
                                               final @QueryParam("s") String sort,
                                               final @QueryParam("page") @DefaultValue("1") int page,
                                               final @PathParam(value = "id") long id,
                                               final @Context Request request) {
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.USER, sort, id, null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.USER, id, null);

        return searchHelper.getResponse(query, type, userId, sort, page, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("snippets/favorites")
    public Response searchInFavorites(final @QueryParam("q") String query,
                                      final @QueryParam("t") String type,
                                      final @QueryParam("s") String sort,
                                      final @QueryParam("page") @DefaultValue("1") int page,
                                      final @Context Request request) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.FAVORITES, sort, user.getId(), null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.FAVORITES, user.getId(), null);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("q", query);
        queryParams.put("t", type);
        queryParams.put("s", sort);

        return searchHelper.generateResponseWithLinks(page, queryParams, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("/snippets/following")
    public Response searchInFollowing(final @QueryParam("q") String query,
                                      final @QueryParam("t") String type,
                                      final @QueryParam("uid") String userId,
                                      final @QueryParam("s") String sort,
                                      final @QueryParam("page") @DefaultValue("1") int page,
                                      final @Context Request request) {

        User user = loginAuthentication.getLoggedInUser().orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.FOLLOWING, sort, user.getId(), null, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.FOLLOWING, user.getId(), null);
        return searchHelper.getResponse(query, type, userId, sort, page, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("{id}")
    public Response getUser(final @PathParam(value="id") long id) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        UserDto userDto = UserDto.fromUser(user, roleService.isAdmin(user.getId()), uriInfo);
        return Response.ok(userDto).build();
    }

    @GET
    @Path("{id}/profile-photo")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getProfilePhoto(final @PathParam("id") long id,
                                    @Context Request request) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        byte[] icon = user.getIcon();
        // handle case where no icon exists
        CacheControl cc = new CacheControl();
        // TODO set constant
//        cc.setMaxAge(86400);
        EntityTag etag = new EntityTag(Integer.toString(Arrays.hashCode(icon)));
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder == null) {
            builder = Response.ok(icon);
            builder.tag(etag);
        }
        builder.cacheControl(cc);
        return builder.build();
    }

    @PUT
    @CachePut(cacheNames="profile-photo", key="#id")
    @Path("{id}/profile-photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPhoto(@PathParam(value="id") final long id,
                                 @FormDataParam("photo") final InputStream iconInputStream,
                                 @FormDataParam("photo") final FormDataContentDisposition iconContentDisposition
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
        byte[] icon;
        try {
            icon = IOUtils.toByteArray(iconInputStream);
        } catch (IOException e) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.500.io.image", null, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessageDto).build();
        }
        userService.changeProfilePhoto(currentUser.getId(), icon);
        return Response.accepted().build();
    }

    @POST
    @Path("{id}/profile-photo64")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadPhoto64(@PathParam(value="id") final long id,
                                final @Valid ProfilePhotoDto profilePhotoDto
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
        userService.changeProfilePhotoBase64(currentUser.getId(), profilePhotoDto.getEncodedPhoto());
        return Response.accepted().build();
    }


    @PUT
    @Path("{id}/user-data")
    public Response updateUserData(@PathParam(value="id") final long id, @Valid UserDataDto userDataDto) {
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
        if (userDataDto.getDescription() != null)
            userService.changeDescription(id, userDataDto.getDescription());
        if (userDataDto.getEncodedPhoto() != null && !userDataDto.getEncodedPhoto().isEmpty())
            userService.changeProfilePhotoBase64(currentUser.getId(), userDataDto.getEncodedPhoto());
        return Response.accepted().build();
    }
}
