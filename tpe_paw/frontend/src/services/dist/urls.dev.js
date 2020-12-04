"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var localDomain = 'http://localhost:8080/';
var urls = {
  localDomain: localDomain,
  user: {
    current: localDomain + 'user/current',
    activeSnippets: localDomain + 'user/:id/snippets/active',
    deletedSnippets: localDomain + 'user/:id/snippets/deleted',
    upvotedSnippets: localDomain + 'user/snippets/upvoted',
    followingSnippets: localDomain + 'user/snippets/following',
    favoriteSnippets: localDomain + 'user/snippets/favorites',
    getUser: localDomain + 'user/:id',
    profilePhoto: localDomain + 'user/:id/profile-photo',
    profilePhoto64: localDomain + 'user/:id/profile-photo64',
    updateUserData: localDomain + 'user/:id/user-data'
  },
  admin: {
    add: localDomain + 'admin/add'
  },
  registration: {
    login: localDomain + 'login',
    register: localDomain + 'register',
    verifyEmail: localDomain + 'verify-email',
    resendEmailVerification: localDomain + 'resend-email-verification',
    sendRecoveryEmail: localDomain + 'send-recovery-email',
    resetPassword: localDomain + 'reset-password'
  },
  tags: {
    tags: localDomain + 'tags',
    tag: localDomain + 'tags/:id',
    getTagSnippets: localDomain + 'tags/:id/snippets',
    followTag: localDomain + 'tags/:id/follow',
    unfollowTag: localDomain + 'tags/:id/unfollow'
  },
  search: {
    searchHome: localDomain + 'search',
    searchFavorites: localDomain + 'favorites/search',
    searchFollowing: localDomain + 'following/search',
    searchUpvoted: localDomain + 'upvoted/search',
    searchFlagged: localDomain + 'flagged/search',
    searchInLanguage: localDomain + 'langugages/:id/search',
    searchInTag: localDomain + 'tags/:id/search',
    searchInActiveUserSnippets: localDomain + 'user/:id/active/search',
    searchInDeletedUserSnippets: localDomain + 'user/:id/deleted/search'
  },
  explore: {
    search: localDomain + 'explore'
  },
  snippet: {
    snippets: localDomain + 'snippet',
    snippet: localDomain + 'snippet/:id',
    restore: localDomain + 'snippet/:id/restore',
    voteSnippet: localDomain + 'snippet/:id/vote',
    // Pass isPositive in body
    favSnippet: localDomain + 'snippet/:id/favorite',
    // For both faving and unfaving
    reportSnippet: localDomain + 'snippet/:id/report',
    flagSnippet: localDomain + 'snippet/:id/flag',
    create: localDomain + 'snippet',
    flagged: localDomain + 'snippet/flagged'
  },
  languages: {
    languages: localDomain + 'languages',
    language: localDomain + 'languages/:id',
    getLanguageSnippets: localDomain + 'languages/:id/snippets'
  },
  test: {
    jerseyTest: localDomain + 'jersey-test'
  }
};
var _default = urls;
exports["default"] = _default;