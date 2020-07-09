import urls from './urls'
import axiosFetcher from './axiosFetcher'

// TODO pass page as parameter
const getTags = (page) => axiosFetcher.get(urls.tags.getTags, {
  queryParams: {
    page: page
  }
})

export default {
  getTags
}
