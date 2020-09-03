<template>
  <v-container v-if="snippet != null" id="snippet-card-container">
      <v-card min-width="300px" class="snippet-card-card" @click="goToSnippetDetail">
        <v-container>
        <!-- User and language -->
          <v-layout dense>
              <v-flex dense>
                <v-list-item two-line>
                  <v-avatar class="mr-2" color="indigo">
                    <img v-if="!error" @error="error = true" class="profile-circle" :src="owner.icon"/>
                    <v-icon v-else>mdi-account-circle</v-icon>
                  </v-avatar>
                  <v-list-item-content> 
                    <v-list-item-title class="headline mb-1">{{ owner.username }}</v-list-item-title>
                    <v-list-item-subtitle>{{ standardDate }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-flex>

              <v-flex class="pr-2 snippet-language-flex">
                <v-layout class="pr-2" justify-end>
                  <v-flex class="flex-grow-0 pa-0">
                    <v-btn icon
                      color="red"
                      @mousedown="$event.stopPropagation()"
                      @click="fav">
                      <v-icon :disabled="faving">
                        mdi-heart{{(snippet.favorite) ? '' : '-outline'}}
                      </v-icon>
                    </v-btn>
                  </v-flex>
                  <v-flex v-if="snippet.flagged">
                    <v-btn icon aria-disabled>
                      <v-icon color="red">mdi-flag</v-icon>
                    </v-btn>
                  </v-flex>
                  <v-flex class="flex-grow-0 pa-0">
                    <v-btn
                      outlined
                      color="light-blue"
                      class="white--text"
                      elevation="0"
                      @mousedown="$event.stopPropagation()"
                      @click.stop="null"
                      @click="goToLanguageSnippets(language.id)"
                    >
                        {{ language.name }}
                    </v-btn>
                  </v-flex>
                </v-layout>
              </v-flex>
          </v-layout>

          <!-- Title and description -->
          <v-layout class="title-description-layout" column>
            <!-- <v-flex>
              <p>{{snippet.title}}</p>
            </v-flex>
            <v-flex>
              <p>{{snippet.description}}</p>
            </v-flex> -->
            <v-list-item two-line>
              <v-list-item-content class="pa-0"> 
                <v-list-item-title class="headline mb-1">{{ snippet.title }}</v-list-item-title>
                <p>{{ snippet.description }}</p>
              </v-list-item-content>
            </v-list-item>
          </v-layout>

          <!-- Code preview -->
          <v-layout class="code-layout">
            <v-textarea
              no-resize
              hide-details
              rounded
              class="pl-2 snippet-card-code-textarea"
              readonly
              filled
              :value="snippet.code"
            >
              <p class="snippet-card-code-fade">a</p>
            </v-textarea>
          </v-layout>
        </v-container>
      </v-card>
  </v-container>
  
</template>

<script>
import snippets from '@/services/snippets.js'
import axiosFetcher from '@/services/axiosFetcher.js'

export default {
  name: 'SnippetComponent', 

  components: {
    // TagComponent
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
      return this.snippet.dateCreated.split('T')[0]
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
      .catch(e => {
        this.$store.dispatch('snackError', e.message)
      })
      .finally(() => { this.faving = false })
    },
  },
  mounted () {
    axiosFetcher.get(this.snippet.owner).then(r => { this.owner = r.data })
    axiosFetcher.get(this.snippet.language).then(r => { this.language = r.data })
  }
}
</script>

<style lang="scss">
@import '@/styles/colours.scss';

#snippet-card-container {
  max-width: 450px;
  // max-width: 33%;
  // margin: 5%;
  .account-circle {
    max-height: 30px;
    max-width: 30px;
  }
  .title-description-layout {
    // max-width: 300px;
  }
  .code-layout {
    // max-width: 300px;
  }
  .snippet-language-flex {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
  .snippet-card-code-textarea {
    border-radius: 10px;
    textarea {
      overflow: hidden !important;
      -webkit-user-select: none; /* Safari */        
      -moz-user-select: none; /* Firefox */
      -ms-user-select: none; /* IE10+/Edge */
      user-select: none; /* Standard */
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
      opacity: 0.1 !important;
    }
  }
}
</style>
