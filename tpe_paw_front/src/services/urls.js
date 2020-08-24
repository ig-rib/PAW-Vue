const localDomain = 'http://localhost:8080/'

const urls = {
  localDomain,
  user: {
    current: localDomain + 'user/current',
    activeSnippets: localDomain + 'user/:id/snippets/active',
    deletedSnippets: localDomain + 'user/:id/snippets/deleted',
    upvotedSnippets: localDomain + 'user/upvoted',
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
    resetPassword: localDomain + 'reset-password',
  },
  tags: {
    searchTags: localDomain + 'tags',
    getTagSnippets: localDomain + 'tags/:id/snippets',
    followTag: localDomain + 'tags/:id/follow',
    unfollowTag: localDomain + 'tags/:id/unfollow',
    deleteTag: localDomain + 'tags/:id/delete'
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
    getSnippet: localDomain + 'snippet/:id',
    deleteSnippet: localDomain + 'snippet/:id/delete',
    voteSnippet: localDomain + 'snippet/:id/vote', // Pass isPositive in body
    favSnippet: localDomain + 'snippet/:id/fav', // For both faving and unfaving
    reportSnippet: localDomain + 'snippet/:id/report',
    flagSnippet: localDomain + 'snippet/:id/flag',
    create: localDomain + 'snippet/create'
  },
  languages: {
    searchLanguages: localDomain + 'languages',
    getLanguageSnippets: localDomain + 'languages/:id/snippets',
    deleteLanguage: localDomain + 'languages/:id/delete'
  },
  test: {
    jerseyTest: localDomain + 'jersey-test'
  }
}

export default urls
