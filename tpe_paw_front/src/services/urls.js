const localDomain = 'http://localhost:8080/'

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
  test: {
    jerseyTest: localDomain + 'jersey-test'
  }
}

export default urls
