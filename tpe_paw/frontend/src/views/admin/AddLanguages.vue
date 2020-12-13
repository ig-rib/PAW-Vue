<template>
  <v-container id="admin-add-container">
    <v-layout v-if="isAdmin" justify-center pt-10>
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
                  v-model="newLanguage"
                  :rules="[rules.newLanguage]">
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
              <v-flex pl-2 align-center>
                <v-chip medium>
                {{newLanguages.length}} {{ $t('admin.addLanguages.entered')}}
                </v-chip>
              </v-flex>
              <v-flex ml-3 shrink align-start>
                <v-btn 
                  rounded
                  align-start
                  :disabled="newLanguages.length < 1"
                  color="#64b5f6"
                  @click="postAllLanguages">{{ $t('admin.addLanguages.addNewLanguages') }}</v-btn>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
      </v-flex>
    </v-layout>
    <error v-else>
    </error>
  </v-container>
</template>

<script>
import languages from '@/services/languages.js'
import validations from '@/functions/validations'
import Error from '@/views/error/Error.vue'

export default {
  title () {
    return this.$t('titles.addLanguages')
  },
  components: {
    error: Error
  },
  data () {
    return {
      newLanguage: '',
      newLanguages: []
    }
  },
  methods: {
    addLanguage () {
      if (this.allRulesAlright && !this.newLanguages.includes(this.newLanguage)) {
        this.newLanguages.push(this.newLanguage)
        this.newLanguage = ''
      }
    },
    deleteLanguage (index) {
      this.newLanguages.splice(index, 1)
    },
    postAllLanguages () {
      const promises = this.newLanguages.map(languageName => languages.addLanguage(languageName))
      Promise.all(promises)
        .then(r => {
            this.$store.dispatch('snackSuccess', this.$t('admin.addLanguages.success'))
            this.newLanguages = []
            this.newLanguage = ''
        })
        .catch(e => {
          this.$store.dispatch('snackError', this.$t('admin.addLanguages.error'))
        })
    }
  },
  computed: {
    rules () {
      return {
        newLanguage: () => validations.lengthBetween(this.newLanguage, 1, 50)
      }
    },
    allRulesAlright () {
      return Object.keys(this.rules).filter(rule => this.rules[rule]() !== true).length === 0
    },
    hasNewLanguages () {
      return this.newLanguages.length > 0
    },
    isAdmin () {
      return this.$store.getters.user != null && this.$store.getters.user.admin
    }
  },
  mounted () {
    if (this.isAdmin) {
      this.$router.replace({ name: 'error' })
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/colours.scss';
@import "@/styles/main.scss";
@import '@/styles/adminAdd.scss';
</style>
