import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getUser = (id) => axiosFetcher.get(urls.user.getUser, {
  pathVariables: {
    id
  }
})

const getProfilePhoto = (id) => axiosFetcher.get(urls.user.profilePhoto, {
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
const uploadProfilePhoto = (id, photo) => axiosFetcher.post(urls.user.profilePhoto, {
  pathVariables: {
    id
  }
},
  photo,
  {
    'Content-Type' : 'multipart/form-data'
  })

const updateUserData = (id, description, username) => axiosFetcher.put(urls.user.updateUserData, {
  pathVariables: {
    id
  }
}, {
  description
  // username
})

export default {
  getUser,
  getActiveUserSnippets,
  getDeletedUserSnippets,
  uploadProfilePhoto,
  updateUserData,
  getProfilePhoto
}
