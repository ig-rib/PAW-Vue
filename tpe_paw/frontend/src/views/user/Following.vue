<template>
  <div class="fill-width">
    <v-layout class="view-title-layout">
      <v-flex shrink class="view-title">
        {{ $t('user.following.title') }}
      </v-flex>
    </v-layout>
    <v-layout justify-center>
      <v-btn
        rounded
        outlined
        color="primary"
        @click="goToFollowedTags">
        {{ $t('user.following.manageFollowedTags') }}
      </v-btn>
    </v-layout>
    <v-layout>
      <snippet-grid ref="userFavoritesSnippetGrid"></snippet-grid>
    </v-layout>
  </div>
</template>

<script>
export default {
  title: 'Snippit - Following',
  mounted () {
    this.$on('searchResults', r => this.$refs.userFavoritesSnippetGrid.$emit('searchResults', r))
  },
  methods: {
    goToFollowedTags () {
      this.$router.push({
        name: 'tags-main',
        query: {
          showOnlyFollowing: true
        }
      })
    }
  }
}
</script>
