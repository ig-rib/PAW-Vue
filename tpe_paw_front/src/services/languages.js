import urls from './urls'
import axiosFetcher from './axiosFetcher'

const queryParamTemplate = {
  q: '',
  page: 1,
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

<<<<<<< HEAD
const searchLanguage = (page = 1, name, showEmpty = true) => axiosFetcher.get(urls.languages.searchLanguages, {
  queryParams: {
    page,
    showEmpty,
    name
  } 
})

=======
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
>>>>>>> develop
const deleteLanguage = (id) => axiosFetcher.del(urls.languages.deleteLanguage, {
   pathVariables: { 
     id: id 
    } 
})



export default {
  getLanguages,
  getSnippetsForLanguage,
  searchLanguages,
  deleteLanguage
}
