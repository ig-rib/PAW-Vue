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

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.*;

// TODO Modularize...

@Path("/")
public class SearchController {

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
    @Autowired
    private LoginAuthentication loginAuthentication;
    @Autowired
    private TagService tagService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private UserService userService;
    @Context
    private UriInfo uriInfo;

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
    private static final String HOME = "";
    private static final String FOLLOWING = "following/";
    private static final String FAVORITES = "favorites/";
    private static final String UPVOTED = "upvoted/";
    private static final String FLAGGED = "flagged/";
    private static final String LANGUAGES = "languages/";
    private static final String TAGS = "tags/";
    private static final String USER = "user/";

    // GET version of endopints

    @GET
    @Path("feed/search")
    public Response searchInHome(final @QueryParam("q") String query,
                                               final @QueryParam("t") String type,
                                               final @QueryParam("uid") String userId,
                                               final @QueryParam("s") String sort,
                                               final @QueryParam("page") @DefaultValue("1") int page) {
        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.HOME, sort, null, null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.HOME, null, null);

        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    @GET
    @Path("/favorites/search")
    public Response searchInFavorites(final @QueryParam("q") String query,
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
        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.HOME, sort, user.getId(), null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.FAVORITES, user.getId(), null);
        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    @GET
    @Path("/following/search")
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
        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.FOLLOWING, sort, user.getId(), null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.FOLLOWING, user.getId(), null);
        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    @GET
    @Path("/upvoted/search")
    public Response searchInUpvoted(final @QueryParam("q") String query,
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
        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.UPVOTED, sort, user.getId(), null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.UPVOTED, user.getId(), null);
        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    @GET
    @Path("/flagged/search")
    public Response searchInFlagged(final @QueryParam("q") String query,
                                               final @QueryParam("t") String type,
                                               final @QueryParam("uid") String userId,
                                               final @QueryParam("s") String sort,
                                               final @QueryParam("page") @DefaultValue("1") int page) {

        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.FLAGGED, sort, null, null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.FLAGGED, null, null);
        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    @GET
    @Path("/languages/{langId}/search")
    public Response searchInLanguage(final @QueryParam("q") String query,
                                               final @QueryParam("t") String type,
                                               final @QueryParam("uid") String userId,
                                               final @QueryParam("s") String sort,
                                               final @QueryParam("page") @DefaultValue("1") int page,
                                     final @PathParam(value = "langId") long langId) {

        Language language = this.languageService.findById(langId).orElse(null);
        if (language == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.language", new Object[]{langId}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.LANGUAGES, sort, null, langId, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.LANGUAGES, null, langId);
        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    @GET
    @Path("/tags/{tagId}/search")
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
        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.TAGS, sort, null, tagId, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.TAGS, null, tagId);
        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    @GET
    @Path("/user/{id}/active/search")
    public Response searchInActiveUserSnippets(final @QueryParam("q") String query,
                                               final @QueryParam("t") String type,
                                               final @QueryParam("uid") String userId,
                                               final @QueryParam("s") String sort,
                                               final @QueryParam("page") @DefaultValue("1") int page,
                                               final @PathParam(value = "id") long id) {
        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.USER, sort, id, null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.USER, id, null);
        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    @GET
    @Path("/user/{id}/deleted/search")
    public Response searchInDeletedUserSnippets(final @QueryParam("q") String query,
                                               final @QueryParam("t") String type,
                                               final @QueryParam("uid") String userId,
                                               final @QueryParam("s") String sort, 
                                               final @QueryParam("page") @DefaultValue("1") int page,
                                                final @PathParam(value = "id") long id) {
        User user = loginAuthentication.getLoggedInUser();
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User currentUser = loginAuthentication.getLoggedInUser();
        if (currentUser == null || !user.equals(currentUser)) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.403.profile.owner", new Object[]{loginAuthentication.getLoggedInUsername()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.FORBIDDEN).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(type, query, SnippetDao.Locations.DELETED, sort, id, null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(type, query, SnippetDao.Locations.DELETED, id, null);
        return generateResponseWithLinks(page, snippets, totalSnippetCount);
    }

    private Collection<Snippet> findByCriteria(String type, String query, SnippetDao.Locations location, String sort, Long userId, Long resourceId, int page) {
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

    private Response generateResponseWithLinks(@DefaultValue("1") @QueryParam("page") int page, List<SnippetDto> snippets, int totalSnippetCount) {
        int pageCount = (totalSnippetCount/SNIPPET_PAGE_SIZE) + ((totalSnippetCount % SNIPPET_PAGE_SIZE == 0) ? 0 : 1);

        Response.ResponseBuilder respBuilder = Response.ok(new GenericEntity<List<SnippetDto>>(snippets) {})
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page",pageCount).build(), "last");
        if (page > 1)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page-1).build(), "prev");
        if (page < pageCount)
            respBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page+1).build(), "next");

        return respBuilder.build();
    }

    private int getSnippetByCriteriaCount(String type, String query, SnippetDao.Locations location, Long userId, Long resourceId) {
        return this.snippetService.getSnippetByCriteriaCount(
                typesMap.getOrDefault(type, SnippetDao.Types.ALL),
                query == null ? "" : query,
                location,
                userId,
                resourceId);
    }
}
