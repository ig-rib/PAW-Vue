<template>
  <div>
    <v-app-bar
      pa-0
      color="lightgrey"
      elevate-on-scroll
      scroll-target="#scrolling-techniques-7"
      height="68px"
    >
      <v-layout fill-height class="nav-layout">
        <!-- Hamburger button for navdrawer -->
        <v-flex shrink v-if="$vuetify.breakpoint.mdAndDown">
          <v-app-bar-nav-icon @click="navDrawer = !navDrawer"></v-app-bar-nav-icon>
        </v-flex>

        <!-- Title -->
        <v-flex md1 sm1>
          <v-toolbar-title
            @click="goHome"
            >{{ $t('snippit') }}</v-toolbar-title>
        </v-flex>

        <!-- Navigation for large screens -->

        <v-flex v-if="$vuetify.breakpoint.lgAndUp" shrink>
          <v-layout fill-height class="pt-0">
            <v-flex
              v-for="item in paths"
              :key="item.title"
            >
              <v-btn
                elevation="0"
                :to="item.path"
                class="nav-button"
              >
                <v-layout>
                  <v-flex>
                      <v-icon>{{ item.icon }}</v-icon>
                  </v-flex>

                  <v-flex>
                    <div>{{ item.title }}</div>
                  </v-flex>
                </v-layout>
              </v-btn>
            </v-flex>
          </v-layout>
        </v-flex>

        <!-- Search bar -->
        <v-flex id="search-bar-flex" px-2>
          <v-layout>
            <v-flex lg6 grow>
              <v-card height="70%" width="100%">
                <v-layout fill-height>
                  <v-flex>
                    <v-text-field
                      @keyup.enter="search"
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
                  v-model="showEmpty"
                  :label="$t('search.showEmpty')"></v-checkbox>
              </v-flex>
              <v-flex px-2 shrink class="search-checkbox" v-if="resultType == 'tag' && $store.getters.loggedIn">
                <v-checkbox
                  v-model="showOnlyFollowing"
                  :label="$t('search.showOnlyFollowing')"
                  ></v-checkbox>
              </v-flex>
                <!-- </v-layout>
              </v-flex> -->
            </template>
          </v-layout>
        </v-flex>
        <v-flex shrink v-if="$store.getters.loggedIn">
          <v-btn :to="{name: 'create-snippet'}">
            {{ $t('snippets.createSnippet.createSnippet') }}
          </v-btn>
        </v-flex>
        <!-- Registration/Login/User section -->
        <v-flex shrink v-if="$vuetify.breakpoint.lgAndUp">
          <v-layout v-if="!$store.getters.loggedIn">
            <v-flex shrink>
              <v-btn @click="goToLogin">{{ $t('registration.login') }}</v-btn>
            </v-flex>
          </v-layout>
          <v-layout v-else>
            <v-flex shrink>
              <v-btn x-large icon @click="goToProfile">
                <v-icon>mdi-account-circle</v-icon>  
              </v-btn>
            </v-flex>
            <v-flex shrink>
              <v-btn x-large icon @click="logout">
                <v-icon>mdi-logout</v-icon>
              </v-btn>
            </v-flex>
          </v-layout>
        </v-flex>
      </v-layout>
    </v-app-bar>
    <v-navigation-drawer v-if="$vuetify.breakpoint.mdAndDown"
      disable-resize-watcher
      absolute
      temporary
      v-model="navDrawer">
      <h3>
        {{`Snippit`}}
      </h3>
      <v-list class="pt-0">
      <v-list-item
        v-for="item in paths"
        :key="item.title"
        :to="item.path"
      >
        <v-layout>
          <v-flex>
              <v-icon>{{ item.icon }}</v-icon>
          </v-flex>

          <v-flex>
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-flex>
        </v-layout>
      </v-list-item>
    </v-list>
    <v-layout v-if="!$store.getters.loggedIn">
      <v-flex>
        <v-btn @click="goToLogin">{{ $t('registration.login') }}</v-btn>
      </v-flex>
    </v-layout>
    <v-layout v-else>
      <v-flex shrink>
        <v-btn x-large icon @click="goToProfile">
          <v-icon>mdi-account-circle</v-icon>  
        </v-btn>
      </v-flex>
      <v-flex shrink>
        <v-btn x-large icon @click="logout">
          <v-icon>mdi-logout</v-icon>
        </v-btn>
      </v-flex>
    </v-layout>
    </v-navigation-drawer>
    <div>
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
      showEmpty: true,
      showOnlyFollowing: false,
      paths: [
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
          icon: 'mdi-coffee',
          path: {
            name: 'languages-main'
          }
        },
        {
          title: this.$t('tags.title'),
          icon: 'mdi-tag',
          path: {
            name: 'tags-main'
          }
        }
      ]
    }
  },
  computed: {
    searchTypes () {
      return Object.values(search.constants.type)
    },
    searchOrders () {
      return Object.values(search.constants.order)
    },
    // TODO make name clearer
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
      console.log('persistent-navigator params', params)
      this.$router.replace({
        query: params
      })
      switch (this.resultType) {
        case 'snippet':
          return search.searchInLocation(this.$router.currentRoute.path, params)
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
      this.$store.dispatch('logout')
      this.$router.go()
    }
  },
  // Decided to update contents as soon as checkboxes are
  // clicked, since user would need to re-enter the search
  // query (including the 'empty query' case) otherwise, making it
  // less friendly.
  watch: {
    showEmpty: {
      handler: function (newVal, oldVal) {
        let params = {}
        Object.assign(params, this.$route.query)
        params.showEmpty = newVal
        this.performSearch(params)
          .then(r => {
            // TODO handle data
            console.log(params)
            // this.$router.query.showEmpty = newVal
          })
          .catch(e => {
            // Restores old value,
            this.showEmpty = oldVal
            // TODO let user know why
          })
      }
    },
    showOnlyFollowing: {
      handler: function (newVal, oldVal) {
        let params = {}
        Object.assign(params, this.$route.query)
        params.showOnlyFollowing = newVal
        this.performSearch(params)
          .then(r => {
            // TODO handle data
            console.log(params)
            // this.$router.query.showOnlyFollowing = newVal
          })
          .catch(e => {
            // Restores old value,
            this.showEmpty = oldVal
            // TODO let user know why
          })
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
  div.v-toolbar__content {
    padding: 0px;
  }
  .nav-layout {
    .flex {
      display: flex;
      align-items: center;
    }
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
    height: 100% !important;
    border-radius: 0px !important;
    .a {
      border-radius: 0px !important;
    }
  }
  #search-bar-flex {
    & > .layout {
      height: 100%;
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
  }
</style>
