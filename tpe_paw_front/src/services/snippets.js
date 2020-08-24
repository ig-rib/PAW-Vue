import urls from './urls.js'
import axiosFetcher from './axiosFetcher.js'

const createSnippet = (snippet) => axiosFetcher.post(urls.snippet.create, {}, snippet)

const getSnippet = (id) => axiosFetcher.get(urls.snippet.getSnippet, {
  pathVariables: {
    id
  }
})

export default {
  createSnippet,
  getSnippet
}