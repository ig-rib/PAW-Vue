import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getUser = (id) => axiosFetcher.get(urls.user.getUser, {
  pathVariables: {
    id
  }
})

const getUserSnippets = (page, id) => axiosFetcher.get(urls.user.getUserSnippets, {
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
  getUserSnippets,
  uploadProfilePhoto
}
