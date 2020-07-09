/* eslint-disable */

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
  </v-container>
</template>

<script>
import languages from '@//services/languages.js'

export default {
  name: 'languages',
  data () {
    return {
      languages: null
    }
  },
  methods: {
  },
  mounted () {
    // Promise
    languages.getLanguages()
      .then(values => { this.languages = values.data })
      .catch(error => { console.log(error) })
  }
}

</script>

<style lang="scss">

.language-chip{
    width: 100px;
}

</style>
