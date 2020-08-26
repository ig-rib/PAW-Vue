<template>
  <v-container v-if="snippet != null" id="snippet-card-container">
      <v-card @click="goToSnippetDetail">
        <!-- User and language -->
        <v-container fill-height fluid>
          <v-row class="pt-0" align="start" justify="space-between">
              <v-col class="pt-0" cols="8">
                <v-list-item two-line>
                  <v-avatar class="mr-2" color="indigo">
                    <img v-if="!error" @error="error = true" class="profile-circle" :src="owner.icon"/>
                    <v-icon v-else>mdi-account-circle</v-icon>
                  </v-avatar>
                  <v-list-item-content> 
                    <v-list-item-title class="headline mb-1">{{ owner.username }}</v-list-item-title>
                    <v-list-item-subtitle>{{ snippet.date }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-col>

              <v-col cols="4">
                <v-row class="mr-2" justify="end">
                  <v-btn icon @click="fav">
                    <v-icon :disabled="faving">
                      mdi-heart{{(snippet.favorite) ? '' : '-outline'}}
                    </v-icon>
                  </v-btn>
                  <v-chip
                    color="light-blue"
                    label
                    text-color="white"
                    @mousedown="$event.stopPropagation()"
                    @click.stop="null"
                    @click="goToLanguageSnippets(language.id)"
                  >
                      {{ language.name }}
                  </v-chip>
                </v-row>
              </v-col>
          </v-row>

          <!-- Title and description -->
          <v-list-item two-line>
            <v-list-item-content class="pa-0"> 
              <v-list-item-title class="headline mb-1">{{ snippet.title }}</v-list-item-title>
              <p>{{ snippet.description }}</p>
            </v-list-item-content>
          </v-list-item>

          <!-- Code preview -->
          <v-textarea
            class="pl-2"
            name="input-7-1"
            filled
            label="Code preview"
            readonly
            :value="snippet.code"
          ></v-textarea>

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
  },
  mounted () {
    axiosFetcher.get(this.snippet.owner).then(r => { this.owner = r.data })
    axiosFetcher.get(this.snippet.language).then(r => { this.language = r.data })
  }
}
</script>

<style lang="scss">
#snippet-card-container {
  .account-circle {
    max-height: 30px;
    max-width: 30px;
  }
}
</style>
