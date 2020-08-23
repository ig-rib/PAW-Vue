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
      <input name="photo" type="file" @change="readImage">
      <v-btn @click="uploadPhoto"></v-btn>
    </v-layout>
    <v-layout>
      <v-flex md6 lg6>
        {{ profilePhoto }}
      </v-flex>
      <v-flex md6 lg6>
        {{ gottenProfilePhoto }}
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import user from '@/services/user.js'
import urls from '@/services/urls.js'

  export default {
    data () {
      return {
        editing: false,
        profilePhoto: null,
        gottenProfilePhoto: '',
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
          this.profilePhoto = e.target.result
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
        let formData = new FormData()
        formData.append('photo', this.profilePhoto)
        // user.uploadProfilePhoto(75, formData).then(r2 => {
        //   console.log(r2.data)
        // })
        user.uploadProfilePhoto64(75, btoa(this.profilePhoto)).then(r => {
          console.log(r.data)
        })
      },
      updateDescription () {
        user.updateUserData(75, this.description)
          .then(r => this.updateOldDescription())
          .catch(e => this.resetUserData())
      },
      saveImage (e) {
        this.profilePhoto = e.target.files[0]        
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
        return `${this.user.icon}`
      },
      currentUser () {
        return this.$store.getters.user
      },
      profilePhotoUrl () {
        return urls.user.profilePhoto
      }
    },
    mounted () {
      // Unconditionally get and store user
      user.getUser(this.$route.params.id)
        .then(r => { 
          this.user = r.data
          this.oldDescription = this.description = this.user.description
        })
      user.getProfilePhoto(this.$route.params.id)
        .then(r => {
          this.gottenProfilePhoto = r.data
        })
    }
  }
</script>
