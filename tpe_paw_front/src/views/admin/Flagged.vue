<template>
  <v-container>
    <v-layout>
      FLAGGED
    </v-layout>
    <v-layout>
      <snippet-grid ref="userFlaggedSnippetGrid"></snippet-grid>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  mounted () {
    this.$on('searchResults', r => this.$refs.userFlaggedSnippetGrid.$emit('searchResults', r))
  }
}
</script>
