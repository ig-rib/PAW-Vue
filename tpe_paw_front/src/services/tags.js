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

export default {
  getTags,
  followTag
}
