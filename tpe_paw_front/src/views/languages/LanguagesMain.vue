<template>
  <v-container>
    <p>{{$t('languages.title')}}</p>
    <v-layout justify-center mb-10 class="text-center">
      <v-pagination
        v-model="pagination.page"
        v-on:input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
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
      <v-layout v-else wrap justify-center>
        <v-flex my-2
          :class="`lg3 md4 sm4 ${ $vuetify.breakpoint.lgAndUp ? 'px-2' : 'mx-2' } px-2`"
          v-for="lang in languages"
          :key="lang.id">
          <div>
            <v-card elevation="1" class="card-chip" @click="goToLanguageSnippets(lang)">
              <v-layout width="100%">
                <v-flex px-2 class="tag-name-flex">
                  {{ lang.name }}
                </v-flex>
              <!-- <v-chip @click="goToLanguageSnippets(lang)" class="ma-2 language-chip" label>
                {{ lang.name }}
              </v-chip> -->
              </v-layout>
            </v-card>
          </div>
        </v-flex>
      </v-layout>
    </div>
    
  </v-container>
</template>

<script>
import languages from '@/services/languages.js'
import helpers from '@/functions/helpers.js'

export default {
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
      status: 'l'
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
          console.log(error)
          this.status = 'e'
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
          console.log(error)
          this.status = 'e'
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
    }
  },
  mounted () {
    const queryParams = this.$route.query
    languages.searchLanguages(queryParams)
      .then(response => {
        this.pagination.page = parseInt(queryParams.page) || 1
        this.handleSearchResponse(response)
      })
      .catch(error => { 
          console.log(error)
          this.status = 'e'
          })
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
