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
    <v-layout>
    </v-layout>
    <v-divider></v-divider>
    <v-layout v-if="!editing">
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
        imagePreview: null,
        description: '',
        image64: '',
        oldDescription: '',
        user: () => {},
        hasPhotoPreview: false
      }
    },
    methods: {
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
      saveImage (event) {
        this.hasPhotoPreview = true
        this.profilePhoto = event.target.files[0]
        console.log(this.profilePhoto)
        const reader = new FileReader()
        const that = this
        reader.onload = function (e) {
          that.imagePreview = e.target.result
          that.image64 = e.target.result.split('base64,').pop()
        }
        reader.readAsDataURL(this.profilePhoto)
        console.log(this.profilePhoto, btoa(this.profilePhoto))
      },
      cancelEditing () {
        this.imagePreview = null
        this.profilePhoto = null
        this.editing = false
        this.hasPhotoPreview = false
        this.description = this.user.description
      },
      saveChanges () {
        const params = {
          encodedPhoto: this.image64,
          description: this.description
        }
        console.log(params)
        user.updateUserData(this.$route.params.id, params).then(r => {
          this.user.descrpition = this.description
        })
        .finally(() => {
          this.editing = false
          this.hasPhotoPreview = false
        })
      }
    },
    computed: {
      renderableImage () {
        console.log(this.user.icon)
        return `${this.user.icon}`
      },
      currentUser () {
        return this.$store.getters.user
      },
      profilePhotoUrl () {
        return urls.user.profilePhoto
      },
      editableImage () {
        if (this.hasPhotoPreview && this.imagePreview != null) {
          return this.imagePreview
        } else {
          return this.renderableImage
        }
      }
    },
    mounted () {
      // Unconditionally get and store user
      user.getUser(this.$route.params.id)
        .then(r => {
          console.log('userData', r.data)
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
