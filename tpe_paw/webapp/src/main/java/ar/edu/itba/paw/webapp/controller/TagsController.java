package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.RoleService;
import ar.edu.itba.paw.interfaces.service.SnippetService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.ErrorMessageDto;
import ar.edu.itba.paw.webapp.dto.FollowDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import ar.edu.itba.paw.webapp.dto.TagDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

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


    @GET
    @Path("tags/{tagId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getTag(final @PathParam(value="tagId") long tagId,
                           final @Context Request request) {
        final Tag tag = tagService.findTagById(tagId).orElse(null);
        Long userId = loginAuthentication.getLoggedInUser().map(User::getId).orElse(null);

        if (tag == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.tag", new Object[]{tagId}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }

        TagDto tagDto = TagDto.fromTag(tag);
        tagDto.setUserFollowing(userId != null && tagService.userFollowsTag(userId, tag.getId()));
        CacheControl cc = new CacheControl();
        EntityTag eTag = new EntityTag(String.valueOf(tagDto.hashCode()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);

        if (builder == null) {
            builder = Response.ok(tagDto).tag(eTag);
        }


        return builder
                .cacheControl(cc)
                .build();
    }

    @GET
    @Path("/tags/{tagId}/snippets")
    public Response searchInTag(final @QueryParam("q") @DefaultValue("") String query,
                                final @QueryParam("t") String type,
                                final @QueryParam("uid") String userId,
                                final @QueryParam("s") String sort,
                                final @QueryParam("page") @DefaultValue("1") int page,
                                final @PathParam(value = "tagId") long tagId,
                                final @Context Request request) {

        String escapedQuery = query.replaceAll("%", "\\\\%");
        Tag tag = this.tagService.findTagById(tagId).orElse(null);
        if (tag == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.tag", new Object[]{tagId}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = searchHelper.findByCriteria(type, escapedQuery, SnippetDao.Locations.TAGS, sort, null, tagId, page)
                .stream()
                .map(sn -> SnippetDto.fromSnippet(sn, uriInfo, loginAuthentication.getLoggedInUser().orElse(null)))
                .collect(Collectors.toList());
        int totalSnippetCount = searchHelper.getSnippetByCriteriaCount(type, escapedQuery, SnippetDao.Locations.TAGS, null, tagId);

        return searchHelper.getResponse(query, type, userId, sort, page, snippets, totalSnippetCount, uriInfo);
    }



    @POST
    @Path("tags/{tagId}/follow")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response followTag(@PathParam(value="tagId") final long tagId,
                                          final FollowDto followDto,
                                          @Context SecurityContext securityContext) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.401.tag.follow", new Object[]{null}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessageDto).build();
        }
        tagService.followTag(user.getId(), tagId);
        return Response.ok(followDto).build();
    }

    @POST
    @Path("tags/{tagId}/unfollow")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response unfollowTag(@PathParam(value="tagId") final long tagId,
                                          final FollowDto followDto) {
        User user = loginAuthentication.getLoggedInUser().orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.401.tag.follow", new Object[]{null}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessageDto).build();
        }
        tagService.unfollowTag(user.getId(), tagId);
        return Response.ok(followDto).build();
    }

    @GET
    @Path("tags")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response searchTags(final @QueryParam("page") @DefaultValue("1") int page,
                               final @QueryParam("showEmpty") @DefaultValue("true") boolean showEmpty,
                               @QueryParam("showOnlyFollowing") @DefaultValue("false") boolean showOnlyFollowing,
                               final @QueryParam("q") @DefaultValue("") String q,
                               final @Context Request request) {
        // Find the user, check if it exists
        Long userId = loginAuthentication.getLoggedInUser().map(User::getId).orElse(null);

        String escapedQuery = q.replaceAll("%", "\\\\%");

        final List<TagDto> tags = new ArrayList<>();
        for(Tag t: tagService.findTagsByName(escapedQuery, showEmpty, showOnlyFollowing, userId, page, TAG_PAGE_SIZE)){
            TagDto tagDto = TagDto.fromTag(t);
            tagDto.setUserFollowing(userId != null && tagService.userFollowsTag(userId, t.getId()));
            tagDto.setSnippetsUsingIsEmpty(t.getSnippetsUsing().size() == 0);
            tags.add(tagDto);
        }


        int tagsCount = tagService.getAllTagsCountByName(escapedQuery, showEmpty, showOnlyFollowing, userId);
        int pageCount = (tagsCount/TAG_PAGE_SIZE) + ((tagsCount % TAG_PAGE_SIZE == 0) ? 0 : 1);

        Response.ResponseBuilder builder = Response.ok(new GenericEntity<List<TagDto>>(tags) {});
        builder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).queryParam("showEmpty", showEmpty).queryParam("showOnlyFollowing", showOnlyFollowing).queryParam("q", q).build(), "first")
               .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).queryParam("showEmpty", showEmpty).queryParam("showOnlyFollowing", showOnlyFollowing).queryParam("q", q).build(), "last");
        if (page > 1)
            builder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).queryParam("showEmpty", showEmpty).queryParam("showOnlyFollowing", showOnlyFollowing).queryParam("q", q).build(), "prev");
        if (page < pageCount)
            builder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).queryParam("showEmpty", showEmpty).queryParam("showOnlyFollowing", showOnlyFollowing).queryParam("q", q).build(), "next");
        builder.header("Cache-Control", "no-cache, no-store, must-revalidate");
        builder.header("Pragma", "no-cache");
        builder.header("Expires", "0");
        return builder.build();
    }

    /**
     * Can only create one tag at a time in order
     * to adhere to ReST best practices
     * @param tagDto
     * @return
     */
    @POST
    @PreAuthorize("hasRole('ADMIN')")
    @Path("tags")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response addTag(final TagDto tagDto) {
        Tag newTag = tagService.addTag(tagDto.getName());
        if (newTag == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.500.tags.create", new Object[]{tagDto.getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessageDto).build();
        }
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(newTag.getId())).build()).build();
    }

    @DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @Path("tags/{tagId}")
    public Response deleteTag(@PathParam(value="tagId") long tagId) {
        this.tagService.removeTag(tagId);
        return Response.ok().build();
    }
}
