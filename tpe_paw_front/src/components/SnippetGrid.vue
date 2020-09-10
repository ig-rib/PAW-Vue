<template>
  <v-container>
    <v-layout justify-center mb-10 class="text-center">
      <v-pagination
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </v-layout>
    <v-layout>
      <v-layout class="grid-progress-circle" v-if="status === 'l'" justify-center>
        <v-progress-circular
          :size="70"
          :width="7"
          color="primary"
          indeterminate>
        </v-progress-circular>
      </v-layout>
      <v-layout 
        class="grid-error-layout"
        align-center
        column
        justify-center
        v-else-if="status === 'e'">
        <v-flex my-2 shrink>
          {{ $t('error.grid.snippets') }}
        </v-flex>
        <v-flex my-2>
          <v-btn @click="tryLoadingAgain">{{ $t('error.grid.tryAgain') }}</v-btn>
        </v-flex>
      </v-layout>
      <v-layout v-else row wrap justify-center>
        <v-flex shrink
            v-for="snippet in snippets"
            :key="snippet.id"
          >
          <snippet
            :snippet="snippet"/>
        </v-flex>
      </v-layout>
    </v-layout>
    <v-layout justify-center mt-10 v-if="status === ''" class="text-center">
      <v-pagination
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </v-layout>
  </v-container>
</template>

<script>
import searchService from '@/services/search.js'
import helpers from '@/functions/helpers.js'

export default {
    data () {
    return {
      snippets: [],
      status: 'l',
      pagination: {
        page: 1,
        length: 1,
        visible: 7
      }
    }
  },
  methods: {
    paginationChange () {
      this.status = 'l'
      const queryParams = {}
      Object.assign(queryParams, this.$route.query)
      queryParams.page = this.pagination.page
      this.$router.replace({
        query: queryParams
      })
      searchService.searchInLocation(this.$route, queryParams)
        .then(r => {
          this.handleSearchResponse(r)
        })
        .catch(error => { 
          console.log(error)
          this.status = 'e'
          })
    },
    handleSearchResponse (r) {
      this.links = helpers.parseLinks(r.headers.link)
      this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10)
      console.log(r.data)
      this.snippets = r.data
      this.status = ''
    },
    tryLoadingAgain () {
      this.status = 'l'
      const queryParams = {}
      Object.assign(queryParams, this.$route.query)
      queryParams.page = this.pagination.page
      searchService.searchInLocation(this.$route, queryParams)
        .then(values => {
          this.handleSearchResponse(values)
        })
        .catch(error => { 
          console.log(error)
          this.status = 'e'
          })
    }
  },
  mounted () {
    const queryParams = this.$route.query
    this.status = 'l'
    searchService.searchInLocation(this.$route, queryParams)
      .then(r => {
        this.pagination.page = parseInt(queryParams.page) || 1
        this.handleSearchResponse(r)
        })
      .catch(error => { 
          console.log(error)
          this.status = 'e'
          })
    this.$on('searchResults', r => {
      this.pagination.page = parseInt(queryParams.page) || 1
      this.handleSearchResponse(r)
    })
    this.$on('updated', () => {
      searchService.searchInLocation(this.$route, queryParams)
        .then(r => {
          this.pagination.page = parseInt(queryParams.page) || 1
          this.handleSearchResponse(r)
        })
        .catch(error => { 
          console.log(error)
          this.status = 'e'
          })
    })
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
</style>
