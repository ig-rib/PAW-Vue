<template>
  <div>
    <v-layout>
      FLAGGED
    </v-layout>
    <v-layout>
      <snippet-grid ref="userFlaggedSnippetGrid"></snippet-grid>
    </v-layout>
  </div>
</template>

<script>
export default {
  mounted () {
    this.$on('searchResults', r => this.$refs.userFlaggedSnippetGrid.$emit('searchResults', r))
  }
}
</script>