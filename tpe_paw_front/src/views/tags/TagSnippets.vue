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
import SnippetComponent from '@/components/Snippet.vue'
import tags from '@/services/tags.js'
import helpers from '@/functions/helpers.js'

export default {
    name: 'TagsSnippets',
    mounted () {
    this.$on('searchResults', (r) => this.$refs.feedSnippets.$emit('searchResults', r))
  }
}

</script>
