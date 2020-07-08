import axios from 'axios'

/**
 * @param {string} url
 * @param {Object} pathVariables
 * @param {string} method
 * @param {Object} content
 * @returns {Promise} Response
 */
const axiosFetcher = (url = '', pathVariables = {}, method = 'get', content = {}) => {
  url = urlMaker(url, pathVariables)
  console.log('real url', url)
  const config = {
    headers: {
      'Content-Type': 'application/json'
    }
  }
  switch (method.toLowerCase()) {
    case 'get':
      return axios.get(url)
    case 'post':
      return axios.post(url, JSON.stringify({
        data: 'dataaaa'
      }), config)
    case 'put':
      return axios.put(url, config)
    case 'delete':
      return axios.delete(url, config)
  }

  // return axios({
  //   method: method,
  //   url: url,
  //   headers: content.headers,
  //   data: content
  // })
}

const get = (url, pathVariables, content) => axiosFetcher(url, pathVariables, 'get', content)
const post = (url, pathVariables, content) => axiosFetcher(url, pathVariables, 'post', content)
const put = (url, pathVariables, content) => axiosFetcher(url, pathVariables, 'put', content)
const del = (url, pathVariables, content) => axiosFetcher(url, pathVariables, 'delete', content)

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
