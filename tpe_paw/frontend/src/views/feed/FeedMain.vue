<template>
  <div>
    <v-layout class="view-title-layout">
      <v-flex shrink class="view-title">
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
