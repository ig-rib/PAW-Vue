const localDomain = 'http://localhost:8080/'

const urls = {
  localDomain,
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
    register: localDomain + 'registration/register',
    verifyEmail: localDomain + 'registration/verify-email',
    resendEmailVerification: localDomain + 'registration/resend-email-verification',
    sendRecoveryEmail: localDomain + 'registration/send-recovery-email',
    resetPassword: localDomain + 'registration/reset-password',
    usernameExists: localDomain + 'registration/username-exists',
    emailExists: localDomain + 'registration/email-exists'

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
    voteSnippet: localDomain + 'snippet/:id/vote', // Pass isPositive in body
    favSnippet: localDomain + 'snippet/:id/favorite', // For both faving and unfaving
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
}

export default urls
