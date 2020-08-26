<template>
  <v-app id="app">
    <router-view></router-view>
  </v-app>
</template>

<script>
export default {
  name: 'App',
  data: () => ({
    //
  }),
  mounted () {
  }
}
</script>

<style lang="scss">
@import "@/styles/multiselect.scss";

#app {
  background: #fefefe;
}
</style>