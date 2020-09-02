<template>
  <v-container id="admin-add-container">
    <v-layout justify-center>
      <v-flex lg5 md6 sm7>
        <v-card class="admin-add-card" min-width="max-content" min-height="max-content">
          <v-container class="admin-add-inner-container" px-5 py-5>
            <v-layout class="admin-add-title">
              {{ $t('admin.addLanguages.addLanguages') }}
            </v-layout>
            <v-layout justify-center align-center class="admin-add-text-field">
              <v-flex>
                <v-text-field
                  rounded
                  outlined
                  dense
                  :placeholder="$t('admin.addLanguages.placeholder')"
                  @keyup.enter="addLanguage"
                  :append-outer-icon="'mdi-plus'"
                  @click:append-outer="addLanguage"
                  v-model="newLanguage">
                </v-text-field>
              </v-flex>
            </v-layout>
            <v-layout :align-center="!hasNewLanguages"
              :justify-center="!hasNewLanguages"
              class="admin-add-entity-list" column>
              <v-list v-if="hasNewLanguages" :class="!hasNewLanguages ? 'v-list-empty' : ''">
                <v-list-item
                  v-for="(nLanguage, $index) in newLanguages"
                  :key="nLanguage"
                  >
                  <v-layout wrap>
                    <v-flex class="list-name-flex" shrink>{{ nLanguage }}</v-flex>
                    <v-flex shrink ml-auto>
                      <v-btn icon @click="deleteLanguage($index)">
                        <v-icon>mdi-delete</v-icon>
                      </v-btn>
                    </v-flex>
                    <v-flex xs12 sm12 md12 lg12 xl12>
                      <v-divider></v-divider> 
                    </v-flex>
                  </v-layout>
                </v-list-item>
              </v-list>
              <v-flex v-else shrink>
                {{ $t('admin.addLanguages.emptyListText') }}
              </v-flex>
            </v-layout>
            <v-layout justify-end class="admin-add-add-btn">
              <v-flex shrink>
                <v-btn 
                  rounded
                  @click="postAllLanguages">{{ $t('admin.addLanguages.addNewLanguages') }}</v-btn>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import languages from '@/services/languages.js'

export default {
  data () {
    return {
      newLanguage: '',
      newLanguages: []
    }
  },
  methods: {
    addLanguage () {
      if (!this.newLanguages.includes(this.newLanguage)) {
        this.newLanguages.push(this.newLanguage)
        this.newLanguage = ''
      }
    },
    deleteLanguage (index) {
      this.newLanguages.splice(index, 1)
    },
    postAllLanguages () {
      let promises = this.newLanguages.map(languageName => languages.addLanguage(languageName))
      Promise.all(promises)
        .then(r => {
          // TODO handle responses
          let correct = r.filter(r => r.status < 300).length
        })
    }
  },
  computed: {
    hasNewLanguages () {
      return this.newLanguages.length > 0
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/adminAdd.scss';
</style>