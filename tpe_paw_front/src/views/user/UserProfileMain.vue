<template>
  <v-container id="user-profile-container">
    <v-layout>
      <v-flex shrink>
        <v-img
          v-if="!editing"
          width="150px"
          height="150px"
          :src="renderableImage"
          class="user-profile-profile-pic"
          />
        <v-btn class="user-profile-profile-pic-btn"
          v-else
          @click="$refs.file.click()">
          <v-img
          width="150px"
          height="150px"
          :src="editableImage"
          class="user-profile-profile-pic"
          />
          <input class="user-profile-photo-input" ref="file" type="file" @change="saveImage" accept="image/*" />
        </v-btn>
      </v-flex>
      <v-flex pt-5 px-5 sm9 md9 lg9>
        <v-layout>
          <v-flex grow>
            {{ user.username }}
          </v-flex>
          <v-flex shrink>            
            <v-btn
              v-if="!editing"
              @click="editing = true"
            >
              {{ $t('user.profile.editProfile') }}
            </v-btn>
            <v-layout v-else>
              <v-flex>
                <v-btn
                  @click="cancelEditing">
                {{ $t('user.profile.cancel') }}
              </v-btn>
              </v-flex>
              <v-flex>
                <v-btn
                  @click="saveChanges">
                {{ $t('user.profile.done') }}
              </v-btn>
              </v-flex>
            </v-layout>
          </v-flex>
        </v-layout>
        <v-layout mt-10>
          <v-flex>
            <div v-if="!editing">
              {{ user.description }}
            </div>
            <div v-if="editing">
              <v-text-field v-model="description">
              </v-text-field>
            </div>
          </v-flex>
        </v-layout>
      </v-flex>
    </v-layout>
    <v-divider></v-divider>
    <v-layout v-if="!editing">

    </v-layout>
    <img :src="profilePhoto"/>
    {{profilePhoto}}
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
        description: '',
        oldDescription: '',
        user: () => {},
        hasPhotoPreview: false
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
        user.uploadProfilePhoto64(this.$route.params.id, btoa(this.profilePhoto)).then(r => {
          console.log(r.data)
        })
      },
      updateDescription () {
        user.updateUserData(75, this.description)
          .then(r => this.updateOldDescription())
          .catch(e => this.resetUserData())
      },
      saveImage (e) {
        this.hasPhotoPreview = true
        this.profilePhoto = e.target.files[0]        
      },
      resetUserData () {
        this.description = this.oldDescription
      },
      updateOldDescription () {
        this.oldDescription = this.description
      },
      cancelEditing () {

      },
      saveChanges () {

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
      },
      editableImage () {
        if (this.hasPhotoPreview) {
          let fr = new FileReader()
          return fr.readAsDataURL(this.profilePhoto)
        } else {
          return this.renderableImage
        }
      }
    },
    mounted () {
      // Unconditionally get and store user
      user.getUser(this.$route.params.id)
        .then(r => { 
          this.user = r.data
          this.oldDescription = this.description = this.user.description
        })
    }
  }
</script>

<style lang="scss">
  #user-profile-container {
    .user-profile-profile-pic {
      border-radius: 150px;
    }
    .user-profile-profile-pic-btn {
      height: max-content;
      width: max-content;
      padding: 0px;
      border-radius: 150px;
    }
    .user-profile-photo-input {
      display: none;
    }
  }
</style>
