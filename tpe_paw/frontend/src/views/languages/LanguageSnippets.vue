<template>
  <v-container>
    <v-layout justify-center align-center column pa-5 v-if="language.deleted">
      <v-flex ma-3 shrink>
        <v-card class="notice-card">
          <v-layout>
            <v-flex ml-2 shrink class="notice-card-icon">
              <v-icon color="red">
                mdi-delete-sweep
              </v-icon>
            </v-flex>
            <v-flex>
              <v-card-title>
                {{ $t('languages.languageDeleted.title') }}
              </v-card-title>
              <v-card-text class="notice-card-text">
                <p>{{ $t('languages.languageDeleted.message') }}</p>
              </v-card-text>
            </v-flex>
          </v-layout>
        </v-card>
      </v-flex>
    </v-layout>
    <v-layout justify-center align-center>
      <v-flex shrink class="view-title" v-cloak>
        {{ $t('languages.languageSnippets', { langName: language.name}) }}
      </v-flex>
      <v-flex v-if="isAdmin && !language.deleted" ml-5 shrink>
        <v-btn class="title-delete-btn" @click="deleting = true" icon>
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </v-flex>
    </v-layout>
    <snippet-grid ref="languageSnippets"></snippet-grid>
    <v-dialog content-class="delete-dialog" v-model="deleting">
      <v-card class="dialog-card">
        <v-card-title class="justify-center">{{ $t('admin.confirmDeletion') }}</v-card-title>
        <v-card-subtitle class="justify-center dialog-subtitle">{{ $t('admin.languageDeletionDisclaimer', {langName: language.name}) }}</v-card-subtitle>
        <v-layout px-3 pb-5 justify-center>
          <v-flex shrink mr-2>
            <v-btn rounded outlined color="#2286c3" @click="deleteLanguage">{{ $t('admin.confirm') }}</v-btn>
          </v-flex>
          <v-flex shrink>
            <v-btn rounded outlined color="red" @click="deleting = false">{{ $t('admin.cancel') }}</v-btn>
          </v-flex>
        </v-layout>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import languages from '@/services/languages.js'

export default {
    title () { return this.$t('titles.languagesSnippets') },
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
          .then(r => { 
            this.deleting = false
            this.$store.dispatch('snackSuccess', this.$t('languages.successDelete'))
            this.$router.push({
              name: 'languages-main'
            })
          })
         .catch(e => this.$store.dispatch('snackError', this.$t('languages.errorDelete')))
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

<style lang="scss" scoped>
@import '@/styles/noticeCard.scss';

</style>
