<template>
  <v-container>
    <v-card id="snippet-detail-card" v-if="!loading">
      <v-container>
        <v-layout>
          <v-flex>
            <div>
              {{ snippet.title }}
            </div>
          </v-flex>
          <v-flex shrink ml-auto>
            <v-btn :to="{
              name: 'language-snippets',
              params: { id: language.id }
            }" text outlined v-cloak>
              {{ language.name }}
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
        <!-- ACTION BAR - ACTION ICONS AND OWNER DATA -->
        <v-layout id="snippet-detail-action-bar">
          <!-- ACTION ICONS -->
          <v-flex class="action-icons" sm8 md8 lg8>
            <v-layout class="action-icons-layout">
              <v-flex v-if="isSnippetOwner">
                <v-btn v-if="!snippet.deleted" :disabled="deleting" @click="deleteSnippet" icon>
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
                <v-btn v-else :disabled="deleting" @click="restoreSnippet" icon>
                  <v-icon>mdi-delete-restore</v-icon>
                </v-btn>
              </v-flex>
              <v-flex>
                <v-btn :disabled="faving" @click="fav" icon>
                  <v-icon>{{`mdi-heart${snippet.favorite ? '' : '-outline'}`}}</v-icon>
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
                <v-btn v-if="isAdmin" :disabled="flagging" @click="flag" icon>
                  <v-icon>{{`mdi-flag${snippet.flagged ? '' : '-outline'}`}}</v-icon>
                </v-btn>
                <v-btn v-else :disabled="reporting" @click="report" icon>
                  <v-icon>{{`mdi-alert-octagon${snippet.reported ? '' : '-outline'}`}}</v-icon>
                </v-btn>
              </v-flex>
            </v-layout>
          </v-flex>
          <!-- OWNER DATA -->
          <v-flex sm4 md4 lg4>
            <v-layout column>
              <v-flex>
                {{snippet.dateCreated}}
              </v-flex>
              <v-flex>
                <v-layout>
                  <v-flex shrink>
                    <v-img class="owner-image" width="40px" height="40px" :src="owner.icon" v-if="user != null">
                    </v-img>
                  </v-flex>
                  <v-flex>
                    <v-layout column>
                      <v-flex>
                        {{ owner.username }}
                      </v-flex>
                      <v-flex>
                        {{ owner.reputation }}
                      </v-flex>
                    </v-layout>
                  </v-flex>
                </v-layout>
              </v-flex>
            </v-layout>
        </v-flex>
        </v-layout>
      </v-container>
      <v-dialog v-model="reportDialog">
        <v-card>
          <v-card-title>{{ $t('snippets.snippetDetail.report.whatIsWrong') }}</v-card-title>
          <v-textarea v-model="reportMessage"></v-textarea>
          <v-card-actions>
            <v-btn @click="sendReport">{{ $t('snippets.snippetDetail.report.confirm') }}</v-btn>
            <v-btn @click="cancelReport">{{ $t('snippets.snippetDetail.report.cancel')}}</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-card>
  </v-container>
</template>

<script>
import snippets from '@/services/snippets.js'
import axiosFetcher from '@/services/axiosFetcher.js'
import urls from '@/services/urls.js'

export default {
  data () {
    return {
      snippet: {},
      user: {},
      language: {},
      tags: [],
      loading: true,
      faving: false,
      voting: false,
      flagging: false,
      reporting: false,
      deleting: false,
      reportDialog: false,
      reportMessage: ''
    }
  },
  methods: {
    fav () {
      this.faving = true
      let promise = {}
      if (this.snippet.favorite) {
        promise = snippets.unfavSnippet(this.snippet.id)
      } else {
        promise = snippets.favSnippet(this.snippet.id)
      }
      promise.then(r => { this.snippet.favorite = !this.snippet.favorite })
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
        this.owner.reputation = r.data.ownerReputation
      })
      .finally(() => { this.voting = false })
    },
    flag () {
      this.flagging = true
      let promise = {}
      if (this.snippet.flagged) {
        promise = snippets.unflagSnippet(this.snippet.id)
      } else {
        promise = snippets.flagSnippet(this.snippet.id)
      }
      promise.then(r => { this.snippet.flagged = !this.snippet.flagged })
      .finally(() => { this.flagging = false })
    },
    report () {
      this.reporting = true
      let promise = {}
      if (this.snippet.reported) {
        snippets.unreportSnippet(this.snippet.id)
          .then(r => {
            this.snippet.reported = false
          })
          .finally(() => { this.reporting = false })
      } else {
        this.reportDialog = true
      }
    },
    sendReport () {
      snippets.reportSnippet(this.snippet.id, {
        detail: this.reportMessage,
        // TODO how to find baseURI
        baseUri: urls.localDomain + this.$route.path
      }).then(r => {
        this.snippet.reported = true
        this.resetReportData()
      })
      // TODO add catch clause
    },
    cancelReport () {
      this.resetReportData()
    },
    resetReportData () {
      this.reportMessage = ''
      this.reportDialog = false
      this.reporting = false
    },
    deleteSnippet () {
      this.deleting = true
      snippets.deleteSnippet(this.snippet.id)
        .then(r => {
          this.snippet.deleted = true
        })
        .finally(() => {
          this.deleting = false
        })
    },
    restoreSnippet () {
      this.deleting = true
      snippets.restoreDeletedSnippet(this.snippet.id)
        .then(r => {
          this.snippet.deleted = false
        })
        .finally(() => {
          this.deleting = false
        })
    }
  },
  computed: {
    isUpvoted () {
      return this.snippet.vote != null && this.snippet.vote.positive
    },
    isDownvoted () {
      return this.snippet.vote != null && !this.snippet.vote.positive
    },
    isSnippetOwner () {
      return this.$store.getters.user.id === this.owner.id
    },
    isAdmin () {
      return this.$store.getters.user.admin
    }
  },
  mounted () {
    // TODO add catch statements
    this.loading = true
    snippets.getSnippet(this.$route.params.id)
      .then(snippetResponse => {
        this.snippet = snippetResponse.data
        let ownerRequest = axiosFetcher.get(snippetResponse.data.owner)
        let languageRequest = axiosFetcher.get(snippetResponse.data.language)
        let allRequests = []
        allRequests.push(...snippetResponse.data.tags.map(tagUri => axiosFetcher.get(tagUri)))
        allRequests.push(ownerRequest)
        allRequests.push(languageRequest)
        return Promise.all(allRequests)
      })
      .then(responses => {
        this.language = responses.pop().data
        this.owner = responses.pop().data
        this.tags = responses.map(r => r.data)
      })
      .finally(() => {
        this.loading = false
      })
  },
  watch: {
    reportDialog: {
      handler: function (newVal, oldVal) {
        if (newVal === false) {
          this.resetReportData()
        }
      }
    }
  }
}
</script>

<style lang="scss">
  #snippet-detail-card {
    .owner-image {
      border-radius: 40px;
    }
    min-width: max-content;
  }
  #snippet-detail-card > .container {
    height: 500px !important;
    display: flex;
    flex-direction: column;
  }
  #snippet-detail-action-bar {
    margin-top: auto !important;
    clear: both;
    .action-icons-layout {
      border: 1px solid black;
      border-radius: 40px;
      flex-grow: 0;
    }
    .action-icons-layout > .flex {
      flex-grow:0;
    }
  }
  #snippet-detail-action-bar > .flex {
      display: flex;
      align-items: center;
    }
</style>