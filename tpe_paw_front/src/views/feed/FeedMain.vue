<template>
  <div>
    <v-layout>
      <v-flex class="view-title">
        {{ $t('feed.title') }}
      </v-flex>
    </v-layout>
    <snippet-grid ref="feedSnippets"></snippet-grid>
  </div>
</template>

<script>

export default {
  name: 'Feed',
  mounted () {
    this.$on('searchResults', (r) => this.$refs.feedSnippets.$emit('searchResults', r))
  }
}
</script>
