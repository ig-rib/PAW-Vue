<template>
  <div id="snippet-card-container"> 
      <v-card @click="goToSnippetDetail">
          <!-- User and language -->
          <div fill-height fluid>
              <v-row class="pt-0" align="start" justify="space-between">
                  <v-col class="pt-0" cols="8">
                    <v-list-item two-line>
                      <v-avatar class="mr-2" color="indigo">
                        <img v-if="!error" @error="error = true" class="profile-circle" :src="snippetData.owner.icon"/>
                        <v-icon v-else>mdi-account-circle</v-icon>
                      </v-avatar>
                      <v-list-item-content> 
                        <v-list-item-title class="headline mb-1">{{ snippetData.owner.username }}</v-list-item-title>
                        <v-list-item-subtitle>{{ snippetData.date }}</v-list-item-subtitle>
                      </v-list-item-content>
                    </v-list-item>
                  </v-col>

                  <v-col cols='4'>
                    <v-icon class="mr-1 mt-1">
                      mdi-heart
                    </v-icon>
                    <v-chip
                      class="mt-2"
                      color="light-blue"
                      label
                      text-color="white"
                      @click="goToLanguageSnippets(snippetData.language.id)"
                    >
                        {{ snippetData.language.name }}
                    </v-chip>
                  </v-col>
              </v-row>

              <!-- Title and description -->
              <v-list-item two-line>
                <v-list-item-content class="pa-0"> 
                  <v-list-item-title class="headline mb-1">{{ snippetData.title }}</v-list-item-title>
                  <p>{{ snippetData.description }}</p>
                </v-list-item-content>
              </v-list-item>

              <!-- Code preview -->
              <v-textarea
                class="pl-2"
                name="input-7-1"
                filled
                label="Code preview"
                readonly
                :value="snippetData.code"
              ></v-textarea>

          </div>
      </v-card>
  </div>
  
</template>

<script>
export default {
  name: 'SnippetComponent', 

  components: {
    // TagComponent
  },
  props: {
    snippetData: Object
  },

  data () {
    return {
      error: false
    }
  },
  methods: {
    goToLanguageSnippets (langId) {
      this.$router.push({
        name: 'language-snippets',
        params: {
          id: langId
        }
      })
      event.stopPropagation()
    },
    goToSnippetDetail () {
      this.$router.push({
        name: 'snippet-detail',
        params: {
          id: this.snippetData.id
        }
      })
    }
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
