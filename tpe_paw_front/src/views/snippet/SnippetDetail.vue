<template>
  <v-container class="snippet-detail-outer-container">
    <v-layout justify-center pa-5 v-if="snippet.flagged">
      <v-flex shrink>
        <v-card class="notice-card">
          <v-card-title>
            {{ $t('snippets.snippetDetail.flagged.title') }}
          </v-card-title>
          <v-card-text class="notice-card-text">
            <p>{{ $t('snippets.snippetDetail.flagged.text1') }}</p>
            <p>
              {{ $t('snippets.snippetDetail.flagged.text2') }}
            </p>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
    <v-layout justify-center>
      <v-card id="snippet-detail-card" v-if="!loading">
        <v-container px-12>
          <v-layout class="snippet-title-line">
            <v-flex>
              <div class="snippet-title">
                {{ snippet.title }}
              </div>
            </v-flex>
            <v-flex shrink ml-auto>
              <v-btn :ripple="false" color="light-blue" :to="{
                name: 'language-snippets',
                params: { id: language.id }
              }" text outlined v-cloak>
                {{ language.name }}
              </v-btn>
            </v-flex>
          </v-layout>
          <v-layout class="description-line">
            <v-flex>
              <div>
                {{snippet.description}}
              </div>
            </v-flex>
          </v-layout>
          <v-layout class="divider-line">
            <v-divider></v-divider>
          </v-layout>
          <!-- CODE LAYOUT -->
          <v-layout class="snippet-code-layout">
            <v-flex>
              <v-textarea
              readonly
              no-resize
              hide-details
              rounded
              filled
              class="snippet-detail-code-textarea"
              v-model="snippet.code" v-cloak>
              </v-textarea>
            </v-flex>
          </v-layout>
          <!-- TAGS LINE -->
          <v-layout class="tags-line" v-if="tags.length > 0" wrap>
            <v-flex
              v-for="tag in tags"
              :key="tag.name"
              shrink
              px-2>
              <v-btn :ripple="false" 
                depressed
                color="light-blue"
                class="white--text"
                :to="{
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
            <v-flex class="action-icons" sm8 md8 lg8 mx-2>
              <v-layout class="action-icons-layout">
                <v-flex v-if="isSnippetOwner">
                  <v-btn :ripple="false" v-if="!snippet.deleted" :disabled="deleting" @click="deleteSnippet" icon>
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                  <v-btn :ripple="false" v-else :disabled="deleting" @click="restoreSnippet" icon>
                    <v-icon>mdi-delete-restore</v-icon>
                  </v-btn>
                </v-flex>
                <!-- FAV -->
                <v-flex>
                  <v-btn :ripple="false" :class="`fav-btn ${snippet.favorite ? 'color-crimson' : '' }`" :disabled="faving" @click="fav" icon>
                    <v-icon>{{`mdi-heart${snippet.favorite ? '' : '-outline'}`}}</v-icon>
                  </v-btn>
                </v-flex>
                <!-- VOTES -->
                <v-flex>
                  <v-layout>
                    <!-- UPVOTE -->
                    <v-flex>
                      <v-btn :ripple="false" :color="`${ isUpvoted ? 'green' : '' }`" class="thumb-up-btn" :disabled="voting" @click="vote(true)" icon>
                        <v-icon>{{`mdi-thumb-up${isUpvoted ? '' : '-outline'}`}}</v-icon>
                      </v-btn>
                    </v-flex>
                    <!-- SCORE -->
                    <v-flex class="snippet-score-flex">
                      {{ snippet.score }}
                    </v-flex>
                    <!-- DOWNVOTE -->
                    <v-flex>
                      <v-btn :ripple="false" :color="`${ isDownvoted ? 'red' : '' }`" class="thumb-down-btn" :disabled="voting" @click="vote(false)" icon>
                        <v-icon>{{`mdi-thumb-down${isDownvoted ? '' : '-outline'}`}}</v-icon>
                      </v-btn>
                    </v-flex>
                  </v-layout>
                </v-flex>
                <v-flex v-if="isAdmin || allowedToReport">
                  <!-- FLAG -->
                  <v-btn :ripple="false" :color="`${ snippet.flagged ? 'red' : '' }`" class="flag-btn" v-if="isAdmin" :disabled="flagging" @click="flag" icon>
                    <v-icon>{{`mdi-flag${snippet.flagged ? '' : '-outline'}`}}</v-icon>
                  </v-btn>
                  <!-- REPORT -->
                  <v-btn :ripple="false" :class="`fav-btn ${snippet.reported ? 'color-sandybrown' : '' }`" class="report-btn" v-else :disabled="reporting" @click="report" icon>
                    <v-icon>{{`mdi-alert-octagon${snippet.reported ? '' : '-outline'}`}}</v-icon>
                  </v-btn>
                </v-flex>
              </v-layout>
            </v-flex>
            <!-- OWNER DATA -->
            <v-flex shrink>
              <v-layout column>
                <v-flex>
                  {{snippet.dateCreated}}
                </v-flex>
                <v-flex>
                  <v-card elevation="0" class="owner-data" @click="goToOwnerProfile">
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
                  </v-card>
                </v-flex>
              </v-layout>
          </v-flex>
          </v-layout>
        </v-container>
        <v-dialog v-model="reportDialog">
          <v-card>
            <v-card-title>{{ $t('snippets.snippetDetail.report.whatIsWrong') }}</v-card-title>
            <v-textarea
            :rules="[rules.maxLength]"
            v-model="reportMessage"></v-textarea>
            <v-card-actions>
              <v-btn :ripple="false" :disabled="rules.maxLength() !== true" @click="sendReport">{{ $t('snippets.snippetDetail.report.confirm') }}</v-btn>
              <v-btn :ripple="false" @click="cancelReport">{{ $t('snippets.snippetDetail.report.cancel')}}</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-card>
    </v-layout>
  </v-container>
</template>

<script>
import snippets from '@/services/snippets.js'
import user from '@/services/user.js'
import axiosFetcher from '@/services/axiosFetcher.js'
import urls from '@/services/urls.js'
import validations from '@/functions/validations'

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
    },
    goToOwnerProfile () {
      this.$router.push({
        name: 'user-profile',
        params: {
          id: this.owner.id
        }
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
    },
    allowedToReport () {
      return this.$store.getters.user.reputation >= 10
    },
    rules () {
      return {
        maxLength: () => validations.maxLength(this.reportMessage, 300)
      }
    }
  },
  mounted () {
    // TODO add catch statements
    this.loading = true
    snippets.getSnippet(this.$route.params.id)
      .then(snippetResponse => {
        this.snippet = snippetResponse.data
        const ownerRequest = axiosFetcher.get(snippetResponse.data.owner)
        const languageRequest = axiosFetcher.get(snippetResponse.data.language)
        const allRequests = []
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
    if (this.$store.getters.loggedIn) {
      user.getLoggedInUser().then(r => {
        this.$store.dispatch('setUser', r.data)
      })
    }
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
@import '~vuetify/src/styles/settings/_variables';
@import '@/styles/noticeCard.scss';

  #snippet-detail-card {
    border-radius: 10px;
    .owner-image {
      border-radius: 40px;
    }
    .owner-data:hover {
      cursor: pointer;
    }
    min-width: min-content;
    .snippet-title-line {
      > .flex {
        display: flex;
        align-items: center;
      }
      .snippet-title {
        font-size: 38px;
      }
    }
    .description-line {
      max-height: 10%;
    }
    .divider-line {
      flex-grow: 0;
      margin: 0px 0px 2.5% 0px;
    }
    .snippet-code-layout {
      min-height: 50%;
      .snippet-detail-code-textarea {
        border-radius: 10px;
        height: 100%;
        div {
          height: 100%;
        }
      }
    }
    .tags-line {
      align-items: center;
      max-height: 10%;
    }
  }

  @media #{map-get($display-breakpoints, 'sm-and-up')} {
    #snippet-detail-card > .container {
    height: 750px !important;
    max-width: 650px;
    display: flex;
    flex-direction: column;
  }

  }
  #snippet-detail-action-bar {
    margin-top: auto !important;
    clear: both;
    justify-content: space-between;

    .action-icons-layout {
      background: #fefefe;
      border: 1px solid lightgrey;
      border-radius: 40px;
      justify-content: space-around;
      width: max-content;
      padding: 0px 50px 0px 50px;
      box-shadow: 1px 1px 3px rgba(50, 50, 50, 0.4) inset;
      .fav-btn:hover, .color-crimson {
        color: crimson;
      }
      .thumb-up-btn:hover {
        color: green;
      }
      .thumb-down-btn:hover {
        color: red;
      }
      .report-btn:hover, .color-sandybrown {
        color: sandybrown;
      }
      .flag-btn {
        color: red;
      }
    }
    .action-icons-layout > .flex {
      flex-grow:0;
      // width: max-content;
      i, .v-btn, i:active {
        width: 75px;
        height: 75px;
        font-size: 50px;
      }
      i:hover {
        font-size: 65px;
      }
    }
    .v-btn::before {
      background-color: transparent;
    }
    .v-btn::before:hover {
      font-size: 60px;
    }
  }
  #snippet-detail-action-bar > .flex {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .snippet-score-flex {
    display: flex;
    align-items: center;
  }
</style>
