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
          <v-toolbar-title>Snippit</v-toolbar-title>
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
        <v-flex grow px-2>
          <v-layout>
            <v-flex>
              <v-card height="70%" :width="`${$vuetify.breakpoint.lgAndUp ? '60%' : '100%'}`">
                <v-layout fill-height>
                  <v-flex>
                    <v-text-field
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
            <v-flex>
              <v-select 
              :items="searchTypes"
              item-text="name"
              item-value="value"
              v-model="searchType"></v-select>
            </v-flex>
            <v-flex>
              <v-select
              :items="searchOrders"
              item-text="name"
              item-value="value"
              v-model="searchOrder"></v-select>
            </v-flex>
          </v-layout>
        </v-flex>

        <!-- Registration/Login/User section -->
        <v-flex class="flex-move-end">
          <v-btn icon>
            <v-icon>mdi-heart</v-icon>
          </v-btn>

          <v-btn icon>
            <v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
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
        <v-btn @click="goToLogin">LOGIN</v-btn>
      </v-flex>
    </v-layout>
    <v-layout v-else>
      <v-flex>
        <v-btn @click="goToProfile">PROFILE</v-btn>
      </v-flex>
      <v-flex>
        <v-btn @click="logout">LOGOUT</v-btn>
      </v-flex>
    </v-layout>
    </v-navigation-drawer>
    <div>
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import search from '@/services/search.js'

export default {
  data () {
    return {
      navDrawer: false,
      searchQuery: '',
      searchType: '',
      searchOrder: '',
      paths: [
        {
          title: this.$t('feed.title'),
          icon: 'mdi-home',
          path: {
            name: 'feed'
          }
        },
        {
          title: this.$t('explore.title'),
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
    }
  },
  methods: {
    goto (path) {
      this.$router.push(path)
    },
    search () {
      search.searchInLocation(this.$router.currentRoute.path, {
        q: this.searchQuery,
        t: this.searchType,
        s: this.searchOrder
      })
    },
    goToLogin () {
      this.$router.push({
        name: 'login'
      })
    },
    goToProfile () {

    },
    logout () {
      this.$store.dispatch('logout')
      this.$router.go()
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
</style>
