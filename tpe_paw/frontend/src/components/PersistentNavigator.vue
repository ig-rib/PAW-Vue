<template>
  <div id="persistent-navigator-div">
    <v-app-bar
      pa-0
      color="#dee2e6"
      elevate-on-scroll
      scroll-target="#scrolling-techniques-7"
      height="68px"
    >
      <v-layout fill-height class="nav-layout">
        <!-- Hamburger button for navdrawer -->
        <v-flex pl-1 shrink v-if="$vuetify.breakpoint.mdAndDown">
          <v-app-bar-nav-icon @click="navDrawer = !navDrawer"></v-app-bar-nav-icon>
        </v-flex>
        <!-- Title -->
        <v-flex class="title-flex" md1 sm1 v-if="$vuetify.breakpoint.lgAndUp">
          <v-btn min-width="max-content" @click="goHome" :ripple="false" depressed class="title-btn">
            {{ $t('snippit') }}
          </v-btn>
        </v-flex>

        <!-- Navigation for large screens -->

        <v-flex v-if="$vuetify.breakpoint.lgAndUp" shrink>
          <v-layout fill-height class="pt-0">
            <v-flex
              v-for="item in generalPathsNoFeed"
              :key="item.title"
            >
              <v-btn
                :ripple="false"
                elevation="0"
                :to="item.path"
                class="nav-button"
              >
                <v-layout column align-center>
                  <v-flex>
                      <v-icon>{{ item.icon }}</v-icon>
                  </v-flex>
                  <v-flex>
                    <div>{{ item.title }}</div>
                  </v-flex>
                </v-layout>
              </v-btn>
            </v-flex>
            <v-menu v-if="$store.getters.loggedIn" offset-y>
              <template v-slot:activator="{ on }">
                <v-btn
                :ripple="false"
                elevation="0"
                class="nav-button"
                v-on="on"
              >
                <v-layout column align-center>
                  <v-flex>
                    <v-icon>mdi-heart-box-outline</v-icon>
                  </v-flex>
                  <v-flex>
                    <div>{{ $t('navigation.forUser') }}</div>
                  </v-flex>
                </v-layout>
              </v-btn>
              </template>
              <v-list>
                <v-list-item
                  v-for="item in loggedInPaths"
                  :key="item.title"
                  :to="item.path">
                  <v-list-item-title>{{ item.title }}</v-list-item-title>
                </v-list-item>
                <v-list-item
                  v-if="isAdmin"
                  :to="{ name: 'flagged' }"
                >
                  <v-list-item-title>{{ $t('admin.flagged') }}</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </v-layout>
        </v-flex>

        <!-- Search bar -->
        <v-flex v-if="!inExplore" id="search-bar-flex" px-2>
          <v-layout justify-start>
            <v-flex>
              <v-card height="70%" width="100%">
                <v-layout fill-height>
                  <v-flex>
                    <v-text-field
                      @keyup.enter="search"
                      :placeholder="searchBarHint"
                      height="100%"
                      class="nav-search-text-field"
                      dense
                      rounded
                      hide-details
                      v-model="searchQuery">
                    </v-text-field>
                  </v-flex>
                  <v-divider vertical></v-divider>
                  <v-flex shrink>
                    <v-btn
                      height="100%"
                      @click="search"
                      class="search-btn"
                      :ripple="false"
                      icon>
                      <v-icon>mdi-magnify</v-icon>
                    </v-btn>
                  </v-flex>
                </v-layout>
              </v-card>
            </v-flex>
            <template v-if="resultType == 'snippet'">
              <v-flex class="search-select-flex" shrink px-2>
                <v-select
                hide-details
                :items="searchTypes"
                item-text="name"
                item-value="value"
                v-model="searchType"
                :label="$t('search.selectType')"
                dense
                solo
                flat></v-select>
              </v-flex>
              <v-flex class="search-select-flex" shrink px-2>
                <v-select
                :items="searchOrders"
                item-text="name"
                item-value="value"
                :label="$t('search.selectOrder')"
                v-model="searchOrder"
                dense
                solo
                flat></v-select>
              </v-flex>
            </template>
            <template v-else>
              <!-- <v-flex>
                <v-layout column> -->
              <v-flex px-2 shrink class="search-checkbox">
                <v-checkbox
                  :ripple="false"
                  v-model="showEmpty"
                  :label="$t('search.showEmpty')"></v-checkbox>
              </v-flex>
              <v-flex px-2 shrink class="search-checkbox" v-if="resultType == 'tag' && $store.getters.loggedIn">
                <v-checkbox
                  :ripple="false"
                  v-model="showOnlyFollowing"
                  :label="$t('search.showOnlyFollowing')"
                  ></v-checkbox>
              </v-flex>
                <!-- </v-layout>
              </v-flex> -->
            </template>
          </v-layout>
        </v-flex>
        <v-flex ml-auto shrink v-if="$store.getters.loggedIn">
          <template v-if="isAdmin">
            <v-menu offset-y>
              <template v-slot:activator="{ on }">
                <v-btn
                elevation="0"
                class="nav-button"
                v-on="on"
                icon
              >
                <v-icon>mdi-plus</v-icon>
              </v-btn>
              </template>
              <v-list>
                <v-list-item
                  :to="{ name: 'add-tags'}">
                  <v-list-item-title>{{ $t('admin.addTags.addTags') }}</v-list-item-title>
                </v-list-item>
                <v-list-item
                  :to="{ name: 'add-languages'}">
                  <v-list-item-title>{{ $t('admin.addLanguages.addLanguages') }}</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
            
          </template>
          <template v-else>
            <v-btn 
              class="create-snippet-btn"
              rounded
              color="accent"
              :ripple="false"
              v-if="$vuetify.breakpoint.lgAndUp"
              :to="{ name: 'create-snippet' }">
              {{ $t('snippets.createSnippet.createSnippet') }}
            </v-btn>
            <v-btn v-else icon :to="{ name: 'create-snippet' }">
              <v-icon>mdi-plus</v-icon>
            </v-btn>
          </template>
        </v-flex>
        <!-- Registration/Login/User section -->
        <v-flex :class="`${(!$store.getters.loggedIn) ? 'ml-auto' : ''}`" shrink v-if="$vuetify.breakpoint.lgAndUp">
          <v-layout v-if="!$store.getters.loggedIn && !inRegistrationPage">
            <v-flex ma-2 shrink>
              <v-btn class="white--text" color="accent" :ripple="false" @click="goToLogin">{{ $t('registration.login') }}</v-btn>
            </v-flex>
          </v-layout>
          <v-layout pl-2 v-if="$store.getters.loggedIn">
            <v-flex v-if="!isAdmin" class="navbar-profile-flex" shrink>
              <v-layout>
                <v-flex
                  v-ripple
                  class="navbar-profile-clickable-flex"
                  :to="{ name: 'user-profile', params: { id: $store.getters.user.id } }"
                  replace>
                  <v-img v-if="!profileImageError" @error="profileImageError = true" width="40px" height="40px" class="navbar-profile-circle" :src="$store.getters.user.icon"/>
                  <v-icon v-else>mdi-account-circle</v-icon>  
                </v-flex>
              </v-layout>
            </v-flex>
            <v-flex ml-2 shrink>
              <v-btn x-large icon @click="logout">
                <v-icon>mdi-logout</v-icon>
              </v-btn>
            </v-flex>
          </v-layout>
        </v-flex>
      </v-layout>
    </v-app-bar>

    <!-- Navigation Drawer -->
    
    <v-navigation-drawer v-if="$vuetify.breakpoint.mdAndDown"
      disable-resize-watcher
      absolute
      temporary
      v-model="navDrawer">
      <v-layout justify-center align-center class="drawer-title-layout">
        {{ $t('snippit') }}
      </v-layout>
        <v-list>
          <v-list-item
            color="primary"
            v-for="item in generalPaths"
            :key="item.title"
            :to="item.path"
          >
            <v-layout>
              <v-flex shrink>
                <v-icon>{{ item.icon }}</v-icon>
              </v-flex>

              <v-flex pl-5>
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-flex>
            </v-layout>
          </v-list-item>
        </v-list>
      <v-divider></v-divider>
      <v-list class="pt-0" v-if="$store.getters.loggedIn">
        <v-list-item
          :to="{ name: 'flagged' }"
          v-if="isAdmin">
          <v-layout>
              <v-flex shrink>
                <v-icon>mdi-flag</v-icon>
              </v-flex>
              <v-flex pl-5>
                <v-list-item-title>{{ $t('admin.flagged') }}</v-list-item-title>
              </v-flex>
            </v-layout>
        </v-list-item>
        <v-list-item
          color="primary"
          v-for="item in loggedInPaths"
          :key="item.title"
          :to="item.path">
          <v-layout>
              <v-flex shrink>
                <v-icon>{{ item.icon }}</v-icon>
              </v-flex>
              <v-flex pl-5>
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-flex>
            </v-layout>
        </v-list-item>
      </v-list>
    <v-divider></v-divider>
    <v-layout my-2 v-if="!$store.getters.loggedIn && !inRegistrationPage">
      <v-flex ma-2>
        <v-btn class="white--text" color="accent" :ripple="false" @click="goToLogin">{{ $t('registration.login') }}</v-btn>
      </v-flex>
    </v-layout>
    <v-layout pl-2 my-2 v-if="$store.getters.loggedIn">
      <v-flex v-if="!isAdmin" class="navbar-profile-flex" shrink>
        <v-layout>
          <v-flex
            v-ripple
            class="navbar-profile-clickable-flex"
            :to="{ name: 'user-profile', params: { id: $store.getters.user.id } }"
            replace>
            <v-img v-if="!profileImageError" @error="profileImageError = true" width="40px" height="40px" class="navbar-profile-circle" :src="$store.getters.user.icon"/>
            <v-icon v-else>mdi-account-circle</v-icon>  
          </v-flex>
        </v-layout>
      </v-flex>
      <v-flex ml-2 shrink>
        <v-btn x-large icon @click="logout">
          <v-icon>mdi-logout</v-icon>
        </v-btn>
      </v-flex>
    </v-layout>
    </v-navigation-drawer>
    <div class="px-10">
      <router-view ref="persistentNavigatorRouterView"></router-view>
    </div>
  </div>
</template>

<script>
import search from '@/services/search.js'
import tags from '@/services/tags.js'
import languages from '@/services/languages.js'

export default {
  data () {
    return {
      navDrawer: false,
      searchQuery: '',
      searchType: '',
      searchOrder: '',
      showEmpty: false,
      showOnlyFollowing: false,
      generalPaths: [
        {
          title: this.$t('feed.title'),
          icon: 'mdi-home',
          path: {
            name: 'feed'
          }
        },
        {
          title: this.$t('explore.explore'),
          icon: 'mdi-magnify',
          path: {
            name: 'explore'
          }
        },
        {
          title: this.$t('languages.title'),
          icon: 'mdi-language-python  ',
          path: {
            name: 'languages-main',
            query: {
              showEmpty: false
            }
          }
        },
        {
          title: this.$t('tags.title'),
          icon: 'mdi-tag-outline',
          path: {
            name: 'tags-main',
            query: {
              showEmpty: false
            }
          }
        }
      ],
      loggedInPaths: [
        {
          title: this.$t('user.following.following'),
          icon: 'mdi-tag-heart',
          path: {
            name: 'following'
          }
        },
        {
          title: this.$t('user.favorites.favorites'),
          icon: 'mdi-heart',
          path: {
            name: 'favorites'
          }
        },
        {
          title: this.$t('user.upvoted.upvoted'),
          icon: 'mdi-thumb-up',
          path: {
            name: 'upvoted'
          }
        }
      ],
      profileImageError: false
    }
  },
  computed: {
    searchTypes () {
      return Object.values(search.constants.type)
    },
    searchOrders () {
      return Object.values(search.constants.order)
    },
    resultType () {
      const routeName = this.$route.name
      switch (routeName) {
        case 'languages-main':
          return 'language'
        case 'tags-main':
          return 'tag'
        default:
          return 'snippet'
      }
    },
    generalPathsNoFeed () {
      return this.generalPaths.filter(item => item.path.name !== 'feed')
    },
    isAdmin () {
      return this.$store.getters.user.admin
    },
    searchBarHint () {
      let params = {}
      switch (this.resultType) {
        case 'snippet':
          params = { objectType: 'snippets' }
          break;
        case 'tag':
          params = { objectType: 'tags' }
          break;
        case 'language':
          params = { objectType: 'languages' }
          break;
      }
      return this.$t('search.searchBarHint', params)
    },
    inRegistrationPage () {
      return this.$route.path.includes('registration')
    },
    inExplore () {
      return this.$route.path.includes('explore')
    }
  },
  methods: {
    goHome () {
      this.$router.push({
        name: 'feed'
      })
    },
    search () {
      let params
      if (this.resultType === 'snippet') {
        params = {
          t: this.searchType,
          s: this.searchOrder
        }
      } else {
        params = {
          showEmpty: this.showEmpty,
          showOnlyFollowing: this.showOnlyFollowing
        }
      }
      params.q = this.searchQuery
      params.page = 1
      this.performSearch(params)
    },
    performSearch (params) {
      this.$router.replace({
        query: params
      })
      switch (this.resultType) {
        case 'snippet':
          return search.searchInLocation(this.$route, params)
            .then(r => {
              this.$refs.persistentNavigatorRouterView.$emit('searchResults', r)
            })
        case 'tag':
          return tags.searchTags(params)
            .then(r => {
              this.$refs.persistentNavigatorRouterView.$emit('searchResults', r)
            })
        case 'language':
          return languages.searchLanguages(params)
            .then(r => {
                this.$refs.persistentNavigatorRouterView.$emit('searchResults', r)
              })
      }
    },
    goToLogin () {
      this.$router.push({
        name: 'login'
      })
    },
    goToProfile () {
      this.$router.push({
        name: 'user-profile',
        params: {
          id: this.$store.getters.user.id
        }
      })
    },
    logout () {
      window.localStorage.removeItem('token')
      window.localStorage.removeItem('keepSignedIn')
      this.$store.dispatch('logout')
      this.$router.go({ name: 'nothing' })
    },
    goToAdd () {
      if (this.isAdmin) {
        this.$router.push({
          name: 'add-tags-or-languages'
        })
      } else {
        this.$router.push({
          name: 'create-snippet'
        })
      }
    }
  },
  // Decided to update contents as soon as checkboxes are
  // clicked, since user would need to re-enter the search
  // query (including the 'empty query' case) otherwise, making it
  // less friendly.
  watch: {
    showEmpty: {
      handler: function (newVal, oldVal) {
        if (newVal !== oldVal) {
          const params = {}
          Object.assign(params, this.$route.query)
          params.showEmpty = newVal
          this.performSearch(params)
            .then(r => {})
            .catch(e => {
              this.showEmpty = oldVal
              this.$store.dispatch('snackError', e.response.data.message) 
            })
        }
      }
    },
    showOnlyFollowing: {
      handler: function (newVal, oldVal) {
        const params = {}
        Object.assign(params, this.$route.query)
        params.showOnlyFollowing = newVal
        params.page = 1
        this.performSearch(params)
          .then(r => {})
          .catch(e => {
            this.showEmpty = oldVal
            this.$store.dispatch('snackError', e.response.data.message) 
          })
      }
    },
    $route: function (neww, old) {
      if (neww.path !== old.path) {
        this.showEmpty = this.$route.query.showEmpty || false
        this.showOnlyFollowing = this.$route.query.showOnlyFollowing || false
        this.searchQuery = ''
      }
    }
  },
  mounted () {
    const query = this.$store.query
    if (query != null) {
      if (query.showEmpty === false) {
        this.showEmpty = false
      }
      if (query.showOnlyFollowing != null) {
        this.showOnlyFollowing = query.showOnlyFollowing
      }
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/alignmentUtils.scss';
@import '@/styles/colours.scss';
@import "@/styles/main.scss";

#persistent-navigator-div {
  div.v-toolbar__content {
    padding: 0px;
  }
  .drawer-title-layout {
    min-height: 100px;
    font-size: 40px;
    -webkit-user-select: none; 
    -moz-user-select: none; 
    -ms-user-select: none;
    user-select: none;
    // color: $primary !important;
  }
  .nav-layout {
    .flex {
      display: flex;
      align-items: center;
    }
  }
  .navbar-profile-flex {
    display: flex;
    align-items: center;
  }
  .navbar-profile-clickable-flex {
    border-radius: 100%;
    cursor: pointer;
  }
  .navbar-profile-circle {
    // width: 100%;
    // height: auto;
    // position: relative;
    // -webkit-border-radius: 50%;
    // -moz-border-radius: 50%;
    // -ms-border-radius: 50%;
    // -o-border-radius: 50%;
    border-radius: 50%;
    // display: flex;
    // align-items: center;
    // justify-content: center;
  }
  .title-flex {
    display: flex;
    justify-content: center;
    min-width: max-content;
  }
  .title-btn {
    height: 100% !important;
    border-radius: 3px !important;
    .a {
      border-radius: 3px !important;
    }
    width: 100%;
    padding: 0px;
    font-weight: 400;
    font-size: 24px !important;
    letter-spacing: 0;
    // color: $primary;
    background: transparent;
  }
  .nav-search-text-field {
    background: white;
    height: 100%;
    .v-text-field__slot {
      display: flex;
      align-items: center;
    }
    div {
      height: 100%;
    }
  }
  .nav-button {
    background: transparent;
    height: 100% !important;
    border-radius: 0px !important;
    .a {
      border-radius: 0px !important;
    }
    // color: $primary;
  }
  .create-snippet-btn {
    // background-color: $primary;
    color: white;
  }
  #search-bar-flex {
    & > .layout, .v-card {
      height: 100%;
      border-radius: 8px;
    }
    .v-text-field__details, .v-messages.theme--light {
      visibility: hidden;
      min-height: 0px;
    }
    .v-input__control > .v-input__slot {
      margin-bottom: 0px;
    }
    .v-select__selections {
      max-width: 50px;
    }
    .search-select-flex .v-input__control {
      height: 0px;
    }
    .search-btn {
      border-top-left-radius: 0px;
      border-bottom-left-radius: 0px;
      border-top-right-radius: 8px;
      border-bottom-right-radius: 8px;
      .v-ripple__container {
        display: none !important;
      }
    }
  }
}
</style>
