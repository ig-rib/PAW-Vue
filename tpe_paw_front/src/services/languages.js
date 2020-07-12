import urls from './urls'
import axiosFetcher from './axiosFetcher'

const getLanguages = (page) => axiosFetcher.get(urls.languages.getLanguages, {
  queryParams: {
    page: page
  }
})

const getSnippetsForLanguage = (id, page) => axiosFetcher.get(urls.languages.getLanguage, {
  pathVariables: {
    id: id
  },
  queryParams: {
    page: page
  }
})

export default {
  getLanguages,
  getSnippetsForLanguage
}
