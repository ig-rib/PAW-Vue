<template>
  <v-container>
    <v-layout class="view-title-layout">
      <v-flex shrink class="view-title">
        {{ $t('admin.flagged') }}
      </v-flex>
    </v-layout>
    <v-layout>
      <snippet-grid ref="userFlaggedSnippetGrid"></snippet-grid>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  title () {
    return this.$t('titles.flagged')
  },
  mounted () {
    this.$on('searchResults', r => this.$refs.userFlaggedSnippetGrid.$emit('searchResults', r))
  }
}
</script>
