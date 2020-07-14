<template>
  <v-container>
    <img :src="`data:image/*;base64,${renderableImage}`" alt="" srcset="">
    <v-btn @click="getActiveUserSnippets">
      GET ACTIVE USER SNIPPETS
    </v-btn>
    <v-btn @click="getDeletedUserSnippets">
      GET DELETED USER SNIPPETS
    </v-btn>
    <v-btn @click="getUserEntity">
      GET USER ENTITY
    </v-btn>
    <v-btn @click="uploadPhoto">
      UPLOAD PHOTO
    </v-btn>
    <input type="file" @change="readImage" accept="image/*">
    {{ image64 }}
  </v-container>
</template>

<script>
import user from '@/services/user.js'
  export default {
    data () {
      return {
        editing: false,
        image64: ''
      }
    },
    methods: {
      readImage (event) {
        const selectedImage = event.target.files[0]
        const reader = new FileReader()
        reader.onload = (e) => {
          this.image64 = btoa(e.target.result)
        }
        reader.readAsBinaryString(selectedImage)
      },
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
      },
      uploadPhoto () {
        user.uploadProfilePhoto(75, this.image64)
          .then()
      }
    },
    computed: {
      renderableImage () {
        return this.image64
      }
    },
    mounted () {
      // user.getUserEntity(this.$router.)
    }
  }
</script>
