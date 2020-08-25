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
  return axiosFetcher.get(urls.languages.languages, {
    queryParams: params
  })
}
const deleteLanguage = (id) => axiosFetcher.del(urls.languages.language, {
   pathVariables: { 
     id: id 
    } 
})

const addLanguage = languageName => axiosFetcher.post(urls.languages.languages, {}, {
  name: languageName
})

export default {
  getSnippetsForLanguage,
  searchLanguages,
  deleteLanguage,
  addLanguage
}
