<template>
  <v-container id="snippet-create-container">
    <v-layout justify-center>
      <v-flex shrink class="view-title">
        {{ $t('snippets.createSnippet.createSnippet')}}
      </v-flex>
    </v-layout>
    <v-layout justify-center>
        <v-card v-if="loggedInAndUser" class="snippet-create-card">
          <v-container px-4>
            <v-layout mt-2 mb-1> 
              <v-flex lg7 md7 sm7 xs7 class="snippit-subtitle">
                {{ $t('snippets.createSnippet.title') }}
              </v-flex>
              <v-flex pl-3 lg5 md5 sm5 xs5 class="snippit-subtitle">
                {{ $t('snippets.createSnippet.language') }}
              </v-flex>
            </v-layout>
            <v-layout class="title-row">
              <v-flex pt lg7 md7 sm7 xs7>
                <v-text-field
                  class="custom-label-color"
                  outlined
                  dense
                  rounded
                  :rules="[rules.title, rules.titleNotBlankWithSpaces]"
                  v-model="title"
                  ></v-text-field>
              </v-flex>
              <v-flex lg5 md5 sm5 xs5 align-start>
                <language-select
                  align-start
                  :allowEmpty="false"
                  v-model="language"/>
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
                  v-model="description">
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
                v-model="code"
                ></v-textarea>
              </v-flex>
            </v-layout>
            <v-divider></v-divider>
            <v-layout mt-1 py-2>
              <v-flex class="snippit-subtitle">
                {{ $t('snippets.createSnippet.tags') }}
              </v-flex>
            </v-layout>
            <v-layout class="tags-line">
              <v-flex lg9 md9 sm6 xs6>
                <tag-select
                  multiple
                  v-model="tags"
                  :closeOnSelect="false"
                  :label="$t('snippets.createSnippet.tags')"></tag-select>
              </v-flex>
              <v-flex class="save-btn-flex" lg2 md2 sm6 xs6 align-start>
                <v-btn :disabled="!allRulesAlright" large color="info" @click="saveSnippet">
                  {{ $t('snippets.createSnippet.create') }}
                   <v-icon right dark>mdi-cloud-upload</v-icon>
                </v-btn>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
        <v-card width="600px" class="error-card" v-else-if="isAdmin">
          <v-container>
            <v-layout mb-8 mt-4 justify-center class="error-logo-layout">
              {{ $t('error.youAreAnAdmin') }}
            </v-layout>
            <v-layout  my-2 justify-center class="error-title-layout">
              <v-flex shrink>
                {{ $t('error.adminsCantCreateSnippets') }}
              </v-flex>
            </v-layout>
            <v-layout mb-2 justify-center class="error-gohome-text-layout">
              {{ $t('error.goBackHome') }}
            </v-layout>
            <v-layout my-3 justify-center class="error-gohome-btn-layout">
              <v-flex shrink>
                <v-btn rounded outlined color="primary" @click="goHome">
                  {{ $t('error.goHome') }}
                </v-btn>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
        <v-card width="600px" class="error-card" v-else>
          <v-container>
            <v-layout mb-8 mt-4 justify-center class="error-logo-layout">
              {{ $t('error.youDontHaveAnAccount') }}
            </v-layout>
            <v-layout  my-2 justify-center class="error-title-layout">
              <v-flex shrink>
                {{ $t('error.mustBeAUser') }}
              </v-flex>
            </v-layout>
            <v-layout mb-2 justify-center class="error-gohome-text-layout">
              {{ $t('error.login') }}
            </v-layout>
            <v-layout my-3 justify-center class="error-gohome-btn-layout">
              <v-flex shrink>
                <v-btn rounded outlined color="primary" @click="goToLogin">
                  {{ $t('error.goToLogin') }}
                </v-btn>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
    </v-layout>
  </v-container>
</template>

<script>
import snippetService from '@/services/snippets.js'
import validations from '@/functions/validations'
import helpers from '@/functions/helpers.js'

export default {
  title () { return this.$t('titles.create') },
  data () {
    return {
      title: '',
      language: null,
      description: '',
      code: '',
      tags: []
    }
  },
  methods: {
    saveSnippet () {
      this.$Progress.start()
      snippetService.createSnippet({
        title: this.title,
        languageId: this.language.id,
        description: this.description,
        code: this.code,
        tags: this.tags.map(tag => tag.id)
      }).then(
        r => {
            const str = r.headers.location.split('/'); 
            const snippet_id = str[str.length - 1]
            this.$router.push({
              name: 'snippet-detail',
              params: {
              id: snippet_id
            }
          })
        }
      ).catch(e => {
            if (e.response.status === 403) {
              this.$store.dispatch('snackError', e.response.data.message)
            }
            this.$Progress.fail()
          }
      ).finally(() => this.$Progress.end())
    },
    goHome () {
      this.$router.push({
        name: 'feed'
      })
    },
    goToLogin () {
      this.$router.push({
        name: 'login'
      })
    }
  },
  computed: {
    rules () {
      return {
        title: () => validations.lengthBetween(this.title, 5, 50),
        description: () => validations.maxLength(this.description, 500),
        code: () => validations.lengthBetween(this.code, 5, 30000),
        titleNotBlankWithSpaces: () => validations.notBlankWithSpaces(this.title),
        codeNotBlankWithSpaces: () => validations.notBlankWithSpaces(this.code)
      }
    },
    allRulesAlright () {
      return Object.keys(this.rules).filter(rule => this.rules[rule]() !== true).length === 0 &&
        this.language != null
    },
    loggedInAndUser () {
      return this.$store.getters.loggedIn && !this.$store.getters.user.admin
    },
    isAdmin () {
      return this.$store.getters.loggedIn && this.$store.getters.user.admin
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/main.scss';
@import "@/styles/error.scss";

  #snippet-create-container {
    .multiselect__single {
      white-space: nowrap !important;
      overflow: hidden !important;
    }
    .title-row {
      .flex {
        display: flex;
        align-items: center;
      }
    }
    .snippet-create-card {
      padding-right: 10px;
      padding-left: 10px;
      max-width: 600px;
      min-width: 600px;
      border-radius: 10px;
      .custom-label-color .v-label {
        opacity: 1;
      }
    }
    .description-textarea {
      border-radius: 10px;
    }
    .code-textarea {
      border-radius: 10px;
    }
    .tags-line {
      align-content: space-between;
      .flex {
        max-width: 100%;
      }
      .save-btn-flex {
        display: flex;
        flex: 1;
        justify-content: flex-end;
        align-items: center;
      }
    }
    .snippit-subtitle {
      font-size: 20px;
    }
  }
</style>
