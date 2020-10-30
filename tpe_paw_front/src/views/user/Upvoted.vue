<template>
  <div class="fill-width">
    <v-layout>
      <v-flex class="view-title">
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
  mounted () {
    this.$on('searchResults', r => this.$refs.userFavoritesSnippetGrid.$emit('searchResults', r))
  }
}
</script>
