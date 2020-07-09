/* eslint-disable */

<template>
  <v-container>
    <div>
        <p> Languages </p>
        <p> {{ languages }} </p>
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
    getAllLanguages () {
      languages.getLanguages()
        .then(values => { languages = values })
    }
  }
}

</script>

<style lang="scss">

</style>
