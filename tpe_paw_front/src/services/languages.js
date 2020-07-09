import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getLanguages = () => axiosFetcher.get(urls.languages.getLanguages)

export default {
  getLanguages
}
