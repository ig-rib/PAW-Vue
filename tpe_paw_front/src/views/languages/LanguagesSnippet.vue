<template>
  <v-container>
      <v-row>
        <v-col v-for="snippet in snippets" :key="snippet.id">
          <SnippetComponent
            :title="snippet.title"
            :description="snippet.description"
            :code="snippet.code"
            :language="snippet.language.name"
            :owner="snippet.owner.username"
            :date="snippet.dateCreated"  
          />
          <!-- TODO: format date correctly-->
        </v-col>
      </v-row>
      <div class="text-center">
        <v-pagination
          v-model="pagination.page"
          :length="pagination.length" 
          :total-visible="pagination.visible"
          circle
        ></v-pagination>
      </div>
  </v-container>
</template>

<script>
import SnippetComponent from '@//components/Snippet.vue'
import languages from '@//services/languages.js'
import helpers from '@//functions/helpers.js'

export default {
    name: 'languagesSnippet',
    components: {
        SnippetComponent
    },
    data () {
    return {
      langId: -1,
      language: null,
      snippets: null,
      pagination: {
          page: 1,
          length: 1,
          visible: 7
      }
      
    }
  },
  mounted () {
    this.langId = this.$route.params.id
    languages.getSnippetsForLanguage(this.langId, this.pagination.page)
      .then(values => {
        this.snippets = values.data
        this.links = helpers.parseLinks(values.headers.link)
        this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10);
      })
      .catch(error => { console.log(error) })
  },
  watch: {
      pagination: {
          handler: function () {
            // TODO: change 1 for id
            languages.getSnippetsForLanguage(this.langId, this.pagination.page)
            .then(values => {
                this.snippets = values.data 
                this.links = helpers.parseLinks(values.headers.link)
                this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10);
            })
            .catch(error => { console.log(error) })
          },
          deep: true
      }
  }

}

</script>

<style lang="scss">

</style>
