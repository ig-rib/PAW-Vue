<template>
  <v-container>
    <!-- Search bar -->
    <v-row>
        <v-col>
            <v-card height="100%" max-width=400>
                <v-row fill-height>
                    <v-col>
                        <v-text-field
                        v-model="languageInput"
                        label="Search for a language"
                        dense
                        rounded
                        hide-details
                        >
                        </v-text-field>
                    </v-col>
                    <v-divider vertical></v-divider>
                    <v-col class="flex-grow-0">
                        <v-btn
                        class="pr-2"
                        height="100%"
                        icon
                        v-on:click="searchLanguage"
                        >
                            <v-icon>mdi-magnify</v-icon>
                        </v-btn>
                    </v-col>
                </v-row>
            </v-card>
        </v-col>
    </v-row>
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
  name: 'languagesSearch',
  data () {
    return {
      languages: null,
      links: 0,
      languageInput: '',
      pagination: {
          page: 1,
          length: 1,
          visible: 7
      }
    }
  },
  methods: {
      searchLanguage: function () {
          languages.searchLanguage(this.pagination.page, this.languageInput, true, false)
          .then(values => {
              this.languages = values.data
              this.links = helpers.parseLinks(values.headers.link)
              this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10);
          })
          .catch(error => { console.log(error) })
      },
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
