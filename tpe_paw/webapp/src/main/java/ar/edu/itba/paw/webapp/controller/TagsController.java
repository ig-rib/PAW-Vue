package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.SnippetService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.webapp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
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

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllTags(@QueryParam("page") @DefaultValue("1") int page) {

        // TODO add showEmpty and showOnlyFollowing support

        final List<TagDto> tags = tagService.getAllTags(true, false, null, page, TAG_PAGE_SIZE)
                .stream()
                .map(TagDto::fromTag)
                .collect(Collectors.toList());

        int tagsCount = tagService.getAllTagsCount(true, false, null);
        int pageCount = (tagsCount/TAG_PAGE_SIZE) + ((tagsCount % TAG_PAGE_SIZE == 0) ? 0 : 1);

        Response.ResponseBuilder respBuilder = Response.ok(new GenericEntity<List<TagDto>>(tags) {})
                .header("Access-Control-Allow-Origin", "*")
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
                .header("Access-Control-Allow-Origin", "*")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).build(), "last");
        if (page > 1)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).build(), "next");
        return respBuilder.build();
    }

    @POST
//    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    @Path("/{tagId}/follow")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response changeTagFollowStatus(final FollowDto followDto) {
        Boolean follows = followDto.getFollow();
        return Response.ok(followDto).build();
    }

    @POST
    @Path("/search")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response searchTags(@QueryParam("page") @DefaultValue("1") int page, final ItemSearchDto itemSearchDto) {
        List<TagDto> tags = tagService.findTagsByName(itemSearchDto.getName(), itemSearchDto.isShowEmpty(), itemSearchDto.isShowOnlyFollowing(), null, page, TAG_PAGE_SIZE)
                .stream()
                .map(TagDto::fromTag)
                .collect(Collectors.toList());
        int tagsCount = this.tagService.getAllTagsCountByName(itemSearchDto.getName(), itemSearchDto.isShowEmpty(), itemSearchDto.isShowOnlyFollowing(), null);
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Path("/{tagId}/delete")
    public Response changeTagFollowStatus(@PathParam(value="tagId") long tagId,
                                          @Context SecurityContext securityContext1) {
        // this.tagService.removeTag(tagId);
        Principal userPrincipal = securityContext1.getUserPrincipal();
        String username = userPrincipal.getName();
        return Response.noContent().build();
    }
}
