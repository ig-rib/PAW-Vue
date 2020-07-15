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
    <v-text-field v-model="description"></v-text-field>
    <v-btn @click="updateDescription">
      UPDATE DESCRIPTION
    </v-btn>
    <input type="file" @change="readImage" accept="image/*">
    {{ image64 }}
    {{ this.$store.getters.user}}
  </v-container>
</template>

<script>
import user from '@/services/user.js'
  export default {
    data () {
      return {
        editing: false,
        image64: '',
        description: '',
        oldDescription: ''
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
      },
      updateDescription () {
        user.updateUserData(75, this.description)
          .then(r => this.updateOldDescription())
          .catch(e => this.resetUserData())
      },
      resetUserData () {
        this.description = this.oldDescription
      },
      updateOldDescription () {
        this.oldDescription = this.description
      }
    },
    computed: {
      renderableImage () {
        return this.image64
      },
      currentUser () {
        return this.$store.getters.user
      }
    },
    mounted () {
      // Unconditionally get and store user
      user.getUser(this.$router.currentRoute.params.id)
        .then(r => { 
          this.$store.dispatch('setUser', r.data)
          this.oldDescription = this.description = this.$store.getters.user.description
        })
    }
  }
</script>
