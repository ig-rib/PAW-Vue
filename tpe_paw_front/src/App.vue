<template>
  <v-app id="app">
    <v-snackbar
      :value="snackbar.show"
      @input="hideSnackbar"
    >
      {{ snackbar.message }}
      <template v-slot:action="{ attrs }">
        <v-btn
          text
          v-bind="attrs"
          @click="hideSnackbar">
          {{ $t('snackbar.hide') }}
        </v-btn>
      </template>
    </v-snackbar>
    <router-view></router-view>
  </v-app>
</template>

<script>
import user from '@/services/user.js'

export default {
  name: 'App',
  data: () => ({
    //
  }),
  computed: {
    snackbar: {
      get () {
        return this.$store.getters.snackbar
      },
      set () {
        this.$store.dispatch('hideSnackbar')
      }
    }
  },
  methods: {
    hideSnackbar () {
      this.$store.dispatch('hideSnackbar')
    }
  },
  mounted () {
    if (window.localStorage.getItem('token') != null) {
      this.$store.dispatch('setToken', JSON.parse(window.localStorage.getItem('token')))
      user.getLoggedInUser().then(r => {
        this.$store.dispatch('setUser', r.data)
      })
    }
  }
}
</script>

<style lang="scss">
@import "@/styles/multiselect.scss";

#app {
  background: #fafafa;
}
</style>
