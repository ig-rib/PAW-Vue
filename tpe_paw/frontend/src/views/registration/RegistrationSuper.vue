<template>
  <v-container id="registration-super-container">
    <v-layout justify-center>
      <v-flex xs10 sm6 md6 lg5>
        <v-card min-width="420px" min-height="max-content" class="registration-super-card">
          <router-view></router-view>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  title: 'Snippit ',
  name: 'RegistrationSuper'
}
</script>

<style lang="scss">
  #registration-super-container {
    .registration-super-card {
      max-width: 70% !important;
      border-radius: 8px !important;
    }
  }
</style>
