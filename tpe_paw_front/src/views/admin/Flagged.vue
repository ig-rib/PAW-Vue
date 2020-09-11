<template>
  <v-container>
    <v-layout>
      <v-flex class="view-title">
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
  mounted () {
    this.$on('searchResults', r => this.$refs.userFlaggedSnippetGrid.$emit('searchResults', r))
  }
}
</script>
