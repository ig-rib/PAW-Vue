package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import ar.edu.itba.paw.webapp.form.ExploreForm;
import ar.edu.itba.paw.webapp.form.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.SNIPPET_PAGE_SIZE;

@Path("/explore")
public class SnippetExploreController {

    @Autowired
    private SnippetService snippetService;
    @Autowired private LoginAuthentication loginAuthentication;
    @Autowired private TagService tagService;
    @Autowired private LanguageService languageService;
    @Autowired private RoleService roleService;
    @Autowired private UserService userService;
    @Context private UriInfo uriInfo;

    private final static Map<String, SnippetDao.Types> typesMap;
    static {
        final Map<String, SnippetDao.Types> types = new HashMap<>();
        types.put(null, SnippetDao.Types.TITLE);
        types.put("reputation", SnippetDao.Types.REPUTATION);
        types.put("votes", SnippetDao.Types.VOTES);
        types.put("date", SnippetDao.Types.DATE);
        types.put("title", SnippetDao.Types.TITLE);
        typesMap = Collections.unmodifiableMap(types);
    }

    private final static Map<String, SnippetDao.Orders> ordersMap;
    static {
        final Map<String, SnippetDao.Orders> orders = new HashMap<>();
        orders.put("asc", SnippetDao.Orders.ASC);
        orders.put("desc", SnippetDao.Orders.DESC);
        orders.put("no", SnippetDao.Orders.NO);
        ordersMap = Collections.unmodifiableMap(orders);
    }

    @GET
    @Path("/search")
    public Response exploreSearch(    final @QueryParam("t") String type,
                                      final @QueryParam("s") @DefaultValue("no") String sort,
                                      final @QueryParam("page") @DefaultValue("1") int page,
                                      final @QueryParam("minDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date minDate,
                                      final @QueryParam("maxDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date maxDate,
                                      final @QueryParam("minRep") Integer minRep,
                                      final @QueryParam("maxRep") Integer maxRep,
                                      final @QueryParam("minVotes") Integer minVotes,
                                      final @QueryParam("maxVotes") Integer maxVotes,
                                      final @QueryParam("langId") @DefaultValue("-1") Long langId,
                                      final @QueryParam("tagId") @DefaultValue("-1") Long tagId,
                                      final @QueryParam("uname") String username,
                                      final @QueryParam("title") String title,
                                      final @QueryParam("field") String field,
                                      final @QueryParam("includeFlagged") @DefaultValue("false") Boolean includeFlagged) {

        Instant minInstant = null;
        Instant maxInstant = null;
        if (minDate != null){
            minInstant = minDate.toInstant();
        }
        if (maxDate != null){
            maxInstant = maxDate.toInstant();
        }
        List<SnippetDto> snippets = this.snippetService.findSnippetByDeepCriteria(
                minInstant, maxInstant,
                minRep, maxRep,
                minVotes, maxVotes,
                langId, tagId,
                title, username,
                ordersMap.get(sort), typesMap.get(field), includeFlagged, page, SNIPPET_PAGE_SIZE)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int snippetCount =  this.snippetService.getSnippetByDeepCriteriaCount(
                minInstant, maxInstant,
                minRep, maxRep,
                minVotes, maxVotes,
                langId, tagId,
                title, username,
                includeFlagged);
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
}
