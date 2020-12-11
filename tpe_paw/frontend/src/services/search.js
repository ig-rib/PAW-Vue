import urls from './urls'
import axiosFetcher from './axiosFetcher'
import i18n from '@/i18n'
import Router from '@/router'

// todo move to snippets.js, rename
// every dedicated search endpoint

const constants = {
  type: {
    all: {
      name: i18n.t('search.type.all'),
      value: 'all'
    },
    tag: {
      name: i18n.t('search.type.tag'),
      value: 'tag'
    },
    title: {
      name: i18n.t('search.type.title'),
      value: 'title'
    },
    content: {
      name: i18n.t('search.type.content'),
      value: 'content'
    },
    username: {
      name: i18n.t('search.type.username'),
      value: 'username'
    },
    language: {
      name: i18n.t('search.type.language'),
      value: 'language'
    }
  },
  order: {
    asc: {
      name: i18n.t('search.order.asc'),
      value: 'asc'
    },
    desc: {
      name: i18n.t('search.order.desc'),
      value: 'desc'
    },
    no: {
      name: i18n.t('search.order.no'),
      value: 'no'
    }
  }
}

const snippetQueryParamTemplate = {
  q: '',
  s: '',
  t: '',
  page: 1
}

/**
 * Search in location
 * @param {string} location
 * @param {Number} page
 * @param {Object} params
 * @param {String} params.q The query itself
 * @param {String} params.t Part of snippet to search, one of constants.types.x.value where x can be any
 * @param {String} params.uid User id
 * @param {String} params.s Sort, one of constants.orders.x.value where x can be any
 * @returns {Promise} Response
 */
const searchInLocation = (route, queryParams) => {
  for (const key in snippetQueryParamTemplate) {
    if (queryParams[key] == null) {
      queryParams[key] = snippetQueryParamTemplate[key]
    }
  }
  // // Remove the locale part of uri
  // const resultArray = location.replace(/\/$/, '').split('/')
  // resultArray.shift()
  // resultArray.shift()
  // const pathSuffix = resultArray.join('/')
  let url = ''
  const pathVariables = {}
  
  switch (route.name) {
    case 'tag-snippets':
      url = urls.tags.getTagSnippets
      pathVariables.id = route.params.id
      break;
    case 'language-snippets':
      url = urls.languages.getLanguageSnippets
      pathVariables.id = route.params.id
      break;
    case 'active-snippets':
      url = urls.user.activeSnippets
      pathVariables.id = route.params.id
      break;
    case 'deleted-snippets':
      url = urls.user.deletedSnippets
      pathVariables.id = route.params.id
      break;
    case 'following':
      url = urls.user.followingSnippets
      break;
    case 'favorites':
      url = urls.user.favoriteSnippets
      break;
    case 'upvoted':
      url = urls.user.upvotedSnippets
      break;
    case 'flagged':
      url = urls.snippet.flagged
      break;
    case 'explore':
      queryParams.title = queryParams.q
      delete queryParams.q 
      Router.replace({
        query: queryParams
      })
      return explore(queryParams)
    case 'feed':
    default:
      url = urls.snippet.snippets
      break;
  }

  return axiosFetcher.get(url, {
    queryParams,
    pathVariables
  })
}

const explore = params => {
  return axiosFetcher.get(urls.explore.search, {
    queryParams: params
  })
}

export default {
  constants,
  searchInLocation,
  explore
}
