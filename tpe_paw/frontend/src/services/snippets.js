import urls from './urls.js'
import axiosFetcher from './axiosFetcher.js'

const createSnippet = (snippet) => axiosFetcher.post(urls.snippet.create, {}, snippet)

const getSnippet = (id) => axiosFetcher.get(urls.snippet.snippet, {
  pathVariables: {
    id
  }
})

const voteSnippet = (id, params) => axiosFetcher.put(urls.snippet.voteSnippet, {
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

const flagSnippet = (id, params) => axiosFetcher.put(urls.snippet.flagSnippet, {
  pathVariables: {
    id
  }
}, params)

const unflagSnippet = (id, params) => axiosFetcher.del(urls.snippet.flagSnippet, {
  pathVariables: {
    id
  }
}, params)

const reportSnippet = (id, params) => axiosFetcher.put(urls.snippet.reportSnippet, {
  pathVariables: {
    id
  }
}, params)

const unreportSnippet = (id) => axiosFetcher.put(urls.snippet.reportSnippet, {
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

const editSnippet = (id, params) => axiosFetcher.put(urls.snippet.snippet, {
  pathVariables: {
    id
  }
}, params)

export default {
  createSnippet,
  getSnippet,
  voteSnippet,
  favSnippet,
  unfavSnippet,
  flagSnippet,
  unflagSnippet,
  reportSnippet,
  unreportSnippet,
  deleteSnippet,
  restoreDeletedSnippet,
  editSnippet
}
