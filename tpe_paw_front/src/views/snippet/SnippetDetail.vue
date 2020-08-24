<template>
  <v-container>
    <v-card v-if="!loading">
      <v-layout>
        <v-flex>
          <div>
            {{ snippet.title }}
          </div>
        </v-flex>
        <v-flex shrink ml-auto>
          <v-btn :to="{
            name: 'language-snippets',
            params: { id: snippet.language.id }
          }" text outlined v-cloak>
            {{ snippet.language.name }}
          </v-btn>
        </v-flex>
      </v-layout>
      <v-layout>
        <v-flex>
          <div>
            {{snippet.description}}
          </div>
        </v-flex>
      </v-layout>
      <v-layout>
        <v-divider></v-divider>
      </v-layout>
      <v-layout>
        <v-flex>
          <v-textarea readonly v-model="snippet.code" v-cloak>
          </v-textarea>
        </v-flex>
      </v-layout>
      <v-layout wrap>
        <v-flex
          v-for="tag in tags"
          :key="tag.name"
          shrink
          px-2>
          <v-btn :to="{
            name: 'tag-snippets',
            params: {
              id: tag.id
            }
          }">
            {{ tag.name }}
          </v-btn>
        </v-flex>
      </v-layout>
      <v-layout>
        <v-flex>
          DELETE
        </v-flex>
        <v-flex>
          <v-btn :disabled="faving" @click="fav" icon>
            <v-icon>{{`mdi-heart${snippet.isFavorite ? '' : '-outline'}`}}</v-icon>
          </v-btn>
        </v-flex>
        <v-flex>
          <v-layout>
            <v-flex>
              <v-btn :disabled="voting" @click="vote(true)" icon>
                <v-icon>{{`mdi-thumb-up${isUpvoted ? '' : '-outline'}`}}</v-icon>
              </v-btn>
            </v-flex>
            <v-flex>
              {{ snippet.score }}
            </v-flex>
            <v-flex>
              <v-btn :disabled="voting" @click="vote(false)" icon>
                <v-icon>{{`mdi-thumb-down${isDownvoted ? '' : '-outline'}`}}</v-icon>
              </v-btn>
            </v-flex>
          </v-layout>
        </v-flex>
        <v-flex>
          REPORT
        </v-flex>
        <v-flex>
          OWNER
        </v-flex>
      </v-layout>
    </v-card>
    {{snippet}}
  </v-container>
</template>

<script>
import snippets from '@/services/snippets.js'
import axiosFetcher from '@/services/axiosFetcher.js'


export default {
  data () {
    return {
      snippet: {},
      user: {},
      tags: [],
      loading: true,
      faving: false,
      voting: false,
    }
  },
  methods: {
    fav () {
      this.faving = true
      let promise = {}
      if (this.snippet.isFavorite) {
        promise = snippets.unfavSnippet(this.snippet.id)
      } else {
        promise = snippets.favSnippet(this.snippet.id)
      }
      promise.then(r => { this.snippet.isFavorite = !this.snippet.isFavorite })
      .finally(() => { this.faving = false })
    },
    vote (isPositive) {
      this.voting = true
      const params = {
        positive: isPositive,
        selected: this.snippet.vote == null || this.snippet.vote.positive !== isPositive
      }
      snippets.voteSnippet(this.snippet.id, params).then(r => {
        this.snippet.vote = r.data.vote
        this.snippet.score = r.data.snippetScore
      })
      .finally(() => { this.voting = false })
    }
  },
  computed: {
    isUpvoted () {
      return this.snippet.vote != null && this.snippet.vote.positive
    },
    isDownvoted () {
      return this.snippet.vote != null && !this.snippet.vote.positive
    }
  },
  mounted () {
    // TODO add catch statements
    this.loading = true
    snippets.getSnippet(this.$route.params.id)
      .then(snippetResponse => {
        this.snippet = snippetResponse.data
        let ownerRequest = axiosFetcher.get(snippetResponse.data.owner)
        let allRequests = []
        allRequests.push(...snippetResponse.data.tags.map(tagUri => axiosFetcher.get(tagUri)))
        allRequests.push(ownerRequest)
        return Promise.all(allRequests)
      })
      .then(responses => {
        this.user = responses.pop().data
        this.tags = responses.map(r => r.data)
      })
      .finally(() => {
        this.loading = false
      })
  }
}
</script>
