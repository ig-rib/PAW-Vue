<template>
  <v-container>
    <div>
      <p>{{$t('languages.title')}}</p>
      <v-row>
        <v-col v-for="lang in languages" :key="lang.id">
          <div>
            <v-chip class="ma-2 language-chip" label>{{ lang.name }}</v-chip>
          </div>
        </v-col>
      </v-row>
    </div>
    <div class="text-center">
      <v-pagination
        v-model="pagination.page"
        v-on:input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
        circle
      ></v-pagination>
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
      }
    }
  },
  methods: {
    paginationChange () {
      const queryParams = {}
      Object.assign(queryParams, this.$route.query)
      queryParams.page = this.pagination.page
      languages.searchLanguages(queryParams)
        .then(values => {
          this.languages = values.data 
          this.links = helpers.parseLinks(values.headers.link)
        })
        .catch(error => { console.log(error) })
    },
    handleSearchResponse (response) {
      this.languages = response.data
      this.links = helpers.parseLinks(response.headers.link)
      this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10);
    }
  },
  mounted () {
    const queryParams = this.$route.query
    languages.searchLanguages(queryParams)
      .then(response => {
        this.pagination.page = parseInt(queryParams.page) || 1
        this.handleSearchResponse(response)
      })
    this.$on('searchResults', r => this.handleSearchResponse(r))
  }
}

</script>

<style lang="scss">

.language-chip{
    width: 100px;
}

</style>
