<template>
  <v-container>
    <v-btn @click="getActiveUserSnippets">
      GET ACTIVE USER SNIPPETS
    </v-btn>
    <v-btn @click="getDeletedUserSnippets">
      GET DELETED USER SNIPPETS
    </v-btn>
    <v-btn @click="getUserEntity">
      GET USER ENTITY
    </v-btn>
    <v-btn>
      UPLOAD PHOTO
    </v-btn>
  </v-container>
</template>

<script>
import user from '@/services/user.js'
  export default {
    data () {
      return {
        editing: false
      }
    },
    methods: {
      getActiveUserSnippets () {
        user.getActiveUserSnippets(75)
          .then(r => { this.editing = true })
      },
      getDeletedUserSnippets () {
        user.getDeletedUserSnippets(75)
          .then(r => { this.editing = true })
      },
      getUserEntity () {
        user.getUser(75)
          .then(r => { this.editing = true })
      }
      // uploadPhoto () {
      //   user.uploadPhoto(75, '')
      //     .then()
      // }
    }
  }
</script>
