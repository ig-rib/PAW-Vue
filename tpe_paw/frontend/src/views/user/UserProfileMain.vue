<template>
  <div id="user-profile-container" v-if="!isAdmin">
    <v-layout justify-center pa-5 v-if="currentAndUnverified">
      <v-flex shrink>
        <v-card class="notice-card">
          <v-card-title>
            {{ $t('user.profile.verifyAccount.title') }}
          </v-card-title>
          <v-card-text class="notice-card-text">
            <p>{{ $t('user.profile.verifyAccount.text1') }}</p>
            <p>
              {{ $t('user.profile.verifyAccount.text2') }}
              <a @click="goToVerify">{{ $t('user.profile.verifyAccount.here') }}</a>
            </p>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
    <v-layout justify-center py-10 mt-4>
      <v-flex shrink>
        <div v-if="!editing">
          <v-img
          v-if="!error_image"
          @error="error_image = true" 
          width="220px"
          height="220px"
          :src="user.icon"
          class="user-profile-profile-pic"
          />
          <v-img
           width="220px"
           height="220px"
           :src="renderableImage"
           class="user-profile-profile-pic"
           v-else></v-img>
        </div>

        <v-btn class="user-profile-profile-pic-btn"
          v-else
          @click="$refs.file.click()">
          <v-img
          width="220px"
          height="220px"
          :src="editableImage"
          class="user-profile-profile-pic"
          />
          <input class="user-profile-photo-input" ref="file" type="file" @change="saveImage" accept="image/*" />
        </v-btn>
      </v-flex>
      <v-flex class="user-profile-info-flex" px-5 sm9 md9 lg9>
        <v-layout align-center wrap class="user-profile-username-layout">
          <v-flex shrink class="user-profile-username">
            {{ user.username }}
          </v-flex>
          <v-flex mx-5 mr-5 pa-1 shrink class="user-profile-reputation">
            {{ user.reputation }}
            <v-icon color="#efb810">
              mdi-star
            </v-icon>
          </v-flex>
          <v-flex>
            <v-layout align-center shrink v-if="isLoggedInUser">            
            <v-btn
              rounded outlined color="primary"
              :ripple="false"
              v-if="!editing"
              @click="editing = true"
            >
              {{ $t('user.profile.editProfile') }}
            </v-btn>
            <v-flex v-else shrink>
               <v-layout >
                <v-flex px-2 shrink>
                  <v-btn
                    rounded outlined color="red"
                    :ripple="false"
                    @click="cancelEditing">
                  {{ $t('user.profile.cancel') }}
                </v-btn>
                </v-flex>
                <v-flex px-2 shrink>
                  <v-btn
                    rounded outlined color="#2286c3"
                    :ripple="false"
                    :disabled="!allRulesAlright"
                    @click="saveChanges">
                  {{ $t('user.profile.done') }}
                  </v-btn>
                </v-flex>
              </v-layout>
            </v-flex>
           
          </v-layout>
          </v-flex>
          
        </v-layout>
        <v-layout column>
          <v-flex pt-1 pb-3 pl-2 class="user-profile-joined" shrink>
            {{ joinedOn }}
          </v-flex>
        </v-layout>
        <v-layout class="user-profile-description-layout">
          <v-flex class="user-profile-description">
            <div v-if="!editing">
              {{ user.description }}
            </div>
            <div v-if="editing">
              <v-textarea
                rounded
                outlined
                no-resize
                :placeholder="$t('user.profile.editDescription')"
                :rules="[rules.description]"
                v-model="description">
              </v-textarea>
            </div>
          </v-flex>
        </v-layout>
      </v-flex>
    </v-layout>
    <v-divider></v-divider>
    <v-layout v-if="!editing">
      <v-tabs class="justify-center user-profile-active-toggle" v-if="isLoggedInUser">
        <v-tab :to="{ name: 'active-snippets' }" @click="$refs.userProfileRouterView.$emit('updated')">{{ $t('user.active') }}</v-tab>
        <v-tab :to="{ name: 'deleted-snippets' }" @click="$refs.userProfileRouterView.$emit('updated')">{{ $t('user.deleted') }}</v-tab>
      </v-tabs>
    </v-layout>
    <v-layout>
      <router-view ref="userProfileRouterView"></router-view>
    </v-layout>
  </div>
  <div v-else>
    <v-layout class="view-title-layout">
      <v-flex shrink class="view-title">
        {{ $t('user.profile.admin') }}
      </v-flex>
    </v-layout>
  </div>
</template>

<script>
import user from '@/services/user.js'
import urls from '@/services/urls.js'
import validations from '@/functions/validations'

  export default {
    title () { return this.$t('titles.profile') },
    data () {
      return {
        editing: false,
        profilePhoto: null,
        imagePreview: null,
        description: '',
        image64: '',
        oldDescription: '',
        user: () => {},
        hasPhotoPreview: false,
        isAdmin: false,
        error_image: false
      }
    },
    methods: {
      saveImage (event) {
        if (event.target.files[0].size > 1048576) {
          this.$store.dispatch('snackError', this.$t('validations.profilePhoto'))
          return
        }
        this.hasPhotoPreview = true
        this.profilePhoto = event.target.files[0]
        const reader = new FileReader()
        const that = this
        reader.onload = function (e) {
          that.imagePreview = e.target.result
          that.image64 = e.target.result.split('base64,').pop()
        }
        reader.readAsDataURL(this.profilePhoto)
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
        user.updateUserData(this.$route.params.id, params).then(r => {
          this.user.description = this.description
          const newUserData = this.$store.getters.user
          newUserData.description = this.description
          newUserData.icon = newUserData.icon + `?${new Date().getTime()}`
          this.$emit('profilePicChanged')
          this.$store.dispatch('setUser', newUserData)
          this.$store.dispatch('snackSuccess', this.$t('user.profile.profileInfoUpdated'))
        })
        .finally(() => {
          this.$refs.userProfileRouterView.$emit('updated')
          this.editing = false
          this.hasPhotoPreview = false
        })
      },
      goToVerify () {
        this.$router.push({
          name: 'send-verification-code'
        })
      }
    },
    computed: {
      renderableImage () {
        return 'https://www.sogapar.info/wp-content/uploads/2015/12/default-user-image.png'
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
          return this.user.icon
        }
      },
      isLoggedInUser () {
        return parseInt(this.$route.params.id) === this.$store.getters.user.id
      },
      currentAndUnverified () {
        return this.isLoggedInUser && !this.$store.getters.user.verified
      },
      rules () {
        return {
          description: () => validations.maxLength(this.description, 300)
        }
      },
      allRulesAlright () {
        return Object.keys(this.rules).filter(rule => this.rules[rule]() !== true).length === 0 && !this.usernameExists && !this.emailExists
      },
      joinedOn () {
        const dat = new Date(this.user.dateJoined)
        return this.$t('user.profile.joined', { day: dat.getDate(), month: dat.getMonth() + 1, year: dat.getFullYear() })
      }
    },
    mounted () {
      // Unconditionally get and store user
      if (this.$route.name === 'deleted-snippets' && parseInt(this.$route.params.id) !== this.$store.getters.user.id) {
        this.$router.replace({
          name: 'active-snippets'
        })
      }
      user.getUser(this.$route.params.id)
        .then(r => {
          this.user = r.data
          this.oldDescription = this.description = this.user.description
          this.isAdmin = r.data.admin
        })
      this.$on('searchResults', (r) => this.$refs.userProfileRouterView.$emit('searchResults', r))
    }
  }
</script>

<style lang="scss">
@import '@/styles/alignmentUtils.scss';
@import '@/styles/noticeCard.scss';

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
    .user-profile-info-flex {
      display: flex;
      flex-direction: column;
      align-items: space-between;
    }
    .user-profile-username-layout {
      flex-grow: 0;
    }
    .user-profile-username {
      font-size: 50px;
      font-weight: 300;
    }
    .user-profile-joined {
      font-size: 16px;
      font-weight: 300;
    }
    .user-profile-reputation {
      font-size: 30px;
      font-weight: 400;
      height: max-content;
      padding: 2px;
      border-radius: 30px;
      background-color: $background;
      border: 2px solid $accent;
      align-items: center;
      display: flex;
      i {
        font-size: 25px;
      }
    }
    .user-profile-description-layout {
      flex-grow: 0;
    }
    .user-profile-description {
      font-size: 25px;
      textarea {
        font-size: 25px;
      }
      font-weight: 300;
    }
    .user-profile-active-toggle {
      background: #fafafa;
      .v-tab {
        background: #fafafa;
      }
    }
  }
</style>
