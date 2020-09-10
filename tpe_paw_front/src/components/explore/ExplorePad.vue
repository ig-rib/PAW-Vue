<template>
  <v-card mx-1 my-5>
    <v-container>
      <v-layout>
        {{ $t('explore.order.title') }}
      </v-layout>
      <v-layout my-2>
        <v-flex>
          <v-select
            v-model="exploreParams.orderBy"
            :items="types"
            item-text="name"
            item-value="value"
            :label="$t('explore.order.orderBy')"
            rounded
            outlined
            dense></v-select>
        </v-flex>
        <v-flex>
          <v-select
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
      <v-layout>
        {{ $t('explore.flagged.title') }}
      </v-layout>
      <v-layout>
        <v-layout>
          <v-checkbox v-model="exploreParams.includeFlagged"
            :label="$t('explore.flagged.includeFlagged')"
            ></v-checkbox>
        </v-layout>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        {{ $t('explore.title.title') }}
      </v-layout>
      <v-layout>
        <v-layout>
          <v-text-field v-model="exploreParams.snippetTitle"
            :label="$t('explore.title.label')"
            rounded
            outlined
            dense></v-text-field>
        </v-layout>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        <v-flex>
          <v-layout>
            {{ $t('explore.language') }}
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
        <v-flex>
          <v-layout>
            {{ $t('explore.tag')}}
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
        {{ $t('explore.username') }}
      </v-layout>
      <v-layout>
        <v-layout>
          <v-text-field v-model="exploreParams.username"
            :label="$t('explore.username')"
            rounded
            outlined
            dense></v-text-field>
        </v-layout>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        {{ $t('explore.dateUploaded') }}
      </v-layout>
      <v-layout>
        <v-layout>
          <v-flex>
            <tf-date-picker
              v-model="exploreParams.fromDate"
              :label="$t('explore.from')"
            ></tf-date-picker>
          </v-flex>
          <v-flex>
            <tf-date-picker
              v-model="exploreParams.toDate"
              :label="$t('explore.to')"
            ></tf-date-picker>
          </v-flex>
        </v-layout>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        {{ $t('explore.userReputation.title') }}
      </v-layout>
      <v-layout>
        <v-layout>
          <v-flex>
            <v-text-field
              :label="$t('explore.min')"
              v-model="exploreParams.minRep"
              rounded
              outlined
              dense
            ></v-text-field>
          </v-flex>
          <v-flex>
            <v-text-field
              :label="$t('explore.max')"
              v-model="exploreParams.maxRep"
              rounded
              outlined
              dense
            ></v-text-field>
          </v-flex>
        </v-layout>
      </v-layout>
      <v-divider></v-divider>
      <v-layout>
        {{ $t('explore.snippetVotes.title') }}
      </v-layout>
      <v-layout>
        <v-layout>
          <v-flex>
            <v-text-field
              :label="$t('explore.min')"
              v-model="exploreParams.minVotes"
              rounded
              outlined
              dense
            ></v-text-field>
          </v-flex>
          <v-flex>
            <v-text-field
              :label="$t('explore.max')"
              v-model="exploreParams.maxVotes"
              rounded
              outlined
              dense
            ></v-text-field>
          </v-flex>
        </v-layout>
      </v-layout>
      <v-layout>
        <v-btn @click="exploreSearch">
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