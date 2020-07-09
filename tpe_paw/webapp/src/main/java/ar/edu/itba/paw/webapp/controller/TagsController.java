package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.webapp.dto.TagDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.TAG_PAGE_SIZE;

@Path("/tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllTags(@QueryParam("page") @DefaultValue("1") int page) {

        // TODO add showEmpty and showOnlyFollowing support

        final List<TagDto> tags = tagService.getAllTags(true, false, null, page, TAG_PAGE_SIZE)
                .stream()
                .map(tag -> TagDto.fromTag(tag))
                .collect(Collectors.toList());

        int tagsCount = tagService.getAllTagsCount(true, false, null);
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
}
