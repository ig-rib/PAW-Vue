import axios from 'axios'
import Store from '@/store'
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

const axiosFetcher = (url = '', params = {}, method = 'get', data = {}) => {
  url = urlMaker(url, params.pathVariables)
  const config = {
    headers: {
      'Content-Type': 'application/json'
    },
    params: params.queryParams
  }
  if (Store.getters.token.value != null) {
    config.headers.Authorization = 'Bearer ' + Store.getters.token.value
  }
  let promise = null
  switch (method.toLowerCase()) {
    case 'get':
      promise = axios.get(url, config)
      break;
    case 'post':
      promise = axios.post(url, data, config)
      break;
    case 'put':
      promise = axios.put(url, data, config)
      break;
    case 'delete':
      promise = axios.delete(url, config)
      break;
  }
  return promise
    .then(r => {
      if (r.status === 401) {
        Store.push({ name: 'feed' })
      }
      return Promise.resolve(r)
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
const post = (url, params = {}, data = {}) => axiosFetcher(url, params, 'post', data)

/**
 * Perform put request
 * @param {string} url
 * @param {Object} params
 * @param {Object} params.pathVariables Path variables to be replaced in url
 * @param {Object} params.queryParams Query parameters
 * @param {Object} data
 * @returns {Promise} Response
 */
const put = (url, params = {}, data = {}) => axiosFetcher(url, params, 'put', data)

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
