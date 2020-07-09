<template>
  <div>
    <v-app-bar
      absolute
      color="grey"
      elevate-on-scroll
      scroll-target="#scrolling-techniques-7"
    >
      <v-app-bar-nav-icon @click="navDrawer = !navDrawer"></v-app-bar-nav-icon>

      <v-toolbar-title>Title</v-toolbar-title>

      <v-spacer></v-spacer>

      <v-btn icon>
        <v-icon>mdi-magnify</v-icon>
      </v-btn>

      <v-btn icon>
        <v-icon>mdi-heart</v-icon>
      </v-btn>

      <v-btn icon>
        <v-icon>mdi-dots-vertical</v-icon>
      </v-btn>
    </v-app-bar>
    <v-navigation-drawer
      disable-resize-watcher
      absolute
      temporary
      v-model="navDrawer">
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
      navDrawer: true,
      paths: [
        {
          title: this.$t('explore.title'),
          icon: 'mdi-heart',
          path: {
            name: 'explore'
          }
        },
        {
          title: this.$t('languages.title'),
          icon: 'mdi-magnify',
          path: {
            name: 'languages'
          }
        }
      ]
    }
  },
  methods: {
    goto (path) {
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss">
  // .v-navigation-drawer--close.v-navigation-drawer--temporary {
  //   transform: translateX(-13vw) !important;
  // }
</style>
