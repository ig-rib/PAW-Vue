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
    tags.followTag(450)
      .then(r => console.log(r))
  }
}
</script>
