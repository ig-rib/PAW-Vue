<template>
  <v-container id="explore-container">
    <v-layout>
      <v-flex class="view-title">
        {{ $t('explore.explore') }}
      </v-flex>
    </v-layout>
    <v-layout justify-center wrap>
      <v-flex :class="`${$vuetify.breakpoint.mdAndUp ? 'my-15' : ''}`" md4 lg4 sm12>
        <explore-pad v-if="$vuetify.breakpoint.mdAndUp"
          @searchResults="updateResults"></explore-pad>
        <v-btn rounded v-else @click="showExplorePad = !showExplorePad">
          {{ $t('explore.showFilters') }}
        </v-btn>
      </v-flex>
      <v-flex lg8 md8>
        <snippet-grid ref="exploreSnippets"></snippet-grid>
      </v-flex>
    </v-layout>
    <v-dialog width="450px" id="explore-pad-dialog" v-model="showExplorePad">
      <v-layout>
        <v-flex>
          <explore-pad
            @searchResults="updateResults"></explore-pad>
        </v-flex>
      </v-layout>
    </v-dialog>
  </v-container>
</template>

<script>
import search from '@/services/search.js'
import ExplorePad from '@/components/explore/ExplorePad.vue'

export default {
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
  }
</style>