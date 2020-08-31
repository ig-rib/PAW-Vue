package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.ErrorMessageDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.*;

// TODO Modularize...

@Component
public class SearchHelper {

    private final static Map<String, SnippetDao.Types> typesMap;
    static {
        final Map<String, SnippetDao.Types> types = new HashMap<>();
//        types.put(null, SnippetDao.Types.ALL);
        types.put("all", SnippetDao.Types.ALL);
        types.put("tag", SnippetDao.Types.TAG);
        types.put("title", SnippetDao.Types.TITLE);
        types.put("content", SnippetDao.Types.CONTENT);
        types.put("username", SnippetDao.Types.USER);
        types.put("language", SnippetDao.Types.LANGUAGE);
        typesMap = Collections.unmodifiableMap(types);
    }

    private final static Map<String, SnippetDao.Orders> ordersMap;
    static {
        final Map<String, SnippetDao.Orders> orders = new HashMap<>();
//        orders.put(null, SnippetDao.Orders.NO);
        orders.put("asc", SnippetDao.Orders.ASC);
        orders.put("desc", SnippetDao.Orders.DESC);
        orders.put("no", SnippetDao.Orders.NO);
        ordersMap = Collections.unmodifiableMap(orders);
    }

    @Autowired
    private SnippetService snippetService;

    public Collection<Snippet> findByCriteria(String type, String query, SnippetDao.Locations location, String sort, Long userId, Long resourceId, int page) {
//        if (!typesMap.containsKey(type) || !ordersMap.containsKey(sort)) {
//            return Collections.emptyList();
//        } else {
            return this.snippetService.findSnippetByCriteria(
                    typesMap.getOrDefault(type, SnippetDao.Types.ALL),
                    query == null ? "" : query,
                    location,
                    ordersMap.getOrDefault(sort, SnippetDao.Orders.NO),
                    userId,
                    resourceId,
                    page,
                    SNIPPET_PAGE_SIZE);
//        }
    }

    public Response generateResponseWithLinks(Request request, int page, Map<String, Object> queryParams, List<SnippetDto> snippets, int totalSnippetCount, UriInfo uriInfo) {
        int pageCount = (totalSnippetCount/SNIPPET_PAGE_SIZE) + ((totalSnippetCount % SNIPPET_PAGE_SIZE == 0) ? 0 : 1);

        UriBuilder basePath = uriInfo.getAbsolutePathBuilder();
        queryParams.forEach((key, value) -> {
            if (value != null) {
                basePath.queryParam(key, value);
            }
        });

//        CacheControl cc = new CacheControl();
//        List<Object> list = new ArrayList<>(queryParams.values());
//        list.add(snippets);
//        EntityTag eTag = new EntityTag(Integer.toString(Objects.hash(list.toArray())));
//        Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
//
//        if (builder == null) {
//            builder = Response.ok(new GenericEntity<List<SnippetDto>>(snippets) {});
//            builder.tag(eTag);
//        }

        Response.ResponseBuilder builder = Response.ok(new GenericEntity<List<SnippetDto>>(snippets) {});

        builder.link(UriBuilder.fromUri(basePath.build()).queryParam("page", 1).build(), "first")
                .link(UriBuilder.fromUri(basePath.build()).queryParam("page",pageCount).build(), "last");

        if (page > 1)
            builder.link(UriBuilder.fromUri(basePath.build()).queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            builder.link(UriBuilder.fromUri(basePath.build()).queryParam("page", page+1).build(), "next");

        return builder
//                .cacheControl(cc)
                .build();
    }

    public int getSnippetByCriteriaCount(String type, String query, SnippetDao.Locations location, Long userId, Long resourceId) {
        return this.snippetService.getSnippetByCriteriaCount(
                typesMap.getOrDefault(type, SnippetDao.Types.ALL),
                query == null ? "" : query,
                location,
                userId,
                resourceId);
    }
}
