import urls from './urls'
import axiosFetcher from './axiosFetcher'

// TODO pass page as parameter
const getTags = () => axiosFetcher.get(urls.tags.getTags, {}, {})

export default {
  getTags
}
