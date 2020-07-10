<template>
  <v-container>
    TAGS MAIN
  </v-container>
</template>

<script>
import tags from '@/services/tags.js'
import registration from '@/services/registration.js'
import urls from '@/services/urls.js'
import axios from 'axios'

export default {
  mounted () {
    registration.login('JaneRoe', 'password')
      .then(r => console.log(r))
    tags.deleteTag(450)
      .then(r => console.log(r))
    tags.searchTags(1, 'gre', false, false)
      .then(r => console.log(r))
  }
}
</script>
