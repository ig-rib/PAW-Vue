<template>
  <v-container class="snippet-detail-outer-container">
    <v-layout justify-center align-center column pa-5 v-if="snippet.flagged || snippet.deleted || snippet.showReportedWarning">
      <v-flex ma-3 shrink v-if="snippet.flagged">
        <v-card class="notice-card">
          <v-layout>
            <v-flex ml-2 shrink class="notice-card-icon">
              <v-icon color="red">
                mdi-flag
              </v-icon>
            </v-flex>
            <v-flex>
              <v-card-title>
                {{ $t('snippets.snippetDetail.flagged.title') }}
              </v-card-title>
              <v-card-text class="notice-card-text">
                <p>{{ $t('snippets.snippetDetail.flagged.text1') }}</p>
                <p>
                  {{ $t('snippets.snippetDetail.flagged.text2') }}
                </p>
              </v-card-text>
            </v-flex>
          </v-layout>
        </v-card>
      </v-flex>
      <v-flex ma-3 shrink v-if="snippet.deleted">
        <v-card class="notice-card">
          <v-layout>
            <v-flex ml-2 shrink class="notice-card-icon">
              <v-icon color="red">
                mdi-delete-sweep
              </v-icon>
            </v-flex>
            <v-flex>
              <v-card-title>
                {{ $t('snippets.snippetDetail.deleted.title') }}
              </v-card-title>
              <v-card-text class="notice-card-text">
                <p>{{ $t('snippets.snippetDetail.deleted.text1') }}</p>
                <p>
                  {{ $t('snippets.snippetDetail.deleted.text2') }}
                </p>
              </v-card-text>
            </v-flex>
          </v-layout>
        </v-card>
      </v-flex>
      <v-flex ma-3 shrink v-if="snippet.showReportedWarning">
        <v-card class="notice-card">
          <v-layout column>
            <v-flex>
              <v-layout>
                <v-flex ml-2 shrink class="notice-card-icon">
                  <v-icon color="#f4a460">
                    mdi-alert-octagon
                  </v-icon>
                </v-flex>
                <v-flex>
                  <v-card-title>
                    {{ $t('snippets.snippetDetail.report.warning.title') }}
                  </v-card-title>
                  <v-card-text class="notice-card-text">
                    <p>{{ $t('snippets.snippetDetail.report.warning.message') }}</p>
                    <p>
                      {{ $t('snippets.snippetDetail.report.warning.suggestion') }}
                    </p>
                  </v-card-text>
                </v-flex>
              </v-layout>
            </v-flex>
            <v-flex>
              <v-layout ma-2 justify-center>
                <v-flex shrink>
                  <v-btn
                    rounded
                    outlined
                    color="#f4a460"
                    class="color-sandybrown"
                    @click="dismissAllReports">
                    {{ $t('snippets.snippetDetail.report.warning.dismissAllReports') }}
                  </v-btn>
                </v-flex>
              </v-layout>
            </v-flex>
          </v-layout>
        </v-card>
      </v-flex>
    </v-layout>
    <v-layout v-if="!editing" justify-center>
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
                {{ titleAbreviation(language.name, 12) }}
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
                  <v-btn 
                  :ripple="false" 
                  :class="`fav-btn ${snippet.favorite ? 'color-crimson' : '' }`" 
                  :disabled="faving || snippet.deleted" 
                  @click="fav" icon
                  >
                    <v-icon>{{`mdi-heart${snippet.favorite ? '' : '-outline'}`}}</v-icon>
                  </v-btn>
                </v-flex>
                <!-- VOTES -->
                <v-flex>
                  <v-layout>
                    <!-- UPVOTE -->
                    <v-flex>
                      <v-btn 
                      :ripple="false" 
                      :color="`${ snippet.vote != null && snippet.vote.positive ? 'green' : '' }`" 
                      class="thumb-up-btn" 
                      :disabled="voting || snippet.deleted" 
                      @click="vote(true)" 
                      icon>
                        <v-icon>{{`mdi-thumb-up${snippet.vote != null && snippet.vote.positive ? '' : '-outline'}`}}</v-icon>
                      </v-btn>
                    </v-flex>
                    <!-- SCORE -->
                    <v-flex class="snippet-score-flex">
                      {{ snippet.score }}
                    </v-flex>
                    <!-- DOWNVOTE -->
                    <v-flex>
                      <v-btn 
                      :ripple="false" 
                      :color="`${ snippet.vote != null && !snippet.vote.positive ? 'red' : '' }`" 
                      class="thumb-down-btn" 
                      :disabled="voting || snippet.deleted" 
                      @click="vote(false)" icon>
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
                  <v-btn :ripple="false" :class="`fav-btn ${ snippet.reported ? 'color-sandybrown' : '' }`" class="report-btn" v-else :disabled="reporting" @click="report" icon>
                    <v-icon>{{`mdi-alert-octagon${snippet.reported ? '' : '-outline'}`}}</v-icon>
                  </v-btn>
                </v-flex>
              </v-layout>
            </v-flex>
            <!-- OWNER DATA -->
            <v-flex shrink>
              <v-layout column>
                <v-flex ml-2>
                  {{ readableDate }}
                </v-flex>
                <v-flex>
                  <v-card elevation="0" class="owner-data" @click="goToOwnerProfile">
                    <v-layout px-2>
                      <v-flex class="owner-image-flex" mr-2 shrink>
                        <v-img
                          v-if="!error_image"
                          @error="error_image = true" 
                          class="owner-image" 
                          width="40px"
                          height="40px" 
                          :src="owner.icon">
                        </v-img>
                        <v-icon size="40" v-else>mdi-account-circle</v-icon>
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
              :rules="[rules.lengthBetween]"
              v-model="reportMessage"></v-textarea>
            </v-layout>
            <v-layout px-3 pb-5 justify-end>
              <v-flex shrink mr-2>
                <v-btn rounded outlined color="#2286c3" :disabled="rules.lengthBetween() !== true" @click="sendReport">{{ $t('snippets.snippetDetail.report.confirm') }}</v-btn>
              </v-flex>
              <v-flex shrink>
                <v-btn rounded outlined color="red" @click="cancelReport">{{ $t('snippets.snippetDetail.report.cancel')}}</v-btn>
              </v-flex>
            </v-layout>
          </v-card>
        </v-dialog>
         <v-dialog content-class="report-dialog" v-model="unreportDialog">
          <v-card class="dialog-card">
            <v-card-title class="justify-center">{{ $t('snippets.snippetDetail.report.unreport') }}</v-card-title>
            <v-layout px-3 pb-5 justify-center>
              <v-flex shrink mr-2>
                <v-btn rounded outlined color="#2286c3" @click="sendUnreport">{{ $t('snippets.snippetDetail.report.confirm') }}</v-btn>
              </v-flex>
              <v-flex shrink>
                <v-btn rounded outlined color="red" @click="cancelUnreport">{{ $t('snippets.snippetDetail.report.cancel')}}</v-btn>
              </v-flex>
            </v-layout>

          </v-card>
        </v-dialog>
      </v-card>
      <v-flex class="snippet-detail-progress-flex" v-else>
        <v-progress-circular
          :size="70"
          :width="7"
          color="primary"
          indeterminate></v-progress-circular>
      </v-flex>
    </v-layout>
    <v-layout justify-center v-else>
      <v-card px-3 min-width="500px">
        <v-container>
          <v-layout mt-2 mb-1> 
              <v-flex lg7 md7 sm7 xs7 class="snippit-subtitle">
                {{ $t('snippets.createSnippet.title') }}
              </v-flex>
            </v-layout>
            <v-layout class="title-row">
              <v-flex lg7 md7 sm7 xs7>
                <v-text-field
                  class="custom-label-color"
                  outlined
                  dense
                  rounded
                  :rules="[rules.title, rules.titleNotBlankWithSpaces]"
                  v-model="editedTitle"
                  ></v-text-field>
              </v-flex>
            </v-layout>
            <v-divider></v-divider>
            <v-layout column mt-2 class="description-line">
              <v-flex pb-3 class="snippit-subtitle">
                {{ $t('snippets.createSnippet.description') }}
              </v-flex>
              <v-flex>
                <v-textarea
                  outlined
                  no-resize
                  class="description-textarea"
                  :rules="[rules.description]"
                  v-model="editedDescription">
                </v-textarea>
              </v-flex>
            </v-layout>
            <v-divider></v-divider>
            <v-layout column mt-2 class="code-line">
              <v-flex pb-3 class="snippit-subtitle">
                {{ $t('snippets.createSnippet.code') }}
              </v-flex>
              <v-flex>
                <v-textarea
                filled
                outlined
                class="code-textarea"
                :rules="[rules.code, rules.codeNotBlankWithSpaces]"
                v-model="editedCode"
                ></v-textarea>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
      </v-layout>
    <v-layout align-center class="snippet-detail-edit-btns" justify-center v-if="userIsOwner">
      <v-flex v-if="!editing" shrink>
        <v-btn rounded outlined color="primary" @click="beginEditing">
          {{ $t('snippets.snippetDetail.edit') }}
        </v-btn>
      </v-flex>
      <v-flex shrink v-else>
        <v-layout>
          <v-flex justify-center align-center mx-1>
            <v-btn rounded outlined color="red" @click="exitEdit" :disabled="saving">
              {{ $t('snippets.snippetDetail.cancel') }}
            </v-btn>
          </v-flex>
          <v-flex justify-center align-center mx-1>
            <v-btn color="info" @click="saveEdit" :disabled="!allRulesAlright || saving">
              {{ $t('snippets.snippetDetail.done') }}
              <v-icon right dark>mdi-cloud-upload</v-icon>
            </v-btn>
          </v-flex>
        </v-layout>
      </v-flex>
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
import helpers from '@/functions/helpers.js'

export default {
  title () { return this.$t('titles.detail') },
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
      unreportDialog: false,
      reportMessage: '',
      error_image: false,
      editing: false,
      editedTitle: '',
      editedDescription: '',
      editedCode: '',
      owner: null,
      saving: false
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
          const usr = this.$store.getters.user
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
        promise = snippets.unflagSnippet(this.snippet.id, {
          baseUri: urls.mailBaseUrl + this.$route.path
        })
      } else {
        promise = snippets.flagSnippet(this.snippet.id,{
          baseUri: urls.mailBaseUrl + this.$route.path
        }
        )
      }
      promise.then(r => { 
        this.snippet.flagged = !this.snippet.flagged 
        this.owner.reputation += this.snippet.flagged ? -10 : 10
        this.flagging = false
        if (this.snippet.flagged) {
          this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.snippetFlagged')) 
        } else {
          this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.snippetUnflagged'))
        }
      })
      .catch(e => e)
    },
    report () {
      this.reporting = true
      if (this.snippet.reported && !this.snippet.reportedDismissed) {
        this.unreportDialog = true;
      } else if(this.snippet.reported && this.snippet.reportedDismissed){
        this.reporting = false
        this.$store.dispatch('snackWarning', this.$t('snippets.snippetDetail.report.alreadyReported'))
      } else {
        this.reportDialog = true
      }
    },
    sendReport () {
      snippets.reportSnippet(this.snippet.id, {
        detail: this.reportMessage,
        baseUri: urls.mailBaseUrl + this.$route.path
      }).then(r => {
        this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.report.success'))
        this.snippet.reported = true
      })
      .catch(e => {
        this.$store.dispatch('snackError', e.response.data.message)
      })
      .finally(() => this.resetReportData())
    },
    cancelReport () {
      this.resetReportData()
    },
    sendUnreport () {
      snippets.unreportSnippet(this.snippet.id)
          .then(r => {
            this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.report.successUnreport'))
            this.snippet.reported = false
          })
          .catch(e => {
            this.$store.dispatch('snackError', e.response.data.message)
          })
          .finally(() => this.resetReportData())
    },
    dismissAllReports () {
      snippets.unreportSnippet(this.snippet.id)
        .then(r => {
          this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.report.successDismissedAllReports'))
          this.snippet.showReportedWarning = false
        })
        .catch(e => {
          console.log(e.response)
          this.$store.dispatch('snackError', e.response.data.message)
        })
        .finally(() => this.resetReportData())
    },
    cancelUnreport () {
      this.resetReportData()
    },
    resetReportData () {
      this.reportMessage = ''
      this.reportDialog = false
      this.unreportDialog = false
      this.reporting = false
    },
    getReportColor(){
      if(!this.snippet.reported){
        return 'color-sandybrown'
      }
      else if(this.snippet.reportedDismissed){
        return 'color-yellow'
      }
      else{
        return ''
      }
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
      this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.copiedToClipboard'))
    },
    clearSelection () {
      let sel;
      if ((sel = document.selection) && sel.empty) {
        sel.empty();
      } else {
          if (window.getSelection) {
            window.getSelection().removeAllRanges();
          }
          const activeEl = document.activeElement;
          if (activeEl) {
              const tagName = activeEl.nodeName.toLowerCase();
              if (tagName === 'textarea' ||
                      (tagName === 'input' && activeEl.type === 'text')) {
                  // Collapse the selection to the end
                activeEl.selectionStart = activeEl.selectionEnd;
              }
          }
      }
    },
    titleAbreviation (name, size) {
      if (name != null && name.length > size) {
        const newName = name.substr(0, size - 2) + '...'
        return newName
      }
      return name
    },
    beginEditing () {
      this.editedTitle = this.snippet.title
      this.editedDescription = this.snippet.description
      this.editedCode = this.snippet.code
      this.editing = true
    },
    exitEdit () {
      this.editedTitle = ''
      this.editedDescription = ''
      this.editedCode = ''
      this.editing = false
    },
    saveEdit () {
      // Send to endpoint
      this.saving = true
      this.$Progress.start()
      snippets.editSnippet(this.snippet.id, {
        title: this.editedTitle,
        description: this.editedDescription,
        code: this.editedCode
      }).then(r => {
        this.snippet.title = this.editedTitle
        this.snippet.description = this.editedDescription
        this.snippet.code = this.editedCode
        this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.successEditing'))
        this.exitEdit()
      }).catch(e => {
        this.$store.dispatch('snackError', this.$t('snippets.snippetDetail.errorEditing'))
        this.$Progress.fail()
      }).finally(() => {
        this.$Progress.finish()
        this.saving = false
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
      return this.$store.getters.user.canReport
    },
    rules () {
      return {
        lengthBetween: () => validations.lengthBetween(this.reportMessage, 1, 300),
        title: () => validations.lengthBetween(this.editedTitle, 5, 50),
        description: () => validations.maxLength(this.editedDescription, 500),
        code: () => validations.lengthBetween(this.editedCode, 5, 30000),
        titleNotBlankWithSpaces: () => validations.notBlankWithSpaces(this.editedTitle),
        codeNotBlankWithSpaces: () => validations.notBlankWithSpaces(this.editedCode)
      }
    },
    allRulesAlright () {
      return Object.keys(this.rules).filter(rule => !rule.includes('lengthBetween') && this.rules[rule]() !== true).length === 0
    },
    readableDate () {
      const theDate = new Date(this.snippet.dateCreated)
      return this.$t('snippets.snippetDetail.postingDateTime', { year: theDate.getFullYear(), month: theDate.getMonth() + 1, day: theDate.getDate(), time: `${theDate.getHours()}:${theDate.getMinutes()}` })
    },
    userIsOwner () {
      return this.$store.getters.loggedIn && this.owner != null && parseInt(this.$store.getters.user.id) === parseInt(this.owner.id)
    }
  },
  mounted () {
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
      .catch(e => this.$store.dispatch('snackError', this.$t('snippets.snippetDetail.errorLoading')))
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
    },
    unreportDialog: {
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
@import '@/styles/snippetDetail.scss';
@import '@/styles/createSnippet.scss';
</style>
