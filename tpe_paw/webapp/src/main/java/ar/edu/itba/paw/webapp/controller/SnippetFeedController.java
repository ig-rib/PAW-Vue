package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.RoleService;
import ar.edu.itba.paw.interfaces.service.SnippetService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.LoginAuthentication;
import ar.edu.itba.paw.webapp.constants.Constants;
import ar.edu.itba.paw.webapp.exception.ForbiddenAccessException;
import ar.edu.itba.paw.webapp.form.FavoriteForm;
import ar.edu.itba.paw.webapp.form.FollowForm;
import ar.edu.itba.paw.webapp.form.SearchForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static ar.edu.itba.paw.webapp.constants.Constants.SNIPPET_PAGE_SIZE;

@Controller
public class SnippetFeedController {

    @Autowired private SnippetService snippetService;
    @Autowired private LoginAuthentication loginAuthentication;
    @Autowired private TagService tagService;
    @Autowired private RoleService roleService;
    @Autowired private UserService userService;
    @Autowired private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(SnippetFeedController.class);
    private static final String HOME = "";
    private static final String FOLLOWING = "following/";
    private static final String FAVORITES = "favorites/";
    private static final String UPVOTED = "upvoted/";
    private static final String FLAGGED = "flagged/";

    @RequestMapping("/")
    public ModelAndView getHomeSnippetFeed(final @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        final ModelAndView mav = new ModelAndView("index");

        User currentUser = this.loginAuthentication.getLoggedInUser();
        Collection<Snippet> snippets = this.snippetService.getAllSnippets(page, SNIPPET_PAGE_SIZE);
        int totalSnippetCount = this.snippetService.getAllSnippetsCount();

        this.addModelAttributesHelper(mav, currentUser, totalSnippetCount, page, snippets, HOME);

        return mav;
    }

    @RequestMapping("/favorites")
    public ModelAndView getFavoritesSnippetFeed(final @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        final ModelAndView mav = new ModelAndView("index");

        User currentUser = this.loginAuthentication.getLoggedInUser();
        if (currentUser == null) this.logAndThrow(FAVORITES);

        Collection<Snippet> snippets = this.snippetService.getAllFavoriteSnippets(currentUser.getId(), page, SNIPPET_PAGE_SIZE);
        int totalSnippetCount = this.snippetService.getAllFavoriteSnippetsCount(currentUser.getId());

        this.addModelAttributesHelper(mav, currentUser, totalSnippetCount, page, snippets, FAVORITES);

        return mav;
    }

    @RequestMapping("/following")
    public ModelAndView getFollowingSnippetFeed(final @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        final ModelAndView mav = new ModelAndView("snippet/snippetFollowing");

        User currentUser = this.loginAuthentication.getLoggedInUser();
        if (currentUser == null) this.logAndThrow(FOLLOWING);

        Collection<Snippet> snippets = this.snippetService.getAllFollowingSnippets(currentUser.getId(), page, SNIPPET_PAGE_SIZE);
        int totalSnippetCount = this.snippetService.getAllFollowingSnippetsCount(currentUser.getId());
        this.addModelAttributesHelper(mav, currentUser, totalSnippetCount, page, snippets, FOLLOWING);

        /* Show up to 25 tags -> most popular + non empty */
        Collection<Tag> followingTags = this.tagService.getMostPopularFollowedTagsForUser(currentUser.getId(), Constants.FOLLOWING_FEED_TAG_AMOUNT);
        for (Tag tag : followingTags) {
            FollowForm followForm = new FollowForm();
            followForm.setFollows(true);
            mav.addObject("unfollowForm" + tag.getId().toString(), followForm);
        }
        mav.addObject("followingTags", followingTags);

        return mav;
    }

    @RequestMapping("/upvoted")
    public ModelAndView getUpVotedSnippetFeed(final @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        final ModelAndView mav = new ModelAndView("index");

        User currentUser = this.loginAuthentication.getLoggedInUser();
        if (currentUser == null) this.logAndThrow(UPVOTED);

        Collection<Snippet> snippets = this.snippetService.getAllUpVotedSnippets(currentUser.getId(), page, SNIPPET_PAGE_SIZE);
        int totalSnippetCount = this.snippetService.getAllUpvotedSnippetsCount(currentUser.getId());

        this.addModelAttributesHelper(mav, currentUser, totalSnippetCount, page, snippets, UPVOTED);

        return mav;
    }

    @RequestMapping("/flagged")
    public ModelAndView getFlaggedSnippetFeed(final @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        final ModelAndView mav = new ModelAndView("index");

        User currentUser = this.loginAuthentication.getLoggedInUser();
        if (currentUser == null || !roleService.isAdmin(currentUser.getId())) this.logAndThrow(FLAGGED);

        Collection<Snippet> snippets = this.snippetService.getAllFlaggedSnippets(page, SNIPPET_PAGE_SIZE);
        int totalSnippetCount = this.snippetService.getAllFlaggedSnippetsCount();

        this.addModelAttributesHelper(mav, currentUser, totalSnippetCount, page, snippets, FLAGGED);

        return mav;
    }

    private void addModelAttributesHelper(ModelAndView mav, User currentUser, int snippetCount, int page, Collection<Snippet> snippets, String searchContext) {
        mav.addObject("pages", snippetCount/SNIPPET_PAGE_SIZE + (snippetCount % SNIPPET_PAGE_SIZE == 0 ? 0 : 1));
        mav.addObject("page", page);
        mav.addObject("snippetList", snippets);
        mav.addObject("totalSnippetCount", snippetCount);
        mav.addObject("searchContext",searchContext);
        mav.addObject("searching", false);

        for (Snippet snippet : snippets) {
            if (currentUser != null) {
                /* Fav form quick action */
                FavoriteForm favForm = new FavoriteForm();
                favForm.setFavorite(currentUser.getFavorites().contains(snippet));
                mav.addObject("favoriteForm" + snippet.getId().toString(), favForm);
            }
        }
    }

    @ModelAttribute
    public void addAttributes(Model model, @Valid final SearchForm searchForm) {
        User currentUser = this.loginAuthentication.getLoggedInUser();
        Collection<Tag> userTags =  Collections.emptyList();
        Collection<String> userRoles = Collections.emptyList();
        Collection<Tag> allFollowedTags = Collections.emptyList();

        if (currentUser != null) {
            userTags = this.tagService.getMostPopularFollowedTagsForUser(currentUser.getId(), Constants.MENU_FOLLOWING_TAG_AMOUNT);
            userRoles = this.roleService.getUserRoles(currentUser.getId());
            this.userService.updateLocale(currentUser.getId(), LocaleContextHolder.getLocale());
            allFollowedTags = this.tagService.getFollowedTagsForUser(currentUser.getId());
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userTags", userTags);
        model.addAttribute("userTagsCount", userTags.isEmpty() ? 0 : allFollowedTags.size() - userTags.size() );
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("userRoles", userRoles);
    }

    private void logAndThrow(String location) {
        LOGGER.warn("Inside {} with no logged in user", location);
        throw new ForbiddenAccessException(messageSource.getMessage("error.403", new Object[]{location}, LocaleContextHolder.getLocale()));
    }
}
