<template>
  <v-container>
    <v-layout>
      <v-flex sm3 md3 lg3>
        <img :src="renderableImage" alt="" srcset="">
      </v-flex>
      <v-flex>
        INFO
      </v-flex>
    </v-layout>
    <v-layout>

    </v-layout>
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
        oldDescription: '',
        user: () => {}
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
        return `data:image/png;base64,${this.user.icon}`
      },
      currentUser () {
        return this.$store.getters.user
      }
    },
    mounted () {
      // Unconditionally get and store user
      console.log(this.$router.currentRoute)
      user.getUser(this.$router.currentRoute.params.id)
        .then(r => { 
          this.user = r.data
          this.oldDescription = this.description = this.$store.getters.user.description
        })
    }
  }
</script>
