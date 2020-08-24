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
      <v-layout v-if="status !== 'loading'" row>
        <v-flex md4 sm4 v-for="snippet in completeSnippets" :key="snippet.id">
          <snippet
            :snippetData="snippet"/>
        </v-flex>
      </v-layout>
    </v-layout>
    <div class="text-center">
      <v-pagination
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </div>
    {{ $router.query }}
  </v-container>
</template>

<script>
import searchService from '@/services/search.js'
import axiosFetcher from '@/services/axiosFetcher.js'
import helpers from '@/functions/helpers.js'

export default {
    data () {
    return {
      snippets: [],
      users: {},
      status: '',
      pagination: {
        page: 1,
        length: 1,
        visible: 7
      }
    }
  },
  computed: {
    completeSnippets () {
      return this.snippets.map(x => {
        let newObj = {}
        Object.assign(newObj, x)
        // TODO error-check this
        let ownerId = parseInt(x.owner.split('/').pop())
        newObj.owner = this.users.filter(x => x.id === ownerId)[0]
        return newObj
      })
    }
  },
  methods: {
    paginationChange () {
      const queryParams = {}
      Object.assign(queryParams, this.$route.query)
      queryParams.page = this.pagination.page
      console.log('snippet-grid paginationchange queryparams:', queryParams)
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
      // get unique user links and map them to promises
      let promises = r.data
        .map(x => x.owner)
        .filter((item, pos, array) => !pos || item !== array[pos-1])
        .map(uri => axiosFetcher.get(uri))
      return Promise.all(promises)
        .then(r => {
          this.users = r.map(x => x.data)
        })
        .finally(() => {
          this.status = ''
        })
    }
  },
  mounted () {
    const queryParams = this.$route.query
    this.status = 'loading'
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