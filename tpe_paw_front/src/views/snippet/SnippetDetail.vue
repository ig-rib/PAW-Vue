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
          FAVORITE
        </v-flex>
        <v-flex>
          <v-layout>
            <v-flex>
              UPVOTE
            </v-flex>
            <v-flex>
              SCORE
            </v-flex>
            <v-flex>
              DOWNVOTE
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
      loading: true
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
