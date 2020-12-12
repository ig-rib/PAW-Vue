<template>
  <v-card id="explore-pad-card" min-width="300px" max-width="100% !important" mx-1 mb-5>
    <v-container>
      <v-layout justify-center class="explore-pad-title">
        {{ $t('explore.padTitle') }}
      </v-layout>
      <v-layout>
        <div class="snippit-subtitle explore-pad-section-title">
          {{ $t('explore.order.title') }}
        </div>
      </v-layout>
      <!-- Order by and Sort -->
      <v-layout my-2>
        <v-flex 6 px-2>
          <v-select
            hide-details
            v-model="exploreParams.orderBy"
            :items="types"
            item-text="name"
            item-value="value"
            :label="$t('explore.order.orderBy')"
            rounded
            outlined
            dense></v-select>
        </v-flex>
        <v-flex 6 px-2>
          <v-select
            hide-details
            v-model="exploreParams.sort"
            :items="sortOrders"
            item-text="name"
            item-value="value"
            :label="$t('explore.order.sort')"
            rounded
            outlined
            dense></v-select>
        </v-flex>
      </v-layout>
      <v-divider></v-divider>
      <!-- Include Flagged -->
      <v-layout>
        <div class="snippit-subtitle explore-pad-section-title">
          {{ $t('explore.flagged.title') }}
        </div>
      </v-layout>
      <v-layout>
        <v-layout>
          <v-checkbox :ripple="false" hide-details class="explore-pad-include-flagged-checkbox" v-model="exploreParams.includeFlagged"
            :label="$t('explore.flagged.includeFlagged')"
            ></v-checkbox>
        </v-layout>
      </v-layout>
      <v-divider></v-divider>
      <!-- Title -->
      <v-layout>
        <div class="snippit-subtitle explore-pad-section-title">
          {{ $t('explore.title.title') }}
        </div>
      </v-layout>
      <v-layout>
        <v-layout mb-2>
          <v-text-field 
            hide-details
            v-model="exploreParams.snippetTitle"
            rounded
            outlined
            dense></v-text-field>
        </v-layout>
      </v-layout>
      <v-divider></v-divider>
      <!-- Language -->
      <v-layout wrap>
        <!-- Language -->
        <v-flex md6 sm6 xs6>
          <v-layout>
            <div class="snippit-subtitle explore-pad-section-title">
              {{ $t('explore.language') }}
            </div>
          </v-layout>
          <v-layout>
            <v-flex>
              <language-select
                :multiple="true"
                :closeOnSelect="false"
                v-model="exploreParams.languages"
              ></language-select>
            </v-flex>
          </v-layout>
        </v-flex>
        <!-- Tags -->
        <v-flex md6 sm6 xs6>
          <v-layout>
            <div class="snippit-subtitle explore-pad-section-title">
              {{ $t('explore.tag')}}
            </div>
          </v-layout>
          <v-layout>
            <v-flex>
              <tag-select
                :multiple="true"
                :closeOnSelect="false"
                v-model="exploreParams.tags"
              ></tag-select>
            </v-flex>
          </v-layout>
        </v-flex>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        <div class="snippit-subtitle explore-pad-section-title">
          {{ $t('explore.username') }}
        </div>
      </v-layout>
      <v-layout mb-2>
        <v-layout>
          <v-text-field
            hide-details
            v-model="exploreParams.username"
            rounded
            outlined
            dense></v-text-field>
        </v-layout>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        <div class="snippit-subtitle explore-pad-section-title">
          {{ $t('explore.dateUploaded') }}
        </div>
      </v-layout>
      <v-layout mb-2>
        <v-flex px-1 md6 sm6 xs6>
          <tf-date-picker
            v-model="exploreParams.fromDate"
            :label="$t('explore.from')"
          ></tf-date-picker>
        </v-flex>
        <v-flex px-1 md6 sm6 xs6>
          <tf-date-picker
            v-model="exploreParams.toDate"
            :label="$t('explore.to')"
          ></tf-date-picker>
        </v-flex>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        <div class="snippit-subtitle explore-pad-section-title">
          {{ $t('explore.userReputation.title') }}
        </div>
      </v-layout>
      <v-layout mb-2>
        <v-flex md6 sm6 xs6 px-2>
          <v-text-field
            hide-details
            :label="$t('explore.min')"
            v-model="exploreParams.minRep"
            rounded
            outlined
            dense
          ></v-text-field>
        </v-flex>
        <v-flex md6 sm6 xs6 px-2>
          <v-text-field
            hide-details
            :label="$t('explore.max')"
            v-model="exploreParams.maxRep"
            rounded
            outlined
            dense
          ></v-text-field>
        </v-flex>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        <div class="snippit-subtitle explore-pad-section-title">
          {{ $t('explore.snippetVotes.title') }}
        </div>
      </v-layout>
      <v-layout mb-2>
        <v-flex px-2>
          <v-text-field
            hide-details
            :label="$t('explore.min')"
            v-model="exploreParams.minVotes"
            rounded
            outlined
            dense
          ></v-text-field>
        </v-flex>
        <v-flex px-2>
          <v-text-field
            hide-details
            :label="$t('explore.max')"
            v-model="exploreParams.maxVotes"
            rounded
            outlined
            dense
          ></v-text-field>
        </v-flex>
      </v-layout>
      <v-layout justify-end mt-5 mb-2>
        <v-btn rounded outlined color="primary" class="explore-btn" @click="exploreSearch">
          {{ $t('explore.explore') }}
        </v-btn>
      </v-layout>
    </v-container>
  </v-card>
</template>

<script>
import search from '@/services/search.js'

export default {
  data () {
    return {
      snippets: [],
      exploreParams: {
        orderBy: null,
        sort: null,
        includeFlagged: null,
        snippetTitle: null,
        languages: null,
        tags: null,
        username: null,
        fromDate: null,
        toDate: null,
        minRep: null,
        maxRep: null,
        minVotes: null,
        maxVotes: null
      },
      loadingSearchOptions: false,
      loadingSnippets: false
    }
  },
  methods: {
    exploreSearch () {
      const queryParams = {
        t: this.exploreParams.orderBy,
        s: this.exploreParams.sort,
        minDate: this.exploreParams.fromDate,
        maxDate: this.exploreParams.toDate,
        minRep: this.exploreParams.minRep,
        maxRep: this.exploreParams.maxRep,
        minVotes: this.exploreParams.minVotes,
        maxVotes: this.exploreParams.maxVotes,
        uname: this.exploreParams.username,
        title: this.exploreParams.snippetTitle,
        field: this.exploreParams.orderBy,
        includeFlagged: this.exploreParams.includeFlagged
      }
      if (this.exploreParams.languages != null && this.exploreParams.languages.length !== 0) {
        queryParams.langId = this.exploreParams.languages.map(lang => lang.id)
      }
      if (this.exploreParams.tags != null && this.exploreParams.tags.length !== 0) {
        queryParams.tagId = this.exploreParams.tags.map(tag => tag.id)
      }
      // TODO handle results
      this.$router.replace({
        query: queryParams
      })
      search.explore(queryParams)
        .then(r => {
          this.$emit('searchResults', r)
        })
    }
  },
  computed: {
    sortOrders () {
      return Object.values(search.constants.order)
    },
    types () {
      return Object.values(search.constants.type)
    }
  }
}
</script>

<style lang="scss">
#explore-pad-card {
  border-radius: 10px;
  margin-left: auto;
  .explore-pad-title {
    font-size: 36px;
    font-weight: 300;
  }
  .explore-pad-section-title {
    font-size: 20px;
    font-weight: 300 !important;
    padding: 0 0 2% 0;
    margin: 1% 0 1% 0;
  }
  .explore-pad-include-flagged-checkbox {
    margin: 0 0 5px 0;
    .v-ripple__container {
      display: none;
    }
  }
  .explore-btn {
    .v-ripple__container {
      opacity: 0.1 !important;
    }
  }
  .explore-btn::after {
    box-shadow: 0px !important;
  }
}
</style>