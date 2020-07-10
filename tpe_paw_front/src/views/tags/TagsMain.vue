<template>
  <v-container>
    TAGS MAIN
  </v-container>
</template>

<script>
import tags from '@/services/tags.js'
import urls from '@/services/urls.js'
import axios from 'axios'

export default {
  mounted () {
    tags.followTag(450, true)
      .then(response => console.log(response))
  }
}
</script>
