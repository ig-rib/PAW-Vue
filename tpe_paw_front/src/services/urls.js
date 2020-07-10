const localDomain = 'http://localhost:8080/webapp_war_exploded/'

const urls = {
  user: {
    active: localDomain + 'user/:id/active',
    deleted: localDomain + 'user/:id/deleted',
    userProfile: localDomain + 'user/:id',
    endEditPhoto: localDomain + 'user/:id/:context',
    userImage: localDomain + 'user/:id/image',
    endEditUserProfile: localDomain + 'user/:id/:context/edit'
  },
  admin: {
    add: localDomain + 'admin/add'
  },
  registration: {
    login: localDomain + 'login',
    verifyEmail: localDomain + 'verify-email',
    resendEmailVerification: localDomain + 'resend-email-verification'
  },
  tags: {
    getTags: localDomain + 'tags',
    searchTags: localDomain + 'tags/search',
    getTagSnippets: localDomain + 'tags/:tagId',
    followTag: localDomain + 'tags/:tagId/follow',
    deleteTag: localDomain + 'tags/:tagId/delete'
  },
  // favorites: {
  //   // For feed (could move to user)
  //   favorites: localDomain + 'favorites',
  //   searchFollowing:
  // },
  // following: {
  //   following: localDomain + 'following'
  // },
  // upvoted: {
  //   upvoted: localDomain + 'upvoted'
  // },
  // flagged: {
  //   flagged: localDomain + 'flagged',
  // },
  search: {
    search: localDomain + 'search' // context should be supplied in body
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
  registration: {
    login: localDomain + 'registration/login'
  },
  test: {
    jerseyTest: localDomain + 'jersey-test'
  }
}

export default urls
