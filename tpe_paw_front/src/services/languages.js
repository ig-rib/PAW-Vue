import urls from './urls'
import axiosFetcher from './axiosFetcher'

const queryParamTemplate = {
  q: '',
  page: 1,
  showEmpty: 'true'
}

const getSnippetsForLanguage = (id, page) => axiosFetcher.get(urls.languages.getLanguage, {
  pathVariables: {
    id: id
  },
  queryParams: {
    page: page
  }
})

const searchLanguages = (params) => {
  for (const key in queryParamTemplate) {
    if (params[key] == null) {
      params[key] = queryParamTemplate[key]
    }
  }
  return axiosFetcher.get(urls.languages.searchLanguages, {
    queryParams: params
  })
}
const deleteLanguage = (id) => axiosFetcher.del(urls.languages.deleteLanguage, {
   pathVariables: { 
     id: id 
    } 
})



export default {
  getSnippetsForLanguage,
  searchLanguages,
  deleteLanguage
}
