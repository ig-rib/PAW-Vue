<template>
  <div class="fill-width">
    <v-layout justify-center mb-10 class="text-center">
    </v-layout>
    <v-layout column align-center justify-center py-10 v-if="snippets.length === 0 && status !== 'l' && inOwnProfileActive">
      <v-flex shrink class="no-snippets-text" mb-10>
        {{ $t('components.snippetGrid.profile.noSnippetsInOwnProfileActive') }}
      </v-flex>
      <v-flex shrink>
        <v-btn :ripple="false" :to="{ name: 'create-snippet' }">
          {{ $t('components.snippetGrid.profile.createOne') }}
        </v-btn>
      </v-flex>
    </v-layout>
    <v-layout align-center justify-center py-10 v-else-if="snippets.length === 0 && status !== 'l' && inAnotherProfile">
      <v-flex shrink class="no-snippets-text">
        {{ $t('components.snippetGrid.profile.noSnippetsInAnotherProfile') }}
      </v-flex>
    </v-layout>
    <v-layout align-center justify-center py-10 v-else-if="snippets.length === 0 && status !== 'l' && inOwnProfileDeleted">
      <v-flex shrink class="no-snippets-text">
        {{ $t('components.snippetGrid.profile.noSnippetsInOwnProfileDeleted') }}
      </v-flex>
    </v-layout>
    <v-layout align-center justify-center py-10 v-else-if="snippets.length === 0 && status !== 'l' && status !== 'e'">
      <v-flex shrink class="no-snippets-text">
        {{ $t('components.snippetGrid.noSnippets') }}
      </v-flex>
    </v-layout>
    <v-layout row wrap justify-center>
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
          <v-btn rounded outlined color="primary" @click="tryLoadingAgain">{{ $t('error.grid.tryAgain') }}</v-btn>
        </v-flex>
      </v-layout>
      <!-- <v-layout v-else justify-center row   wrap>
        <v-flex
            class="snippet-card-flex"
            shrink
            v-for="snippet in snippets"
            d-flex
            child-flex
            :key="snippet.id"
          >
          <snippet
            :snippet="snippet"/>
        </v-flex>
      </v-layout> -->
        <masonry
          class="masonry-flex"
          v-else
          :cols="{default: 4, 1700: 3, 1300: 2, 960: 1}"
          :gutter="{default: '2px', 1600: '2px', 1300: '2px', 960: '2px'}"
          >
          <v-flex shrink class="snippet-flex" v-for="snippet in snippets" :key="snippet.id">
            <snippet :snippet="snippet"/>
          </v-flex>
        </masonry>
    </v-layout>
    <v-layout justify-center mt-10 mb-10 v-if="status === ''" class="text-center">
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
    },
    reloadData () {
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
    this.reloadData()
    const queryParams = this.$route.query
    this.$on('searchResults', r => {
      this.pagination.page = parseInt(queryParams.page) || 1
      this.handleSearchResponse(r)
    })
    this.$on('updated', () => {
      // searchService.searchInLocation(this.$route, queryParams)
      //   .then(r => {
      //     this.pagination.page = parseInt(queryParams.page) || 1
      //     this.handleSearchResponse(r)
      //   })
      //   .catch(error => { 
      //     console.log(error)
      //     this.status = 'e'
      //     })
      this.reloadData()
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
  .masonry-flex {
    // @media screen and (min-width: 1200px) {
    //   min-width: 100%;
    // }
    justify-content: center;
  }
  .masonry-flex > div {
    min-width: min-content !important;
  }
  .snippet-flex {
    display: flex;
    justify-content: center;
    min-width: max-content !important;
  }
</style>
