<template>
  <v-container id="snippet-create-container">
    <v-layout justify-center>
      <v-flex shrink class="view-title">
        {{ $t('snippets.createSnippet.createSnippet')}}
      </v-flex>
    </v-layout>
    <v-layout justify-center>
        <v-card class="snippet-create-card">
          <v-container>
            <v-layout class="title-row">
              <v-flex lg7 md7 sm7 xs7>
                <v-text-field
                  pt-12
                  outlined
                  dense
                  rounded
                  :rules="[rules.title, rules.titleNotBlankWithSpaces]"
                  v-model="title"
                  :label="$t('snippets.createSnippet.title')"></v-text-field>
              </v-flex>
              <v-flex lg5 md5 sm5 xs5>
                <language-select
                  :allowEmpty="false"
                  v-model="language"/>
              </v-flex>
            </v-layout>
            <v-divider></v-divider>
            <v-layout column mt-2 class="description-line">
              <v-flex>
                {{ $t('snippets.createSnippet.description') }}
              </v-flex>
              <v-flex>
                <v-textarea
                  rounded
                  outlined
                  no-resize
                  :rules="[rules.description]"
                  v-model="description">
                </v-textarea>
              </v-flex>
            </v-layout>
            <v-divider></v-divider>
            <v-layout column mt-2 class="code-line">
              <v-flex>
                {{ $t('snippets.createSnippet.code') }}
              </v-flex>
              <v-flex>
                <v-textarea
                rounded
                outlined
                no-resize
                :rules="[rules.code, rules.codeNotBlankWithSpaces]"
                v-model="code"
                ></v-textarea>
              </v-flex>
            </v-layout>
            <v-divider></v-divider>
            <v-layout class="tags-line">
              <v-flex md6 sm6 xs6>
                <tag-select
                  multiple
                  v-model="tags"
                  :closeOnSelect="false"
                  :label="$t('snippets.createSnippet.tags')"></tag-select>
              </v-flex>
              <v-flex class="save-btn-flex">
                <v-btn :disabled="!allRulesAlright" @click="saveSnippet">
                  {{ $t('snippets.createSnippet.create') }}
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
  title: 'Snippit - Create Snippet',
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
      snippetService.createSnippet({
        title: this.title,
        languageId: this.language.id,
        description: this.description,
        code: this.code,
        tags: this.tags.map(tag => tag.id)
      }).then(
        r => {
            let str = r.headers.location.split('/'); 
            let snippet_id = str[str.length - 1]
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
          }
      )
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
    }
  }
}
</script>

<style lang="scss">
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
      min-width: 500px;
      max-width: 500px;
    }
    .description-line {
    }
    .code-line {
    }
    .tags-line {
      .flex {
        max-width: 50%;
      }
      .save-btn-flex {
        display: flex;
        justify-content: flex-end;
        align-items: center;
      }
    }
  }
</style>
