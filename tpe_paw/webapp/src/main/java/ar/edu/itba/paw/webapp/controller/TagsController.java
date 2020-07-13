package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.SnippetService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.SNIPPET_PAGE_SIZE;
import static ar.edu.itba.paw.webapp.utility.Constants.TAG_PAGE_SIZE;

@Path("/tags")
public class TagsController {

    @Autowired
    private TagService tagService;
    @Autowired
    private SnippetService snippetService;
    @Autowired
    private UserService userService;

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllTags(@QueryParam("page") @DefaultValue("1") int page,
                                @QueryParam("showEmpty") @DefaultValue("true") boolean showEmpty,
                                @QueryParam("showOnlyFollowing") @DefaultValue("false") boolean showOnlyFollowing) {
        // Find the user, check if it exists
        Long userId = null;
        Optional<User> userOpt;
        // TODO(nth) should return unauthorized if showOnlyFollowing is true && userPrincipal is null
        if (showOnlyFollowing && securityContext.getUserPrincipal() != null) {
            userOpt = userService.findUserByUsername(securityContext.getUserPrincipal().getName());
            if (!userOpt.isPresent())
                return Response.serverError().build();
            userId = userOpt.get().getId();
        }
        else showOnlyFollowing = false;

        final List<TagDto> tags = tagService.getAllTags(showEmpty, showOnlyFollowing, userId, page, TAG_PAGE_SIZE)
                .stream()
                .map(TagDto::fromTag)
                .collect(Collectors.toList());

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
    @Path("/{tagId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response showSnippetsForTag(@QueryParam("page") @DefaultValue("1") int page,
        @PathParam(value="tagId") long tagId) {

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

    @POST
    @Path("/{tagId}/follow")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response followTag(@PathParam(value="tagId") final long tagId,
                                          FollowDto followDto,
                                          @Context SecurityContext securityContext) {
        Optional<User> userOpt = userService.findUserByUsername(securityContext.getUserPrincipal().getName());
        if (!userOpt.isPresent())
            return Response.serverError().build();
        tagService.followTag(userOpt.get().getId(), tagId);
        return Response.ok(followDto).build();
    }

    @POST
    @Path("/{tagId}/unfollow")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response unfollowTag(@PathParam(value="tagId") final long tagId,
                                          FollowDto followDto) {
        Optional<User> userOpt = userService.findUserByUsername(securityContext.getUserPrincipal().getName());
        if (!userOpt.isPresent())
            return Response.serverError().build();
        tagService.unfollowTag(userOpt.get().getId(), tagId);
        return Response.ok(followDto).build();
    }

    @POST
    @Path("/search")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response searchTags(@QueryParam("page") @DefaultValue("1") int page,
                               @QueryParam("showEmpty") @DefaultValue("true") boolean showEmpty,
                               @QueryParam("showOnlyFollowing") @DefaultValue("false") boolean showOnlyFollowing,
                               final ItemSearchDto itemSearchDto) {
        // Find the user, check if it exists
        Long userId = null;
        Optional<User> userOpt;
        if (showOnlyFollowing && securityContext.getUserPrincipal() != null) {
            userOpt = userService.findUserByUsername(securityContext.getUserPrincipal().getName());
            if (!userOpt.isPresent())
                return Response.serverError().build();
            userId = userOpt.get().getId();
        }
        else showOnlyFollowing = false;

        List<TagDto> tags = tagService.findTagsByName(itemSearchDto.getName(), showEmpty, showOnlyFollowing, userId, page, TAG_PAGE_SIZE)
                .stream()
                .map(TagDto::fromTag)
                .collect(Collectors.toList());
        int tagsCount = this.tagService.getAllTagsCountByName(itemSearchDto.getName(), showEmpty, showOnlyFollowing, userId);
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

    @DELETE
    @Path("/{tagId}/delete")
    public Response changeTagFollowStatus(@PathParam(value="tagId") long tagId) {
        this.tagService.removeTag(tagId);
        return Response.noContent().build();
    }
}
