<template>
  <v-container>
    <v-layout>
      <v-flex v-cloak>
        {{ $t('languages.languageSnippets', { langName: language.name}) }}
      </v-flex>
      <v-flex v-if="isAdmin" ml-auto>
        <v-btn @click="deleting = true" icon>
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </v-flex>
    </v-layout>
    <snippet-grid ref="languageSnippets"></snippet-grid>
    <v-dialog v-model="deleting">
      <v-card>
        <v-card-title>{{ $t('admin.confirmDeletion') }}</v-card-title>
        <v-card-text>{{ $t('admin.languageDeletionDisclaimer') }}</v-card-text>
        <v-card-actions>
          <v-btn @click="deleteLanguage">{{ $t('admin.confirm') }}</v-btn>
          <v-btn @click="deleting = false">{{ $t('admin.cancel') }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import languages from '@/services/languages.js'

export default {
    name: 'LanguagesSnippets',
    data () {
      return {
        language: {},
        deleting: false
      }
    },
    methods: {
      deleteLanguage () {
        languages.deleteLanguage(this.$route.params.id)
          // TODO handle responses accordingly
          .then(r => { 
            this.deleting = false
            this.$router.push({
              name: 'languages'
            })
          })
      }
    },
    computed: {
      isAdmin () {
        return this.$store.getters.user.admin
      }
    },
    mounted () {
      languages.getLanguage(this.$route.params.id).then(r => {
        this.language = r.data
      })
      this.$on('searchResults', (r) => this.$refs.languageSnippets.$emit('searchResults', r))
  }
}

</script>
