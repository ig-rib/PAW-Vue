import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getTags = (page) => axiosFetcher.get(urls.tags.getTags, {
  queryParams: {
    page: page
  }
})

const getTagSnippets = (tagId, page = 1) => axiosFetcher.get(urls.tags.getTagSnippets, {
  queryParams: {
    page,
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

const searchTags = (page = 1, name, showEmpty = true, showOnlyFollowing = false) => axiosFetcher.post(urls.tags.searchTags, {
  queryParams: {
    page,
    showEmpty,
    showOnlyFollowing,
    name
  } 
})


export default {
  getTags,
  followTag,
  unfollowTag,
  deleteTag,
  searchTags,
  getTagSnippets
}
