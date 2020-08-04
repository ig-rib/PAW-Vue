import urls from './urls'
import axiosFetcher from './axiosFetcher'
import i18n from '@/i18n'

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
const searchInLocation = (location, params) => {
  // Remove the locale part of uri
  let resultArray = location.split('/')
  resultArray.shift()
  resultArray.shift()
  const pathSuffix = resultArray.join('/')
  console.log('params', params)
  return axiosFetcher.get(urls.localDomain + pathSuffix + '/search', {
    queryParams: params
  })
}
export default {
  constants,
  searchInLocation
}