import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getTags = (page) => axiosFetcher.get(urls.tags.getTags, {
  queryParams: {
    page: page
  }
})

const followTag = (id, follow) => axiosFetcher.post(urls.tags.followTag, {
  pathVariables: {
    tagId: id
  }
}, {
  follow: follow
})

const deleteTag = (id) => axiosFetcher.del(urls.tags.deleteTag, { pathVariables: { tagId: id } })

const searchTags = (page, name, isShowEmpty, isShowOnlyFollowing) => axiosFetcher.post(urls.tags.searchTags, { pathVariables: { page: page } }, {
  name,
  isShowEmpty,
  isShowOnlyFollowing
})

export default {
  getTags,
  followTag,
  deleteTag,
  searchTags
}
