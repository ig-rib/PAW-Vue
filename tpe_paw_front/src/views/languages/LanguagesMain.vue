<template>
  <v-container>
    <div>
        <p> Languages </p>
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
import languages from '@//services/languages.js'
import helpers from '@//functions/helpers.js'

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
      paginationChange: function () {
          languages.getLanguages(this.pagination.page)
            .then(values => {
                this.languages = values.data 
                this.links = helpers.parseLinks(values.headers.link)
            })
            .catch(error => { console.log(error) })
      }
  },
  computed: {
  },
  mounted () {
    // Promise
    languages.getLanguages(this.pagination.page)
      .then(values => {
        this.languages = values.data
        this.links = helpers.parseLinks(values.headers.link)
        this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10);
      })
      .catch(error => { console.log(error) })
  }
}

</script>

<style lang="scss">

.language-chip{
    width: 100px;
}

</style>
