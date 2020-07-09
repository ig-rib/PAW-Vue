<template>
  <v-container>
    TAGS MAIN
  </v-container>
</template>

<script>
import tags from '@/services/tags.js'
export default {
  mounted () {
    tags.getTags(1)
      .then(response => console.log(response))
  }
}
</script>
