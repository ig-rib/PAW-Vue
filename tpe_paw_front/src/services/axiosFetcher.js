import axios from 'axios'

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
  console.log('real url', url)
  const config = {
    headers: {
      'Content-Type': 'application/json'
    },
    params: params.queryParams
  }
  console.log(config)
  switch (method.toLowerCase()) {
    case 'get':
      return axios.get(url, config)
    case 'post':
      return axios.post(url, data, config)
    case 'put':
      return axios.put(url, data, config)
    case 'delete':
      return axios.delete(url, data, config)
  }
}

/**
 * Perform get request
 * @param {string} url
 * @param {Object} params
 * @param {Object} params.pathVariables Path variables to be replaced in url
 * @param {Object} params.queryParams Query parameters
 * @param {Object} data
 * @returns {Promise} Response
 */
const get = (url, params = {}, data = {}) => axiosFetcher(url, params, 'get', data)

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
 * @param {Object} data
 * @returns {Promise} Response
 */
const del = (url, params = {}, data = {}) => axiosFetcher(url, params, 'delete', data)

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
