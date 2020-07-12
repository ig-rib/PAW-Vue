<template>
  <v-app id="app">
    <router-view></router-view>
  </v-app>
</template>

<script>
import registration from '@/services/registration.js'
export default {
  name: 'App',
  data: () => ({
    //
  }),
  mounted () {
    registration.login('JaneRoe', 'password')
      .then(r => {
        console.log(r.headers.authorization.split('Bearer').pop())
        this.$store.dispatch('setToken', r.headers.authorization.split('Bearer').pop())
      })
  }
}
</script>
