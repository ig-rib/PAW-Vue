<template>
  <v-container>
    <v-layout>
      <v-flex class="view-title">
        {{ $t('user.following.title') }}
      </v-flex>
    </v-layout>
    <v-layout>
      <snippet-grid ref="userFavoritesSnippetGrid"></snippet-grid>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  mounted () {
    this.$on('searchResults', r => this.$refs.userFavoritesSnippetGrid.$emit('searchResults', r))
  }
}
</script>
