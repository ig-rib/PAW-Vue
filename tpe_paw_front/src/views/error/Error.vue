<template>
  <v-container>
    <v-card>
      <v-container>
        <v-layout class="error-logo-layout">
          {{ errorCode }}
        </v-layout>
        <v-layout class="error-title-layout">
          <v-flex>
            {{ errorData.title }}
          </v-flex>
        </v-layout>
        <v-layout class="error-gohome-text-layout">
          {{ $t('error.goBackHome') }}
        </v-layout>
        <v-layout justify-center class="error-gohome-btn-layout">
          <v-flex shrink>
            <v-btn @click="goHome">
              {{ $t('error.goHome') }}
            </v-btn>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card>
  </v-container>
</template>

<script>
export default {
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
