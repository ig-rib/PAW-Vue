import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

export default new Vuex.Store({
  plugins: [createPersistedState({
    storage: window.sessionStorage,
  })],
  state: {
    token: {
      value: '',
      expirationDate: null
    }
  },
  mutations: {
    setToken (state, tokenData) {
      state.token.value = tokenData.token
      state.token.expirationDate = tokenData.expirationDate
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
    }
  },
  actions: {
    setToken ({ commit }, tokenData) {
      commit('setToken', tokenData)
    }
  },
  modules: {
  }
})
