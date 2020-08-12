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

const searchLanguage = (page = 1, name, showEmpty = true) => axiosFetcher.get(urls.languages.searchLanguages, {
  queryParams: {
    page,
    showEmpty,
    name
  } 
})

const deleteLanguage = (id) => axiosFetcher.del(urls.languages.deleteLanguage, {
   pathVariables: { 
     id: id 
    } 
})



export default {
  getLanguages,
  getSnippetsForLanguage,
  searchLanguage,
  deleteLanguage
}
