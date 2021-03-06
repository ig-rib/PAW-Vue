<template>
  <v-container>
    <v-layout class="view-title-layout">
      <v-flex shrink class="view-title">
        {{$t('languages.title')}}
      </v-flex>
    </v-layout>
    <v-layout 
        class="grid-error-layout"
        align-center
        column
        justify-center
        v-if="status === 'e'">
        <v-flex my-2 shrink>
          {{ $t('error.grid.languages') }}
        </v-flex>
        <v-flex my-2>
          <v-btn @click="tryLoadingAgain">{{ $t('error.grid.tryAgain') }}</v-btn>
        </v-flex>
      </v-layout>
    <div>
      <v-layout class="grid-progress-circle" v-if="status === 'l'" justify-center>
        <v-progress-circular
          :size="70"
          :width="7"
          color="primary"
          indeterminate>
        </v-progress-circular>
      </v-layout>
      <v-layout v-else-if="status === '' && hasLanguages" wrap justify-center>
        <v-flex my-2
          :class="`lg3 md4 sm6 xs12 ${ $vuetify.breakpoint.lgAndUp ? 'px-2' : 'mx-2' } px-2`"
          v-for="lang in languages"
          :key="lang.id">
          <div>
            <v-card elevation="1" class="card-chip" @click="goToLanguageSnippets(lang)">
              <v-layout width="100%">
                <v-flex px-2 class="tag-name-flex">
                  {{ titleAbreviation(lang.name,20)}}
                </v-flex>
                <v-flex v-if="lang.snippetsUsingIsEmpty" shrink ml-auto>
                  <v-icon>
                    mdi-alpha-e-circle
                  </v-icon>
                </v-flex>
              <!-- <v-chip @click="goToLanguageSnippets(lang)" class="ma-2 language-chip" label>
                {{ lang.name }}
              </v-chip> -->
              <v-flex v-if="isAdmin" shrink>
                <v-btn icon @click="openDeleteDialog(lang)">
                  <v-icon>
                    mdi-delete
                  </v-icon>
                </v-btn>
              </v-flex>
              </v-layout>
            </v-card>
          </div>
        </v-flex>
      </v-layout>
    </div>
    <v-layout align-center justify-center py-10 v-if="status === '' && !hasLanguages">
      <v-flex shrink class="no-snippets-text">
        {{ $t('languages.noLanguagesInHere') }}
      </v-flex>
    </v-layout>
    <v-layout v-if="hasLanguages" justify-center my-10 class="text-center">
      <v-pagination
        v-if="status === ''"
        v-model="pagination.page"
        v-on:input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </v-layout>
    <v-dialog content-class="delete-dialog" v-model="deleting">
      <v-card class="dialog-card">
        <v-card-title class="justify-center">{{ $t('admin.confirmDeletion') }}</v-card-title>
        <v-card-subtitle class="justify-center dialog-subtitle">{{ $t('admin.languageDeletionDisclaimer', {langName: deletingLanguage.name}) }}</v-card-subtitle>
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
import helpers from '@/functions/helpers.js'

export default {
    title () { return this.$t('titles.languages') },
  name: 'languagesMain',
  data () {
    return {
      languages: [],
      links: [],
      pagination: {
          page: 1,
          length: 1,
          visible: 7
      },
      status: 'l',
      deleting: false,
      deletingLanguage: {}
    }
  },
  methods: {
    paginationChange () {
      this.status = 'l'
      const queryParams = {}
      Object.assign(queryParams, this.$route.query)
      queryParams.page = this.pagination.page
      languages.searchLanguages(queryParams)
        .then(values => {
          this.languages = values.data 
          this.links = helpers.parseLinks(values.headers.link)
          this.status = ''
        })
        .catch(error => { 
          this.status = 'e'
          error
          })
    },
    tryLoadingAgain () {
      this.status = 'l'
      const queryParams = {}
      Object.assign(queryParams, this.$route.query)
      queryParams.page = this.pagination.page
      languages.searchLanguages(queryParams)
        .then(values => {
          this.languages = values.data
          this.links = helpers.parseLinks(values.headers.link)
          this.status = ''
        })
        .catch(error => { 
          this.status = 'e'
          error
          })
    },
    handleSearchResponse (response) {
      this.languages = response.data
      this.links = helpers.parseLinks(response.headers.link)
      this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10);
      this.status = ''
    },
    goToLanguageSnippets (lang) {
      this.$router.push({
        name: 'language-snippets',
        params: {
          id: lang.id
        },
        selectedLang: lang
      })
    },
    openDeleteDialog (lang) {
      this.deletingLanguage = lang
      this.deleting = true
      event.stopPropagation()
    },
    deleteLanguage () {
      languages.deleteLanguage(this.deletingLanguage.id)
        .then(r => {
          this.deleting = false
          this.deletingLanguage = {}
          this.$store.dispatch('snackSuccess', this.$t('languages.successDelete'))
          this.refreshLanguages()
        })
        .catch(e => this.$store.dispatch('snackError', this.$t('languages.errorDelete')))
    },
    refreshLanguages () {
      const queryParams = this.$route.query
      languages.searchLanguages(queryParams)
        .then(response => {
          this.pagination.page = parseInt(queryParams.page) || 1
          this.handleSearchResponse(response)
        })
        .catch(error => { 
            this.status = 'e'
            error
            })
    },
    titleAbreviation (name, size) {
      if (name != null && name.length > size) {
        const newName = name.substr(0, size - 2) + '...'
        return newName
      }
      return name
    }
  },
  computed: {
    isAdmin () {
      return this.$store.getters.user.admin
    },
    hasLanguages () {
      return this.languages != null && this.languages.length > 0
    }
  },
  mounted () {
    this.refreshLanguages()
    this.$on('searchResults', r => this.handleSearchResponse(r))
  }
}

</script>

<style lang="scss">
@import '@/styles/cardChip.scss';
@import '@/styles/main.scss';

.language-chip{
    width: 100px;
}

</style>
