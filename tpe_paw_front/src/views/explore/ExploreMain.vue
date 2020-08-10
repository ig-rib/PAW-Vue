<template>
  <v-container>
    <v-layout>
      <v-flex>
        <v-card>
          <v-layout>
            <v-layout>
              {{ $t('explore.order.title') }}
            </v-layout>
            <v-layout>
              <v-flex>
                <v-layout>
                  {{ $t('explore.order.orderBy') }}
                </v-layout>
                <v-layout>
                  <v-select v-model="exploreParams.orderBy"></v-select>
                </v-layout>
              </v-flex>
              <v-flex>
                <v-layout>
                  {{ $t('explore.order.sort')}}
                </v-layout>
                <v-layout v-model="exploreParams.sort"></v-layout>
              </v-flex>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.flagged.title') }}
            </v-layout>
            <v-layout>
              <v-checkbox v-model="exploreParams.includeFlagged"
                :label="$t('explore.flagged.includeFlagged')"
                ></v-checkbox>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.title.title') }}
            </v-layout>
            <v-layout>
              <v-text-field v-model="exploreParams.snippetTitle"
                :label="$t('explore.title.label')"></v-text-field>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-flex>
              <v-layout>
                {{ $t('explore.language') }}
              </v-layout>
              <v-layout>
                <v-select v-model="exploreParams.language"></v-select>
              </v-layout>
            </v-flex>
            <v-flex>
              <v-layout>
                {{ $t('explore.tag')}}
              </v-layout>
              <v-layout>
                <tag-select
                  v-model="exploreParams.tag"
                ></tag-select>
              </v-layout>
            </v-flex>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.username') }}
            </v-layout>
            <v-layout>
              <v-text-field v-model="exploreParams.username"
                :label="$t('explore.username')"></v-text-field>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.dateUploaded') }}
            </v-layout>
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
            <v-layout>
              {{ $t('explore.userReputation.title') }}
            </v-layout>
            <v-layout>
              <v-flex>
                <v-text-field
                  :label="$t('explore.min')"
                  v-model="exploreParams.minRep"
                ></v-text-field>
              </v-flex>
              <v-flex>
                <v-text-field
                  :label="$t('explore.max')"
                  v-model="exploreParams.maxRep"
                ></v-text-field>
              </v-flex>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.snippetVotes.title') }}
            </v-layout>
            <v-layout>
              <v-flex>
                <v-text-field
                  :label="$t('explore.min')"
                  v-model="exploreParams.minVotes"
                ></v-text-field>
              </v-flex>
              <v-flex>
                <v-text-field
                  :label="$t('explore.max')"
                  v-model="exploreParams.maxVotes"
                ></v-text-field>
              </v-flex>
            </v-layout>
          </v-layout>
          <v-layout>
            <v-btn @click="exploreSearch">
              {{ $t('explore.explore') }}
            </v-btn>
          </v-layout>
        </v-card>
      </v-flex>
      <v-flex>
        SNIPPETS HERE
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import search from '@/services/search.js'
import languages from '@/services/languages.js'

export default {
  data () {
    return {
      exploreParams: {
        orderBy: null,
        sort: null,
        includeFlagged: null,
        snippetTitle: null,
        language: null,
        tag: null,
        username: null,
        fromDate: null,
        toDate: null,
        minRep: null,
        maxRep: null,
        minVotes: null,
        maxVotes: null
      },
      languages: () => {},
      loadingSearchOptions: false,
      loadingSnippets: false
    }
  },
  methods: {
    exploreSearch () {
      search.explore({
          t: this.exploreParams.orderBy,
          s: this.exploreParams.sort,
          minDate: this.exploreParams.fromDate,
          maxDate: this.exploreParams.toDate,
          minRep: this.exploreParams.minRep,
          maxRep: this.exploreParams.maxRep,
          minVotes: this.exploreParams.minVotes,
          maxVotes: this.exploreParams.maxVotes,
          langId: this.exploreParams.language,
          tagId: this.exploreParams.tag,
          uname: this.exploreParams.userame,
          title: this.exploreParams.snippetTitle,
          field: this.exploreParams.orderBy,
          includeFlagged: this.exploreParams.includeFlagged
        }
      )
    }
  }
}
</script>
