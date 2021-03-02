<template>
  <v-layout my-8 justify-center>
    <v-card width="600px" class="error-card">
      <v-container>
        <v-layout mb-8 mt-4 justify-center class="error-logo-layout">
          {{ errorCode }}
        </v-layout>
        <v-layout  my-2 justify-center class="error-title-layout">
          <v-flex shrink>
            {{ errorData.name }}
          </v-flex>
        </v-layout>
        <v-layout mb-2 justify-center class="error-gohome-text-layout">
          {{ $t('error.goBackHome') }}
        </v-layout>
        <v-layout my-3 justify-center class="error-gohome-btn-layout">
          <v-flex shrink>
            <v-btn rounded outlined color="primary" @click="goHome">
              {{ $t('error.goHome') }}
            </v-btn>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card>
  </v-layout>
</template>

<script>
export default {
  title () {
    return this.$t('titles.error')
  },
  data () {
    return {
      errorCode: null,
      errorData: {}
    }
  },
  methods: {
    goHome () {
      this.$router.push({
        name: 'feed'
      })
    }
  },
  mounted () {
    this.errorCode = this.$route.params.errNo || 404
    this.errorData = this.$t('error')[this.errorCode]
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/error.scss";
</style>
