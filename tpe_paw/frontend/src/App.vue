<template>
  <v-app id="app">
    <v-snackbar
      :value="snackbar.show"
      @input="hideSnackbar"
      :color="snackbar.color"
    >
      {{ snackbar.message }}
      <template v-slot:action="{ attrs }">
        <v-btn
          text
          v-bind="attrs"
          @click="snackbar.action.func">
          {{ snackbar.action.text }}
        </v-btn>
      </template>
    </v-snackbar>
    <router-view></router-view>
    <vue-progress-bar></vue-progress-bar>
    <v-footer id="footer">
      <v-card-text class="justify-center">
        2020 - Snippit
      </v-card-text>
    </v-footer>
  </v-app>
</template>

<script>
import user from '@/services/user.js'

export default {
  title: 'Snippit',
  name: 'App',
  data: () => ({
    //
  }),
  computed: {
    snackbar: {
      get () {
        const snack = this.$store.getters.snackbar
        return snack
      },
      set () {
        this.$store.dispatch('hideSnackbar')
      }
    },
    language () {
      return navigator.language
    }
    // rememberMe () {
    //   return window.localStorage.getItem('keepSignedIn')
    // },
    // token () {
    //   return window.localStorage.getItem('token')
    // }
  },
  methods: {
    hideSnackbar () {
      this.$store.dispatch('hideSnackbar')
    }
  },
  mounted () {
    window.onbeforeunload = function () {
      if (window.localStorage.getItem('token') != null && window.localStorage.getItem('keepSignedIn') !== 'true') {
        window.localStorage.removeItem('token')
        this.$store.dispatch('logout')
      }
    }
    if (window.localStorage.getItem('token') != null) {
      this.$store.dispatch('setToken', JSON.parse(window.localStorage.getItem('token')))
      user.getLoggedInUser().then(r => {
        this.$store.dispatch('setUser', r.data)
      })
    }
    // else {
    //   window.localStorage.removeItem('token')
    // }
  }
}
</script>

<style lang="scss">
@import "@/styles/colours.scss";
@import "@/styles/main.scss";
#app {
  background: #fafafa;
}
</style>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
