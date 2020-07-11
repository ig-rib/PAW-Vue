import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getLanguages = (page) => axiosFetcher.get(urls.languages.getLanguages, {
  queryParams: {
    page: page
  }
})

export default {
  getLanguages
}
