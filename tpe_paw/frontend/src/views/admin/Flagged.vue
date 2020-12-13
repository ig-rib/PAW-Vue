<template>
  <v-container>
    <div v-if="isAdmin">
      <v-layout class="view-title-layout">
        <v-flex shrink class="view-title">
          {{ $t('admin.flagged') }}
        </v-flex>
      </v-layout>
      <v-layout>
        <snippet-grid ref="userFlaggedSnippetGrid"></snippet-grid>
      </v-layout>
    </div>
    <error v-else/>
  </v-container>
</template>

<script>
import Error from '@/views/error/Error.vue'

export default {
  title () {
    return this.$t('titles.flagged')
  },
  components: {
    error: Error
  },
  computed: {
    isAdmin () {
      return this.$store.getters.user != null && this.$store.getters.user.admin
    }
  },
  mounted () {
    if (!this.isAdmin) {
      this.$router.replace({ name: 'error' })
    }
    this.$on('searchResults', r => this.$refs.userFlaggedSnippetGrid.$emit('searchResults', r))
  }
}
</script>
