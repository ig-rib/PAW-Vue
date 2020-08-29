<template>
  <v-container class="registration-super-container">
    <v-layout justify-center>
      <v-flex xs10 sm6 md6 lg5>
        <v-card min-width="420px" height="500px" class="registration-super-card">
          <router-view></router-view>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  name: 'RegistrationSuper'
}
</script>

<style lang="scss">
  #registration-super-container {
    .registration-super-card {
      max-width: 70% !important;
      // min-height: 500px;
    }
  }
</style>