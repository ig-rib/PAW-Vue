import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'
import i18n from '../i18n.js'

Vue.use(Vuex)

const defaultAction = {
  func: () => {
    store.dispatch('hideSnackbar')
  },
  text: i18n.t('snackbar.hide')
}

const defaultState = {
  token: {
    value: '',
    expirationDate: null
  },
  snackbar: {
    show: false,
    message: 'HELLO',
    action: defaultAction
  },
  user: () => {}
}

const store = new Vuex.Store({
  plugins: [createPersistedState({
    storage: window.sessionStorage
  })],
  state: {
    token: {
      value: '',
      expirationDate: null
    },
    snackbar: {
      show: false,
      message: 'HELLO',
      action: defaultAction
    },
    user: () => {}
  },
  mutations: {
    setToken (state, tokenData) {
      state.token.value = tokenData.token
      state.token.expirationDate = tokenData.expirationDate
    },
    setUser (state, userData) {
      state.user = userData
    },
    setSnackbar (state, snackbarData) {
      for (const [key, value] of Object.entries(snackbarData)) {
        state.snackbar[key] = value
      }
    },
    resetState (state) {
      Object.assign(state, defaultState)
    }
  },
  getters: {
    token (state) {
      return state.token
    },
    loggedIn (state) {
      return (state.token.value != null &&
        state.token.expirationDate != null &&
        new Date() < new Date(state.token.expirationDate))
    },
    user (state) {
      return state.user
    },
    snackbar (state) {
      return state.snackbar
    }
  },
  actions: {
    setToken ({ commit }, tokenData) {
      commit('setToken', tokenData)
    },
    setUser ({ commit }, userData) {
      commit('setUser', userData)
    },
    logout (/* { commit } */) {
      window.sessionStorage.clear()
      // commit('resetState')
    },
    snackError ({ commit }, payload) {
      const message = payload || i18n.t('snackbar.error.defaultMessage')
      commit('setSnackbar', {
        show: true,
        color: 'red',
        message
      })
    },
    snackSuccess ({ commit }, payload) {
      const message = payload || i18n.t('snackbar.success.defaultMessage')
      commit('setSnackbar', {
        show: true,
        color: 'green',
        message
      })
    },
    hideSnackbar ({ commit }) {
      commit('setSnackbar', {
        show: false,
        message: '',
        action: defaultAction
      })
    },
    hideSnackbarNoAction ({ commit }) {
      commit('setSnackbar', {
        show: false,
        message: ''
      })
    }
  },
  modules: {
  }
})

// actionFunction = () => {
//   store.dispatch('hideSnackbar')
// }

export default store
