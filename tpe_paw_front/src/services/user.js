import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getUser = (id) => axiosFetcher.get(urls.user.getUser, {
  pathVariables: {
    id
  }
})

const getActiveUserSnippets = (id, page) => axiosFetcher.get(urls.user.getActiveUserSnippets, {
  queryParams: {
    page
  },
  pathVariables: {
    id
  }
})

const getDeletedUserSnippets = (id, page) => axiosFetcher.get(urls.user.getDeletedUserSnippets, {
  queryParams: {
    page
  },
  pathVariables: {
    id
  }
})
const uploadProfilePhoto = (id, encodedPhoto) => axiosFetcher.put(urls.user.uploadProfilePhoto, {
  pathVariables: {
    id
  }
}, {
  encodedPhoto
})

export default {
  getUser,
  getActiveUserSnippets,
  getDeletedUserSnippets,
  uploadProfilePhoto
}
