<template>
  <v-container v-if="snippet != null" id="snippet-card-container">
      <v-card
        min-width="350px !important"
        min-height="300px"
        max-width="400px !important"
        class="snippet-card-card" 
        :class="`snippet-card-card`" @click="goToSnippetDetail">
        <!-- User and language -->
          <v-layout class="user-language-layout" dense>
              <v-flex class="username-date-flex" dense>
                <v-list-item two-line>
                  <v-flex shrink mr-2 class="snippet-owner-icon-flex">
                      <!-- class="owner-image" -->
                    <v-img
                      height="50px"
                      width="50px"
                    v-if="!error" @error="error = true" :src="owner.icon"/>
                    <v-icon v-else size="50">mdi-account-circle</v-icon>
                  </v-flex>
                  <v-list-item-content> 
                    <v-list-item-title class="username-text headline mb-1">{{ owner.username }}</v-list-item-title>
                    <v-list-item-subtitle>{{ standardDate }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-flex>
              <v-flex class="snippet-language-flex">
                <v-layout class="pr-2" justify-end>
                  <v-flex v-if="snippet.deleted" class="flex-grow-0 pa-1">
                    <v-btn class="snippet-toggle-btn" icon aria-disabled>
                      <v-icon color="red">mdi-delete-sweep</v-icon>
                    </v-btn>
                  </v-flex>
                  <v-flex v-else class="flex-grow-0 pa-1">
                    <v-btn 
                      class="fav-btn snippet-toggle-btn color-crimson"
                      icon
                      @mousedown="$event.stopPropagation()"
                      @click="fav">
                      <v-icon :disabled="faving">
                        mdi-heart{{(snippet.favorite) ? '' : '-outline'}}
                      </v-icon>
                    </v-btn>
                  </v-flex>
                  <v-flex class="pa-1" v-if="snippet.flagged">
                    <v-btn class="snippet-toggle-btn" icon aria-disabled>
                      <v-icon color="red">mdi-flag</v-icon>
                    </v-btn>
                  </v-flex>
                  <v-flex class="flex-grow-0 pa-1">
                    <v-btn
                      outlined
                      color="light-blue"
                      class="white--text"
                      elevation="0"
                      @mousedown="$event.stopPropagation()"
                      @click.stop="null"
                      @click="goToLanguageSnippets(language.id)"
                    >
                        {{ titleAbreviation(language.name,13) }}
                    </v-btn>
                  </v-flex>
                </v-layout>
              </v-flex>
          </v-layout>

          <!-- Title and description -->
          <v-layout class="title-description-layout" column>
            <v-list-item two-line>
              <v-list-item-content class="pa-0"> 
                <div class="snippet-title headline mb-1">{{ snippet.title }}</div>
                <div class="snippet-description">{{ snippet.description }}</div>
              </v-list-item-content>
            </v-list-item>
          </v-layout>
          <v-layout class="code-layout">

            <ssh-pre @click="$event.stopPropagation()" @click.stop="null" @mousedown="$event.stopPropagation()" class="snippet-card-code-textarea" language="js" @copied="copiedToClipboard()">
              {{snippet.code}}
            </ssh-pre>
          </v-layout>
      </v-card>
  </v-container>
  
</template>

<script>
import snippets from '@/services/snippets.js'
import axiosFetcher from '@/services/axiosFetcher.js'
import SshPre from 'simple-syntax-highlighter'
import 'simple-syntax-highlighter/dist/sshpre.css'

export default {
  name: 'SnippetComponent', 

  components: {
    'ssh-pre': SshPre
  },
  props: {
    snippet: {
      type: Object,
      default: () => {}
    }
  },

  data () {
    return {
      error: false,
      faving: false,
      owner: {},
      language: {}
    }
  },
  computed: {
    standardDate () {
      const theDate = new Date(this.snippet.dateCreated)
      return this.$t('snippets.postingDate', { year: theDate.getFullYear(), month: theDate.getMonth() + 1, day: theDate.getDate() })
    },
    cardMaxWidthClass () {
      if (this.$vuetify.breakpoint.lgAndUp) {
        return 'max-800';
      } else if (this.$vuetify.breakpoint.md) {
        return 'max-600';
      } else {
        return 'max-400';
      }
    }
  },
  methods: {
    goToLanguageSnippets (langId) {
      event.stopPropagation()
      this.$router.push({
        name: 'language-snippets',
        params: {
          id: langId
        }
      })
    },
    goToSnippetDetail () {
      this.$router.push({
        name: 'snippet-detail',
        params: {
          id: this.snippet.id
        }
      })
    },
    fav () {
      event.stopPropagation()
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
    copiedToClipboard () {
      this.$store.dispatch('snackSuccess', this.$t('snippets.snippetDetail.copiedToClipboard'))
    },
    titleAbreviation (name, size) {
      if(this.snippet.flagged){
        size -= 3
      }
      if(this.snippet.deleted){
        size -= 3
      }

      if (name != null && name.length > size) {
        const newName = name.substr(0, size - 2) + '...'
        return newName
      }
      return name
    }
  },
  mounted () {
    axiosFetcher.get(this.snippet.owner).then(r => { this.owner = r.data })
    axiosFetcher.get(this.snippet.language).then(r => { this.language = r.data })
  }
}
</script>

<style lang="scss">
@import '@/styles/colours.scss';
@import '@/styles/main.scss';

#snippet-card-container {
  .account-circle {
    max-height: 30px;
    max-width: 30px;
  }
  .user-language-layout {
    flex-grow: 0;
  }
  .username-date-flex {
    max-width: 50%;
    .username-text {
      font-size: 18px !important;
    }
  }
  .title-description-layout {
    flex-grow: 0;
  }
  .code-layout {
    flex-grow: 1;
    div {
      height: auto;
    }
    .v-input__control {
      div {
        height: 100%;
      }
      height: 100%;
    }
  }
  .snippet-language-flex {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
  .snippet-card-code-textarea {
    border-radius: 10px;
    max-height: 300px;
    overflow: hidden;
    width: 100%;
    .v-ripple__container, .v-ripple__container > div {
      opacity: 0.1 !important;
      border-radius: 10px !important;
    }
  }
  .snippet-card-code-fade {
    position: absolute;
  }
  .snippet-card-card {
    -webkit-user-select: none; /* Safari */        
    -moz-user-select: none; /* Firefox */
    -ms-user-select: none; /* IE10+/Edge */
    user-select: none; /* Standard */
    .v-ripple__container {
      v-ripple__animation.v-ripple__animation--visible.v-ripple__animation--in {
        border-radius: 10px !important;
      }
      opacity: 0.1 !important;
      border-radius: 10px;
    }
    display: flex;
    padding: 3% 5% 4% 3%;
    flex-direction: column;
    &, &:before {
      border-radius: 10px;
    }
  }
  .snippet-card-inner-container {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  .snippet-toggle-btn {
    i, .v-btn, i:active {
      font-size: 30px;
    }
  }
  .snippet-toggle-btn.fav-btn {
    i:hover {
      font-size: 35px;
    }
  }
  .snippet-toggle-btn::before {
    background-color: transparent !important;
  }
  .snippet-toggle-btn.fav-btn::before:hover {
    font-size: 60px;
  }
  .snippet-title {
    font-weight: 400;
  }
  .snippet-description {
    font-weight: 300;
    margin-bottom: 2%;
    max-width: 100%;
  }
  .snippet-owner-icon-flex {
    border-radius: 100%;
    overflow: hidden;
  }
}
</style>
