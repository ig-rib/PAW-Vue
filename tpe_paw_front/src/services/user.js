import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getUser = (id) => axiosFetcher.get(urls.user.getUser, {
  pathVariables: {
    id
  }
})

const getLoggedInUser = () => axiosFetcher.get(urls.user.current)

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
    'Content-Type' : 'multipart/form-data;charset=utf-8'
  })

const uploadProfilePhoto64 = (id, photo64) => axiosFetcher.post(urls.user.profilePhoto64, {
  pathVariables: {
    id
  }
  },
  {
    encodedPhoto: photo64
  }
)

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
  getLoggedInUser,
  getActiveUserSnippets,
  getDeletedUserSnippets,
  uploadProfilePhoto,
  uploadProfilePhoto64,
  updateUserData,
  getProfilePhoto
}
