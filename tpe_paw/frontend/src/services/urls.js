// const localDomain = window.location.protocol + '//' + window.location.host + '/';
// const localDomain = 'http://localhost:8080/'
// TODO: change before last deploy
const localDomain = 'http://pawserver.it.itba.edu.ar/paw-2020a-2/';
const localDomainApi = localDomain + 'api/'

const urls = {
  localDomain,
  user: {
    current: localDomainApi + 'user/current',
    activeSnippets: localDomainApi + 'user/:id/snippets/active',
    deletedSnippets: localDomainApi + 'user/:id/snippets/deleted',
    upvotedSnippets: localDomainApi + 'user/snippets/upvoted',
    followingSnippets: localDomainApi + 'user/snippets/following',
    favoriteSnippets: localDomainApi + 'user/snippets/favorites',
    getUser: localDomainApi + 'user/:id',
    profilePhoto: localDomainApi + 'user/:id/profile-photo',
    profilePhoto64: localDomainApi + 'user/:id/profile-photo64',
    updateUserData: localDomainApi + 'user/:id/user-data'
  },
  admin: {
    add: localDomainApi + 'admin/add'
  },
  registration: {
    login: localDomain + 'login',
    register: localDomainApi + 'registration/register',
    verifyEmail: localDomainApi + 'registration/verify-email',
    resendEmailVerification: localDomainApi + 'registration/resend-email-verification',
    sendRecoveryEmail: localDomainApi + 'registration/send-recovery-email',
    resetPassword: localDomainApi + 'registration/reset-password',
    usernameExists: localDomainApi + 'registration/username-exists',
    emailExists: localDomainApi + 'registration/email-exists'

  },
  tags: {
    tags: localDomainApi + 'tags',
    tag: localDomainApi + 'tags/:id',
    getTagSnippets: localDomainApi + 'tags/:id/snippets',
    followTag: localDomainApi + 'tags/:id/follow',
    unfollowTag: localDomainApi + 'tags/:id/unfollow'
  },
  search: {
    searchHome: localDomainApi + 'search',
    searchFavorites: localDomainApi + 'favorites/search',
    searchFollowing: localDomainApi + 'following/search',
    searchUpvoted: localDomainApi + 'upvoted/search',
    searchFlagged: localDomainApi + 'flagged/search',
    searchInLanguage: localDomainApi + 'langugages/:id/search',
    searchInTag: localDomainApi + 'tags/:id/search',
    searchInActiveUserSnippets: localDomainApi + 'user/:id/active/search',
    searchInDeletedUserSnippets: localDomainApi + 'user/:id/deleted/search'
  },
  explore: {
    search: localDomainApi + 'explore'
  },
  snippet: {
    snippets: localDomainApi + 'snippet',
    snippet: localDomainApi + 'snippet/:id',
    restore: localDomainApi + 'snippet/:id/restore',
    voteSnippet: localDomainApi + 'snippet/:id/vote', // Pass isPositive in body
    favSnippet: localDomainApi + 'snippet/:id/favorite', // For both faving and unfaving
    reportSnippet: localDomainApi + 'snippet/:id/report',
    flagSnippet: localDomainApi + 'snippet/:id/flag',
    create: localDomainApi + 'snippet',
    flagged: localDomainApi + 'snippet/flagged'
  },
  languages: {
    languages: localDomainApi + 'languages',
    language: localDomainApi + 'languages/:id',
    getLanguageSnippets: localDomainApi + 'languages/:id/snippets'
  },
  test: {
    jerseyTest: localDomainApi + 'jersey-test'
  }
}

export default urls
