import urls from './urls'
import axiosFetcher from './axiosFetcher'

const queryParamTemplate = {
  q: '',
  page: 1,
  showEmpty: 'true',
  showOnlyFollowing: 'false'
} 

const getTags = (page) => axiosFetcher.get(urls.tags.getTags, {
  queryParams: {
    page: page
  }
})

const getTagSnippets = (tagId, page = 1) => axiosFetcher.get(urls.tags.getTagSnippets, {
  queryParams: {
    page
  },
  pathVariables: {
    tagId
  }
})

const followTag = (id) => axiosFetcher.post(urls.tags.followTag, {
  pathVariables: {
    tagId: id
  }
})

const unfollowTag = (id) => axiosFetcher.post(urls.tags.unfollowTag, {
  pathVariables: {
    tagId: id
  }
})

const deleteTag = (id) => axiosFetcher.del(urls.tags.deleteTag, { pathVariables: { tagId: id } })

const searchTags = params => {
  for (const key in queryParamTemplate) {
    if (params[key] == null) {
      params[key] = queryParamTemplate[key]
    }
  }
  return axiosFetcher.get(urls.tags.searchTags, {
    queryParams: params
  })
}

export default {
  getTags,
  followTag,
  unfollowTag,
  deleteTag,
  searchTags,
  getTagSnippets,
}
