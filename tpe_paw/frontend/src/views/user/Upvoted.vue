<template>
  <div class="fill-width">
    <v-layout class="view-title-layout">
      <v-flex shrink class="view-title">
        {{ $t('user.upvoted.title') }}
      </v-flex>
    </v-layout>
    <v-layout>
      <snippet-grid ref="userFavoritesSnippetGrid"></snippet-grid>
    </v-layout>
  </div>
</template>

<script>
export default {
  title: 'Snippit - Upvoted',
  mounted () {
    this.$on('searchResults', r => this.$refs.userFavoritesSnippetGrid.$emit('searchResults', r))
  }
}
</script>
