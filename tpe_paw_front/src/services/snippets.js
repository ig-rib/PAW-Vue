import urls from './urls.js'
import axiosFetcher from './axiosFetcher.js'

const createSnippet = (snippet) => axiosFetcher.post(urls.snippet.create, {}, snippet)

export default {
  createSnippet
}