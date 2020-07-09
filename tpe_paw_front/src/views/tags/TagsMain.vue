<template>
  <v-container>
    TAGS MAIN
  </v-container>
</template>

<script>
import tags from '@/services/tags.js'
export default {
  mounted () {
    tags.getTags()
      .then(response => console.log(response))
  }
}
</script>
