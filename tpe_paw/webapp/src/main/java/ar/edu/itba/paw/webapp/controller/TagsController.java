package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.RoleService;
import ar.edu.itba.paw.interfaces.service.SnippetService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.exception.ForbiddenAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.Principal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.SNIPPET_PAGE_SIZE;
import static ar.edu.itba.paw.webapp.utility.Constants.TAG_PAGE_SIZE;

@Path("/")
public class TagsController {

    @Autowired
    private TagService tagService;
    @Autowired
    private SnippetService snippetService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SearchHelper searchHelper;
    @Autowired
    private LoginAuthentication loginAuthentication;

    private static final Logger LOGGER = LoggerFactory.getLogger(TagsController.class);

    @Context
    private UriInfo uriInfo;

    //TODO: See if better to use loginAuthentication directly
    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/tags")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllTags(final @QueryParam("page") @DefaultValue("1") int page,
                                final @QueryParam("showEmpty") @DefaultValue("true") boolean showEmpty,
                                @QueryParam("showOnlyFollowing") @DefaultValue("false") boolean showOnlyFollowing) {

        //TODO: Modularize repeated code in searchTags

        // Find the user, check if it exists
        Long userId = null;
        Optional<User> userOpt = Optional.empty();
        if (securityContext.getUserPrincipal() != null) {
            userOpt = userService.findUserByUsername(securityContext.getUserPrincipal().getName());
            if(userOpt.isPresent()){
                userId = userOpt.get().getId();
            }
        }

        // Check if showOnlyFollowing is activated --> If user is not logged return unauthorized.
        if (showOnlyFollowing && !userOpt.isPresent())
                return Response.status(HttpStatus.UNAUTHORIZED.value()).build();

        //TODO: See if better just to store the following data in the user.
        final List<TagDto> tags = new ArrayList<>();
        for(Tag t: tagService.getAllTags(showEmpty, showOnlyFollowing, userId, page, TAG_PAGE_SIZE)){
            TagDto tagDto = TagDto.fromTag(t);
            tagDto.setUserFollowing(userOpt.isPresent() && tagService.userFollowsTag(userId, t.getId()));
            tags.add(tagDto);
        }

        int tagsCount = tagService.getAllTagsCount(showEmpty, showOnlyFollowing, userId);
        int pageCount = (tagsCount/TAG_PAGE_SIZE) + ((tagsCount % TAG_PAGE_SIZE == 0) ? 0 : 1);

        Response.ResponseBuilder respBuilder = Response.ok(new GenericEntity<List<TagDto>>(tags) {})
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).build(), "last");
        if (page > 1)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).build(), "next");

        return respBuilder.build();
    }

    @GET
    @Path("tags/{tagId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response showSnippetsForTag(final @QueryParam("page") @DefaultValue("1") int page,
        final @PathParam(value="tagId") long tagId) {
        final List<SnippetDto> snippets = snippetService.findSnippetsForTag(tagId, page, SNIPPET_PAGE_SIZE)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());

        int snippetsCount = snippetService.getAllSnippetsByTagCount(tagId);
        int pageCount = (snippetsCount/SNIPPET_PAGE_SIZE) + ((snippetsCount % SNIPPET_PAGE_SIZE == 0) ? 0 : 1);

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
    @Path("/tags/{tagId}/snippets")
    public Response searchInTag(final @QueryParam("q") String query,
                                final @QueryParam("t") String type,
                                final @QueryParam("uid") String userId,
                                final @QueryParam("s") String sort,
                                final @QueryParam("page") @DefaultValue("1") int page,
                                final @PathParam(value = "tagId") long tagId) {

        Tag tag = this.tagService.findTagById(tagId).orElse(null);
        if (tag == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.tag", new Object[]{tagId}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.TAGS, sort, null, tagId, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.TAGS, null, tagId);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("q", query);
        queryParams.put("t", type);
        queryParams.put("uid", userId);
        queryParams.put("s", sort);

        return searchHelper.generateResponseWithLinks(page, queryParams, snippets, totalSnippetCount, uriInfo);    }

    //TODO: Check follow/unfollow repsonse and how info is received
    @POST
    @Path("tags/{tagId}/follow")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response followTag(@PathParam(value="tagId") final long tagId,
                                          final FollowDto followDto,
                                          @Context SecurityContext securityContext) {
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        tagService.followTag(user.getId(), tagId);
        return Response.ok(followDto).build();
    }

    @POST
    @Path("tags/{tagId}/unfollow")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response unfollowTag(@PathParam(value="tagId") final long tagId,
                                          final FollowDto followDto) {
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        tagService.unfollowTag(user.getId(), tagId);
        return Response.ok(followDto).build();
    }

    @GET
    @Path("/following/snippets")
    public Response searchInFollowing(final @QueryParam("q") String query,
                                      final @QueryParam("t") String type,
                                      final @QueryParam("uid") String userId,
                                      final @QueryParam("s") String sort,
                                      final @QueryParam("page") @DefaultValue("1") int page) {

        User user = loginAuthentication.getLoggedInUser();
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, query, SnippetDao.Locations.FOLLOWING, sort, user.getId(), null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.FOLLOWING, user.getId(), null);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("q", query);
        queryParams.put("t", type);
        queryParams.put("uid", userId);
        queryParams.put("s", sort);

        return searchHelper.generateResponseWithLinks(page, queryParams, snippets, totalSnippetCount, uriInfo);
    }

    @GET
    @Path("tags")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response searchTags(final @QueryParam("page") @DefaultValue("1") int page,
                               final @QueryParam("showEmpty") @DefaultValue("true") boolean showEmpty,
                               @QueryParam("showOnlyFollowing") @DefaultValue("false") boolean showOnlyFollowing,
                               final @QueryParam("q") @DefaultValue("") String q) {
        // Find the user, check if it exists
        Long userId = null;
        Optional<User> userOpt = Optional.empty();
        if (securityContext.getUserPrincipal() != null) {
            userOpt = userService.findUserByUsername(securityContext.getUserPrincipal().getName());
            if(userOpt.isPresent()){
                userId = userOpt.get().getId();
            }
        }
        if (showOnlyFollowing && !userOpt.isPresent())
            return Response.status(HttpStatus.UNAUTHORIZED.value()).build();

        // Check if showOnlyFollowing is activated --> If user is not logged return unauthorized.
//        List<TagDto> tags = tagService.findTagsByName(query, showEmpty, showOnlyFollowing, userId, page, TAG_PAGE_SIZE)
//                .stream()
//                .map(TagDto::fromTag)
//                .collect(Collectors.toList());
//        int tagsCount = this.tagService.getAllTagsCountByName(query, showEmpty, showOnlyFollowing, userId);

        //TODO: See if better just to store the following data in the user.
        final List<TagDto> tags = new ArrayList<>();
        for(Tag t: tagService.findTagsByName(q, showEmpty, showOnlyFollowing, userId, page, TAG_PAGE_SIZE)){
            TagDto tagDto = TagDto.fromTag(t);
            tagDto.setUserFollowing(userOpt.isPresent() && tagService.userFollowsTag(userId, t.getId()));
            tags.add(tagDto);
        }

        int tagsCount = tagService.getAllTagsCountByName(q, showEmpty, showOnlyFollowing, userId);
        int pageCount = (tagsCount/TAG_PAGE_SIZE) + ((tagsCount % TAG_PAGE_SIZE == 0) ? 0 : 1);

        Response.ResponseBuilder respBuilder = Response.ok(new GenericEntity<List<TagDto>>(tags) {})
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).queryParam("showEmpty", showEmpty).queryParam("showOnlyFollowing", showOnlyFollowing).queryParam("q", q).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).queryParam("showEmpty", showEmpty).queryParam("showOnlyFollowing", showOnlyFollowing).queryParam("q", q).build(), "last");
        if (page > 1)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).queryParam("showEmpty", showEmpty).queryParam("showOnlyFollowing", showOnlyFollowing).queryParam("q", q).build(), "prev");
        if (page < pageCount)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).queryParam("showEmpty", showEmpty).queryParam("showOnlyFollowing", showOnlyFollowing).queryParam("q", q).build(), "next");
        return respBuilder.build();
    }

    @DELETE
    @Path("tags/{tagId}/delete")
    public Response deleteTag(@PathParam(value="tagId") long tagId) {
        Long userId = null;
        Optional<User> userOpt = Optional.empty();
        if (securityContext.getUserPrincipal() != null) {
            userOpt = userService.findUserByUsername(securityContext.getUserPrincipal().getName());
            if(userOpt.isPresent()){
                userId = userOpt.get().getId();
            }
        }

        if ( userOpt.isPresent()  && roleService.isAdmin(userId)){
            this.tagService.removeTag(tagId);
            LOGGER.debug("Admin deleted tag with id {}", tagId);
        } else {
            LOGGER.warn("No user logged in or logged in user not admin but attempting to delete tag {}", tagId);
            return Response.status(HttpStatus.UNAUTHORIZED.value()).build();
        }
        return Response.ok().build();
    }
}
