/* eslint-disable */

<template>
  <v-container>
    <div>
        <p> Languages </p>
        <p> {{links}} </p>
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
        v-model="page"
        :length="3"
      ></v-pagination>
    </div>
  </v-container>
</template>

<script>
import languages from '@//services/languages.js'

export default {
  name: 'languages',
  data () {
    return {
      languages: null,
      links: 0,
      page: 1,
    }
  },
  methods: {
  },
  computed: {
  },
  watch: {
      page: function (){
        anguages.getLanguages()
        .then(values => {
        this.languages = values.data 

        // Parse link data
        const linkHeaders = values.headers.link
        this.links = linkHeaders.split(',').reduce((acc, link) => {
            const match = link.match(/<(.*)>; rel="(\w*)"/)
            const url = match[1]
            const rel = match[2]
            acc[rel] = url
            return acc;
        }, {})
        })
      .catch(error => { console.log(error) })
      }
  },
  mounted () {
    // Promise
    languages.getLanguages()
      .then(values => {
        this.languages = values.data 

        // Parse link data
        const linkHeaders = values.headers.link
        this.links = linkHeaders.split(',').reduce((acc, link) => {
            const match = link.match(/<(.*)>; rel="(\w*)"/)
            const url = match[1]
            const rel = match[2]
            acc[rel] = url
            return acc;
        }, {})
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
