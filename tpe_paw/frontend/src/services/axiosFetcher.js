import axios from 'axios'
import Store from '@/store'
import Router from '@/router'
import querystring from 'querystring'
import { cacheAdapterEnhancer } from 'axios-extensions'
import i18n from '@/i18n.js'

axios.defaults.paramsSerializer = params => {
  return querystring.stringify(params)
}
// axios.defaults.adapter = cacheAdapterEnhancer(axios.defaults.adapter)
const http = axios.create({
  adapter: cacheAdapterEnhancer(axios.defaults.adapter, { enabledByDefault: true, cacheFlag: 'useCache' })
})
/**
 * Wrapper for axios
 * @param {string} url
 * @param {Object} params
 * @param {Object} params.pathVariables Path variables to be replaced in url
 * @param {Object} params.queryParams Query parameters
 * @param {string} method
 * @param {Object} data
 * @returns {Promise} Response
 */

const axiosFetcher = (url = '', params = {}, method = 'get', data = {}, headers = {}) => {
  url = urlMaker(url, params.pathVariables)
  const config = {
    headers: {
      'Content-Type': 'application/json'
    },
    params: params.queryParams
  }
  Object.assign(config.headers, headers)
  if (Store.getters.token.value != null && Store.getters.loggedIn) {
    config.headers.Authorization = 'Bearer ' + Store.getters.token.value
  }
  // console.log('pathVariables', params.pathVariables, 'url', url, 'config', config)
  let promise = null
  switch (method.toLowerCase()) {
    case 'get':
      config.useCache = true
      promise = http.get(url, config)
      break;
    case 'post':
      promise = http.post(url, data, config)
      break;
    case 'put':
      promise = http.put(url, data, config)
      break;
    case 'delete':
      promise = http.delete(url, config)
      break;
  }
  // Handle errors
  // Nth Could modularize implementation of 
  // error handling with beforeAll/afterAll methods
  // in every request
  return promise
    .then(r => {
      return Promise.resolve(r)
    })
    .catch(e => {
      switch (e.response.status) {
        case 400:
          if (e.response.data.length == null) {
            Store.dispatch('snackError', i18n.t('error.unknown'))
          } else if (e.response.data.length === 1) {
            const errorObj = e.response.data[0]
            console.log(errorObj, e.response.data)
            Store.dispatch('snackError', i18n.t('error.400.singleError', errorObj))
          } else {
            Store.dispatch('snackError', i18n.t('error.400.multipleErrors'))
          }
          break;
        case 401:
          Router.push({ name: 'login' })
          break;
        case 403:
        case 404:
          Router.push({
            name: 'error',
            params: {
              errNo: e.response.status
            }
          })            
          break;
        default:
          break;
      }
      return Promise.reject(e)
    })
}

/**
 * Perform get request
 * @param {string} url
 * @param {Object} params
 * @param {Object} params.pathVariables Path variables to be replaced in url
 * @param {Object} params.queryParams Query parameters
 * @returns {Promise} Response
 */
const get = (url, params = {}, data = {}) => axiosFetcher(url, params, 'get')

/**
 * Perform post request
 * @param {string} url
 * @param {Object} params
 * @param {Object} params.pathVariables Path variables to be replaced in url
 * @param {Object} params.queryParams Query parameters
 * @param {Object} data
 * @returns {Promise} Response
 */
const post = (url, params = {}, data = {}, headers = {}) => axiosFetcher(url, params, 'post', data, headers)

/**
 * Perform put request
 * @param {string} url
 * @param {Object} params
 * @param {Object} params.pathVariables Path variables to be replaced in url
 * @param {Object} params.queryParams Query parameters
 * @param {Object} data
 * @returns {Promise} Response
 */
const put = (url, params = {}, data = {}, headers = {}) => axiosFetcher(url, params, 'put', data, headers)

/**
 * Perform delete request
 * @param {string} url
 * @param {Object} params
 * @param {Object} params.pathVariables Path variables to be replaced in url
 * @param {Object} params.queryParams Query parameters
 * @returns {Promise} Response
 */
const del = (url, params = {}, data = {}) => axiosFetcher(url, params, 'delete')

/**
 * @private
 * @description Search and replace params in urls
 * @example
 * // returns /api/entity/1/name/myvalue
 * urlMaker('/api/entity/:id/name/:value', { id: 1, value: 'myvalue' })
 * @param {string} [url=''] Base url
 * @param {Object} params Params to replace
 * @returns {string} New URL
 */
const urlMaker = (url = '', params) => {
  const expression = /:[a-z]+\/?/gi
  let newUrl = url
  const matches = url.match(expression)

  if (matches != null) {
    matches.map(fullItem => {
      const item = fullItem.substring(1, fullItem.length).replace('/', '')
      if (newUrl !== false && params[item] != null) {
        newUrl = newUrl.replace(':' + item, params[item])
      } else {
        newUrl = false
      }
    })
  }

  return newUrl
}

export default {
  get,
  post,
  put,
  del
}
