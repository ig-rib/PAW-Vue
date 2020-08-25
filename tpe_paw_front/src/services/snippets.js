import urls from './urls.js'
import axiosFetcher from './axiosFetcher.js'

const createSnippet = (snippet) => axiosFetcher.post(urls.snippet.create, {}, snippet)

const getSnippet = (id) => axiosFetcher.get(urls.snippet.snippet, {
  pathVariables: {
    id
  }
})

const voteSnippet = (id, params) => axiosFetcher.post(urls.snippet.voteSnippet, {
  pathVariables: {
    id
  }
}, params)

const favSnippet = (id) => axiosFetcher.put(urls.snippet.favSnippet, {
  pathVariables: {
    id
  }
})

const unfavSnippet = (id) => axiosFetcher.del(urls.snippet.favSnippet, {
  pathVariables: {
    id
  }
})

const reportSnippet = (id, params) => axiosFetcher.put(urls.snippet.reportSnippet, {
  pathVariables: {
    id
  }
}, params)

const unreportSnippet = (id) => axiosFetcher.del(urls.snippet.reportSnippet, {
  pathVariables: {
    id
  }
})

const deleteSnippet = (id) => axiosFetcher.del(urls.snippet.snippet, {
  pathVariables: {
    id
  }
})

const restoreDeletedSnippet = (id) => axiosFetcher.put(urls.snippet.restore, {
  pathVariables: {
    id
  }
})

export default {
  createSnippet,
  getSnippet,
  voteSnippet,
  favSnippet,
  unfavSnippet,
  reportSnippet,
  unreportSnippet,
  deleteSnippet,
  restoreDeletedSnippet
}