<template>
  <div class="fill-width">
    <v-layout justify-center mb-10 class="text-center">
      <v-pagination
        v-if="pagination.length > 1"
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </v-layout>
    <v-layout column align-center justify-center py-10 v-if="snippets.length === 0 && inOwnProfileActive">
      <v-flex shrink class="no-snippets-text" mb-10>
        {{ $t('components.snippetGrid.profile.noSnippetsInOwnProfileActive') }}
      </v-flex>
      <v-flex shrink>
        <v-btn :ripple="false" :to="{ name: 'create-snippet' }">
          {{ $t('components.snippetGrid.profile.createOne') }}
        </v-btn>
      </v-flex>
    </v-layout>
    <v-layout align-center justify-center py-10 v-else-if="snippets.length === 0 && inAnotherProfile">
      <v-flex shrink class="no-snippets-text">
        {{ $t('components.snippetGrid.profile.noSnippetsInAnotherProfile') }}
      </v-flex>
    </v-layout>
    <v-layout align-center justify-center py-10 v-else-if="snippets.length === 0 && inOwnProfileDeleted">
      <v-flex shrink class="no-snippets-text">
        {{ $t('components.snippetGrid.profile.noSnippetsInOwnProfileDeleted') }}
      </v-flex>
    </v-layout>
    <v-layout align-center justify-center py-10 v-else-if="snippets.length === 0 && status !== 'l' && status !== 'e'">
      <v-flex shrink class="no-snippets-text">
        {{ $t('components.snippetGrid.noSnippets') }}
      </v-flex>
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
      <v-layout v-else justify-center wrap>
        <v-flex

            class="snippet-card-flex"
            shrink
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
        v-if="snippets.length > 0"
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </v-layout>
  </div>
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
  computed: {
    inOwnProfileActive () {
      return this.$route.name === 'active-snippets' && 
            parseInt(this.$route.params.id) === this.$store.getters.user.id
    },
    inOwnProfileDeleted () {
      return this.$route.name === 'deleted-snippets' && 
            parseInt(this.$route.params.id) === this.$store.getters.user.id
    },
    inAnotherProfile () {
      return this.$route.name === 'active-snippets' && parseInt(this.$route.params.id) !== this.$store.getters.user.id
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
  .no-snippets-text {
    font-size: 30px;
    font-weight: 300;
  }
</style>
