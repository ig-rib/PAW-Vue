const localDomain = 'http://localhost:8080/'

const urls = {
  localDomain,
  user: {
    getActiveUserSnippets: localDomain + 'user/:id/active',
    getDeletedUserSnippets: localDomain + 'user/:id/deleted',
    getUser: localDomain + 'user/:id',
    uploadProfilePhoto: localDomain + 'user/:id/profile-photo',
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
    getTags: localDomain + 'tags',
    searchTags: localDomain + 'tags/search',
    getTagSnippets: localDomain + 'tags/:tagId',
    followTag: localDomain + 'tags/:tagId/follow',
    unfollowTag: localDomain + 'tags/:tagId/unfollow',
    deleteTag: localDomain + 'tags/:tagId/delete'
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
    search: localDomain + 'explore/search'
  },
  snippet: {
    getSnippet: localDomain + 'snippet/:id',
    deleteSnippet: localDomain + 'snippet/:id/delete',
    voteSnippet: localDomain + 'snippet/:id/vote', // Pass isPositive in body
    favSnippet: localDomain + 'snippet/:id/fav', // For both faving and unfaving
    reportSnippet: localDomain + 'snippet/:id/report',
    flagSnippet: localDomain + 'snippet/:id/flag',
    create: localDomain + 'snippet/create'
  },
  languages: {
    getLanguages: localDomain + 'languages',
    searchLanguages: localDomain + 'languages/search',
    getLanguage: localDomain + 'languages/:id',
    deleteLanguage: localDomain + 'languages/:id/delete'
  },
  test: {
    jerseyTest: localDomain + 'jersey-test'
  }
}

export default urls
