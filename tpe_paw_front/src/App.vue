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
  }
}
</script>
