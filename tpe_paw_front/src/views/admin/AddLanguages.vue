<template>
  <v-container id="admin-add-container">
    <v-card>
      <v-layout>
        {{ $t('admin.addLanguages.addLanguages') }}
      </v-layout>
      <v-layout>
        <v-flex>
          <v-text-field
            :append-outer-icon="'mdi-plus'"
            @click:append-outer="addLanguage"
            v-model="newLanguage">
          </v-text-field>
        </v-flex>
      </v-layout>
      <v-layout column>
        <v-flex
          v-for="(nLanguage, $index) in newLanguages"
          :key="nLanguage"
          >
          <v-layout>
            <v-flex>{{ nLanguage }}</v-flex>
            <v-flex><v-btn @click="deleteLanguage($index)"><v-icon>mdi-delete</v-icon></v-btn></v-flex>
          </v-layout>  
        </v-flex>
      </v-layout>
      <v-layout>
        <v-flex>
          <v-btn @click="postAllLanguages">{{ $t('admin.addLanguages.addNewLanguages') }}</v-btn>
        </v-flex>
      </v-layout>
    </v-card>
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
    // newLanguagesAsList () {
    //   return 
    // }
  }
}
</script>
