<template>
  <v-container id="admin-add-container">
    <v-card>
      <v-layout>
        {{ $t('admin.addTags.addTags') }}
      </v-layout>
      <v-layout>
        <v-flex>
          <v-text-field
            :append-outer-icon="'mdi-plus'"
            @click:append-outer="addTag"
            v-model="newTag">
          </v-text-field>
        </v-flex>
      </v-layout>
      <v-layout column>
        <v-flex
          v-for="(nTag, $index) in newTags"
          :key="nTag"
          >
          <v-layout>
            <v-flex>{{ nTag }}</v-flex>
            <v-flex><v-btn @click="deleteTag($index)"><v-icon>mdi-delete</v-icon></v-btn></v-flex>
          </v-layout>  
        </v-flex>
      </v-layout>
      <v-layout>
        <v-flex>
          <v-btn @click="postAllTags">{{ $t('admin.addTags.addNewTags') }}</v-btn>
        </v-flex>
      </v-layout>
    </v-card>
  </v-container>
</template>

<script>
import tags from '@/services/tags.js'

export default {
  data () {
    return {
      newTag: '',
      newTags: []
    }
  },
  methods: {
    addTag () {
      if (!this.newTags.includes(this.newTag)) {
        this.newTags.push(this.newTag)
      }
    },
    deleteTag (index) {
      this.newTags.splice(index, 1)
    },
    postAllTags () {
      let promises = this.newTags.map(tagName => tags.addTag(tagName))
      Promise.all(promises)
        .then(r => {
          // TODO handle responses
          let correct = r.filter(r => r.status < 300).length
        })
    }
  },
  computed: {
    // newTagsAsList () {
    //   return 
    // }
  }
}
</script>
