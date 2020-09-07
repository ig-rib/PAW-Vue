<template>
  <v-container>
    <v-layout>
      <v-flex>
        {{ $t('feed.title') }}
      </v-flex>
    </v-layout>
    <snippet-grid ref="feedSnippets"></snippet-grid>
  </v-container>
</template>

<script>

export default {
  name: 'Feed',
  mounted () {
    console.log('storage')
    console.log(window.localStorage)
    this.$on('searchResults', (r) => this.$refs.feedSnippets.$emit('searchResults', r))
  }
}
</script>
