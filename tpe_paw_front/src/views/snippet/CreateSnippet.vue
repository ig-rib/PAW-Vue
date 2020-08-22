<template>
  <v-container>
    <v-card>
      <v-layout>
        <v-flex>
          <v-text-field
            v-model="title"
            :label="$t('snippets.createSnippet.title')"></v-text-field>
        </v-flex>
        <v-flex>
          <language-select v-model="language"/>
          {{ language }}
        </v-flex>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        <v-textarea
          v-model="description"
          :label="$t('snippets.createSnippet.description')"></v-textarea>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        <v-textarea
        v-model="code"
        :label="$t('snippets.createSnippet.code')"></v-textarea>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        <v-flex>
          <tag-select
            multiple
            v-model="tags"
            :closeOnSelect="false"
            :label="$t('snippets.createSnippet.tags')"></tag-select>
        </v-flex>
        <v-flex>
          <v-btn @click="saveSnippet">
            {{ $t('snippets.createSnippet.createSnippet') }}
          </v-btn>
        </v-flex>
      </v-layout>
    </v-card>
  </v-container>
</template>

<script>
import snippetService from '@/services/snippets.js'

export default {
  data () {
    return {
      title: '',
      language: null,
      description: '',
      code: '',
      tags: []
    }
  },
  methods: {
    saveSnippet () {
      snippetService.createSnippet({
        title: this.title,
        language: this.language.id,
        description: this.description,
        code: this.code,
        tags: this.tags.map(tag => tag.name)
      })
    }
  }
}
</script>