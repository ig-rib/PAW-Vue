<template>
  <div id="explore-container">
    <v-layout class="view-title-layout">
      <v-flex shrink class="view-title">
        {{ $t('explore.explore') }}
      </v-flex>
    </v-layout>
    <v-layout justify-center wrap>
      <v-flex 
        :class="`explore-pad-flex ${$vuetify.breakpoint.mdAndUp ? 'my-15' : ''}`"
        md3 lg3 sm12>
        <explore-pad v-if="$vuetify.breakpoint.mdAndUp"
          @searchResults="updateResults"></explore-pad>
        <v-btn v-else @click="showExplorePad = !showExplorePad">
          {{ $t('explore.showFilters') }}
        </v-btn>
      </v-flex>
      <v-flex lg9 md9>
        <snippet-grid ref="exploreSnippets"></snippet-grid>
      </v-flex>
    </v-layout >
    <v-dialog width="450px" id="explore-pad-dialog" v-model="showExplorePad">
      <v-layout>
        <v-flex>
          <explore-pad
            @searchResults="updateResults"></explore-pad>
        </v-flex>
      </v-layout>
    </v-dialog>
  </div>
</template>

<script>
import search from '@/services/search.js'
import ExplorePad from '@/components/explore/ExplorePad.vue'

export default {
  title: 'Snippit - Explore',
  components: {
    'explore-pad': ExplorePad
  },
  data () {
    return {
      showExplorePad: false,
      loadingSnippets: false
    }
  },
  methods: {
    updateResults (r) {
      this.$refs.exploreSnippets.$emit('searchResults', r)
      this.showExplorePad = false
    }
  },
  mounted () {
    this.$on('searchResults', r => {
      console.log('explore received response')
      this.$refs.exploreSnippets.$emit('searchResults', r)
    })
  }
}
</script>

<style lang="scss">
  #explore-container {
    .explore-pad-flex {
      margin-left: auto;
    }
  }
</style>