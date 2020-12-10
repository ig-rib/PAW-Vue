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
          <v-layout shrink mt-5 mb-3 class="snippet-title-line">
            <v-flex sm10 md10 lg10 xl10>
              <div class="snippet-title">
                {{ snippet.title }}
              </div>
            </v-flex>
            <v-flex class="justify-end" sm2 md2 lg2 xl2 shrink ml-auto>
              <v-btn :ripple="false" color="light-blue" :to="{
                name: 'language-snippets',
                params: { id: language.id }
              }" text outlined v-cloak>
                {{ language.name }}
              </v-btn>
            </v-flex>
          </v-layout>
          <v-layout mb-3 shrink class="description-line">
            <v-flex>
              <div>
                {{snippet.description}}
              </div>
            </v-flex>
          </v-layout>
          <v-layout shrink mb-3 class="divider-line">
            <v-divider></v-divider>
          </v-layout>
          <!-- CODE LAYOUT -->
          <v-layout mb-8 class="snippet-code-layout">
            <v-flex>
              <!-- <v-textarea
              readonly
              no-resize
              hide-details
              rounded
              filled
              class="snippet-detail-code-textarea"
              id="code-textarea"
              v-model="snippet.code" v-cloak>
              </v-textarea> -->
              <ssh-pre class="snippet-detail-code-textarea" language="js" copy-button @copied="copiedToClipboard">
                {{snippet.code}}
                <template v-slot:copy-button>
                  <v-btn icon><v-icon>mdi-content-copy</v-icon></v-btn>
                </template>
              </ssh-pre>
              <input class="hidden-input" type="hidden" id="code-input" :value="snippet.code">
            </v-flex>
              <!-- <v-btn icon @click="copyToClipboard"><v-icon>mdi-clipboard</v-icon></v-btn> -->
          </v-layout>
          <!-- TAGS LINE -->
          <v-layout shrink mb-3 class="tags-line" v-if="tags.length > 0" wrap>
            <v-flex
              v-for="tag in tags"
              :key="tag.name"
              shrink
              pa-2>
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
          <v-layout shrink mb-5 id="snippet-detail-action-bar">
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
                      <v-btn :ripple="false" :color="`${ snippet.vote != null && snippet.vote.positive ? 'green' : '' }`" class="thumb-up-btn" :disabled="voting" @click="vote(true)" icon>
                        <v-icon>{{`mdi-thumb-up${snippet.vote != null && snippet.vote.positive ? '' : '-outline'}`}}</v-icon>
                      </v-btn>
                    </v-flex>
                    <!-- SCORE -->
                    <v-flex class="snippet-score-flex">
                      {{ snippet.score }}
                    </v-flex>
                    <!-- DOWNVOTE -->
                    <v-flex>
                      <v-btn :ripple="false" :color="`${ snippet.vote != null && !snippet.vote.positive ? 'red' : '' }`" class="thumb-down-btn" :disabled="voting" @click="vote(false)" icon>
                        <v-icon>{{`mdi-thumb-down${snippet.vote != null && !snippet.vote.positive ? '' : '-outline'}`}}</v-icon>
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
                  {{ readableDate }}
                </v-flex>
                <v-flex>
                  <v-card elevation="0" class="owner-data" @click="goToOwnerProfile">
                    <v-layout px-2>
                      <v-flex class="owner-image-flex" mr-2 shrink>
                        <v-img class="owner-image" width="40px" height="40px" :src="owner.icon" v-if="user != null">
                        </v-img>
                      </v-flex>
                      <v-flex class="no-select owner-name-score-flex">
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
        <v-dialog content-class="report-dialog" v-model="reportDialog">
          <v-card class="dialog-card">
            <v-card-title>{{ $t('snippets.snippetDetail.report.whatIsWrong') }}</v-card-title>
            <v-layout px-5>
              <v-textarea
              rounded
              outlined
              no-resize
              :rules="[rules.maxLength]"
              v-model="reportMessage"></v-textarea>
            </v-layout>
            <v-layout pa-5 justify-end>
              <v-flex shrink mr-2>
                <v-btn rounded outlined :disabled="rules.maxLength() !== true" @click="sendReport">{{ $t('snippets.snippetDetail.report.confirm') }}</v-btn>
              </v-flex>
              <v-flex shrink>
                <v-btn rounded outlined @click="cancelReport">{{ $t('snippets.snippetDetail.report.cancel')}}</v-btn>
              </v-flex>
            </v-layout>
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
import SshPre from 'simple-syntax-highlighter'
import 'simple-syntax-highlighter/dist/sshpre.css'


export default {
  title: 'Snippit - Snippet Detail',
  components: {
    'ssh-pre': SshPre
  },
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
        if (this.owner.email === this.$store.getters.user.email) {
          let usr = this.$store.getters.user
          usr.reputation = r.data.ownerReputation
          this.$store.dispatch('setUser', usr)
        }
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
          .catch(e => {
            if (e.response.status === 403) {
              this.$store.dispatch('snackError', e.response.data.message)
            }
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
      .catch(e => {
        if (e.response.status === 403) {
          this.$store.dispatch('snackError', e.response.data.message)
        }
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
          this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.snippetDeleted'))
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
          this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.snippetRestored'))
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
    },
    copiedToClipboard () {
      // TODO eventually remove commented code
      // const codeToCopy = document.querySelector('#code-textarea')
      // codeToCopy.select()
      // try {
      //   document.execCommand('copy')
      //   this.clearSelection()
      this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.copiedToClipboard'))
      // } catch (exc) {
      //   this.$store.dispatch('snackError', this.$t('error.snippet.detail.copying'))
      // }
    },
    clearSelection() {
      let sel;
      if ((sel = document.selection) && sel.empty) {
        sel.empty();
      } else {
          if (window.getSelection) {
            window.getSelection().removeAllRanges();
          }
          let activeEl = document.activeElement;
          if (activeEl) {
              let tagName = activeEl.nodeName.toLowerCase();
              if (tagName === 'textarea' ||
                      (tagName === 'input' && activeEl.type === 'text')) {
                  // Collapse the selection to the end
                activeEl.selectionStart = activeEl.selectionEnd;
              }
          }
      }
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
    },
    readableDate () {
      const theDate = new Date(this.snippet.dateCreated)
      return this.$t('snippets.snippetDetail.postingDateTime', { year: theDate.getFullYear(), month: theDate.getMonth(), day: theDate.getDay(), time: `${theDate.getHours()}:${theDate.getMinutes()}` })
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
  
  .report-dialog {
    .dialog-card {
      border-radius: 12px !important;
    }
  }

  #snippet-detail-card {
    border-radius: 10px;
    .owner-image {
      border-radius: 40px;
    }
    .owner-data {
      border: 1px solid lightgrey;
      border-radius: 10px;
      .owner-image-flex {
        align-items: center;
        justify-content: center;
        display:flex
      }
      .owner-name-score-flex {
        align-items: center;
        justify-content: center;
        font-weight: 500;
        .flex {
          max-width: 12ch !important;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
      &:before {
        border-radius: 10px !important;
      }
      .v-ripple__container {
        opacity: 0.2 !important;
        border-radius: 10px !important;
      }
    }
    .owner-data:hover {
      cursor: pointer;
    }
    min-width: min-content;
    .snippet-title-line {
      overflow: hidden;
      > .flex {
        display: flex;
        align-items: center;
      }
      .snippet-title {
        font-size: 38px;
        line-height: 1;
      }
    }
    .description-line {
      text-align: justify;
    }
    .divider-line {
      flex-grow: 0;
      margin: 0px 0px 2.5% 0px;
    }
    .snippet-code-layout {
      min-height: 200px;
      .snippet-detail-code-textarea {
        border-radius: 10px;
        height: 100%;
        max-height: 500px;
        overflow: auto;
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
    min-height: 650px !important;
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
        font-size: 60px;
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
