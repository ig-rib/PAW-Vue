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
    }
  },
  getters: {
    token (state) {
      return state.token
    },
    loggedIn (state) {
      console.log(state.token)
      return (state.token.value != null &&
        state.token.expirationDate != null &&
        new Date() < new Date(state.token.expirationDate))
    },
    user (state) {
      return state.user
    }
  },
  actions: {
    setToken ({ commit }, tokenData) {
      commit('setToken', tokenData)
    },
    setUser ({ commit }, userData) {
      commit('setUser', userData)
    },
    logout () {
      window.sessionStorage.clear()
    }
  },
  modules: {
  }
})
