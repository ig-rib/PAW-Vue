import urls from './urls'
import axiosFetcher from './axiosFetcher'

const queryParamTemplate = {
  q: '',
  page: '',
  showEmpty: 'true'
} 

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

const searchLanguage = (params) => {
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
  getLanguages,
  getSnippetsForLanguage,
  searchLanguage,
  deleteLanguage
}
