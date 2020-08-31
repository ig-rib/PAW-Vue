<template>
  <v-container>
    <div class="text-center">
      <v-pagination
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </div>
    <v-layout>
      <v-layout class="grid-progress-circle" v-if="status === 'l'" justify-center>
        <v-progress-circular
          :size="70"
          :width="7"
          color="primary"
          indeterminate>
        </v-progress-circular>
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
    <div v-if="status !== 'l'" class="text-center">
      <v-pagination
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </div>
  </v-container>
</template>

<script>
import searchService from '@/services/search.js'
import helpers from '@/functions/helpers.js'

export default {
    data () {
    return {
      snippets: [],
      // users: {},
      status: '',
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
    },
    handleSearchResponse (r) {
      this.links = helpers.parseLinks(r.headers.link)
      this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10)
      this.snippets = r.data
      this.status = ''
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
    })
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
</style>