<template>
  <div>
    <v-app-bar
      color="lightgrey"
      elevate-on-scroll
      scroll-target="#scrolling-techniques-7"
      height="68px"
    >
      <v-layout fill-height class="nav-layout">
        <v-flex shrink>
          <v-app-bar-nav-icon @click="navDrawer = !navDrawer"></v-app-bar-nav-icon>

        </v-flex>
        <v-flex md1 sm1>
          <v-toolbar-title>Title</v-toolbar-title>
        </v-flex>
        <v-flex grow class="justify-center">
          <!-- <v-spacer></v-spacer> -->
          <v-card height="80%" :width="`${$vuetify.breakpoint.lgAndUp ? '60%' : '100%'}`">
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
                  icon>
                  <v-icon>mdi-magnify</v-icon>
                </v-btn>
              </v-flex>
            </v-layout>
          </v-card>
        </v-flex>
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
    <v-navigation-drawer
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
    </v-navigation-drawer>
    <div>
      <h1>persistent navigator</h1>
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      navDrawer: false,
      searchQuery: '',
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
            name: 'languages'
          }
        },
        {
          title: this.$t('tags.title'),
          icon: 'mdi-tag',
          path: {
            name: 'tags'
          }
        }
      ]
    }
  },
  methods: {
    goto (path) {
      this.$router.push(path)
    },
    search () {
      //
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/alignmentUtils.scss';

  .nav-layout {
    .flex {
      display: flex;
      align-items: center;
      margin: 0px 4px 0px 4px;
    }
  }
  .nav-search-text-field {
    background: white;
    height: 100%;
    .v-text-field__slot {
      display: flex;
      align-items: center;
    }
  }
  .nav-search-text-field div {
    height: 100%;
  }
</style>
