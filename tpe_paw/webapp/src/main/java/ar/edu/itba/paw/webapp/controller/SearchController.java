package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.dto.ErrorMessageDto;
import ar.edu.itba.paw.webapp.dto.SearchDto;
import ar.edu.itba.paw.webapp.dto.SnippetDto;
import ar.edu.itba.paw.webapp.dto.TagDto;
import ar.edu.itba.paw.webapp.exception.ForbiddenAccessException;
import ar.edu.itba.paw.webapp.exception.URITooLongException;
import ar.edu.itba.paw.webapp.form.DescriptionForm;
import ar.edu.itba.paw.webapp.utility.Constants;
import ar.edu.itba.paw.webapp.utility.MavHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utility.Constants.*;
import static ar.edu.itba.paw.webapp.utility.Constants.TAG_PAGE_SIZE;

// TODO Modularize...

@Path("/")
public class SearchController {

    private final static Map<String, SnippetDao.Types> typesMap;
    static {
        final Map<String, SnippetDao.Types> types = new HashMap<>();
        types.put(null, SnippetDao.Types.ALL);
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
    @Context
    private SecurityContext securityContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(ar.edu.itba.paw.webapp.old_controller.SearchController.class);
    private static final String HOME = "";
    private static final String FOLLOWING = "following/";
    private static final String FAVORITES = "favorites/";
    private static final String UPVOTED = "upvoted/";
    private static final String FLAGGED = "flagged/";
    private static final String LANGUAGES = "languages/";
    private static final String TAGS = "tags/";
    private static final String USER = "user/";

    @POST
    @Path("/search")
    public Response searchInHome(final SearchDto searchDto, final @QueryParam("page") @DefaultValue("1") int page) {
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.HOME, searchDto.getSort(), null, null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.HOME, null, null);

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

    @POST
    @Path("/favorites/search")
    public Response searchInFavorites(final SearchDto searchDto, final @QueryParam("page") @DefaultValue("1") int page) {

        // User already logged in at this point, so no need to check if userPrincipal is null
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.HOME, searchDto.getSort(), user.getId(), null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
                int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.FAVORITES, user.getId(), null);
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

    @POST
    @Path("/following/search")
    public Response searchInFollowing(final SearchDto searchDto, final @QueryParam("page") @DefaultValue("1") int page) {

        // User already logged in at this point, so no need to check if userPrincipal is null
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.FOLLOWING, searchDto.getSort(), user.getId(), null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.FOLLOWING, user.getId(), null);
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

    @POST
    @Path("/upvoted/search")
    public Response searchInUpvoted(final SearchDto searchDto, final @QueryParam("page") @DefaultValue("1") int page) {

        // User already logged in at this point, so no need to check if userPrincipal is null
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.UPVOTED, searchDto.getSort(), user.getId(), null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.UPVOTED, user.getId(), null);
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

    @POST
    @Path("/flagged/search")
    public Response searchInFlagged(final SearchDto searchDto, final @QueryParam("page") @DefaultValue("1") int page) {

        // User already logged in at this point, so no need to check if userPrincipal is null
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.FLAGGED, searchDto.getSort(), null, null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.FLAGGED, null, null);
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

    @POST
    @Path("/languages/{langId}/search")
    public Response searchInLanguage(final SearchDto searchDto,
                                    final @QueryParam("page") @DefaultValue("1") int page,
                                    final @PathParam(value = "langId") long langId) {

        Language language = this.languageService.findById(langId).orElse(null);
        if (language == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.language", new Object[]{langId}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.LANGUAGES, searchDto.getSort(), null, langId, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.LANGUAGES, null, langId);
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

    @POST
    @Path("/tags/{tagId}/search")
    public Response searchInTag(final SearchDto searchDto,
                                    final @QueryParam("page") @DefaultValue("1") int page,
                                    final @PathParam(value = "tagId") long tagId) {

        Tag tag = this.tagService.findTagById(tagId).orElse(null);
        if (tag == null) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.tag", new Object[]{tagId}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.TAGS, searchDto.getSort(), null, tagId, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.TAGS, null, tagId);
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

    @POST
    @Path("/user/{id}/active/search")
    public Response searchInActiveUserSnippets(final SearchDto searchDto,
                                               final @QueryParam("page") @DefaultValue("1") int page,
                                               final @PathParam(value = "id") long id) {
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.USER, searchDto.getSort(), id, null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.USER, id, null);
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

    @POST
    @Path("/user/{id}/deleted/search")
    public Response searchInDeletedUserSnippets(final SearchDto searchDto,
                                                final @QueryParam("page") @DefaultValue("1") int page,
                                                final @PathParam(value = "id") long id) {
        User user = userService.findUserById(id).orElse(null);
        if (user == null){
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.404.user", new Object[]{securityContext.getUserPrincipal().getName()}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.NOT_FOUND).entity(errorMessageDto).build();
        }
        User currentUser = userService.findUserByUsername((securityContext.getUserPrincipal() != null) ? securityContext.getUserPrincipal().getName() : null).orElse(null);
        if (currentUser == null || !user.equals(currentUser)) {
            ErrorMessageDto errorMessageDto = new ErrorMessageDto();
            errorMessageDto.setMessage(messageSource.getMessage("error.403.profile.owner", new Object[]{(securityContext.getUserPrincipal() != null) ? securityContext.getUserPrincipal().getName() : null}, LocaleContextHolder.getLocale()));
            return Response.status(Response.Status.FORBIDDEN).entity(errorMessageDto).build();
        }
        List<SnippetDto> snippets = this.findByCriteria(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.DELETED, searchDto.getSort(), id, null, page)
                .stream()
                .map(SnippetDto::fromSnippet)
                .collect(Collectors.toList());
        int totalSnippetCount = this.getSnippetByCriteriaCount(searchDto.getType(), searchDto.getQuery(), SnippetDao.Locations.DELETED, id, null);
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

    private Collection<Snippet> findByCriteria(String type, String query, SnippetDao.Locations location, String sort, Long userId, Long resourceId, int page) {
        if (!typesMap.containsKey(type) || !ordersMap.containsKey(sort)) {
            return Collections.emptyList();
        } else {
            return this.snippetService.findSnippetByCriteria(
                    typesMap.get(type),
                    query == null ? "" : query,
                    location,
                    ordersMap.get(sort),
                    userId,
                    resourceId,
                    page,
                    SNIPPET_PAGE_SIZE);
        }
    }

    private int getSnippetByCriteriaCount(String type, String query, SnippetDao.Locations location, Long userId, Long resourceId) {
        return this.snippetService.getSnippetByCriteriaCount(
                typesMap.get(type),
                query == null ? "" : query,
                location,
                userId,
                resourceId);
    }
}
