import urls from './urls'
import axiosFetcher from './axiosFetcher'

const queryParamTemplate = {
  q: '',
  page: 1,
  showEmpty: 'true',
  showOnlyFollowing: 'false'
} 

const getTagSnippets = (id, page = 1) => axiosFetcher.get(urls.tags.getTagSnippets, {
  queryParams: {
    page
  },
  pathVariables: {
    id
  }
})

const getTag = id => axiosFetcher.get(urls.tags.tag, {
  pathVariables: {
    id
  }
})

const followTag = (id) => axiosFetcher.post(urls.tags.followTag, {
  pathVariables: {
    id
  }
})

const unfollowTag = (id) => axiosFetcher.post(urls.tags.unfollowTag, {
  pathVariables: {
    id
  }
})

const deleteTag = (id) => axiosFetcher.del(urls.tags.tag, { pathVariables: { id: id } })

const searchTags = params => {
  for (const key in queryParamTemplate) {
    if (params[key] == null) {
      params[key] = queryParamTemplate[key]
    }
  }
  return axiosFetcher.get(urls.tags.tags, {
    queryParams: params
  })
}

const addTag = tagName => axiosFetcher.post(urls.tags.tags, {}, {
  name: tagName
})

export default {
  getTag,
  followTag,
  unfollowTag,
  deleteTag,
  searchTags,
  getTagSnippets,
  addTag
}
