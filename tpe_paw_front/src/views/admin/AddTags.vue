<template>
  <v-container id="admin-add-container">
    <v-layout justify-center>
      <v-flex lg5 md5 sm7>
        <v-card min-width="max-content" min-height="max-content" height="500px">
          <v-container class="admin-add-inner-container" px-5 py-5>
            <v-layout class="admin-add-title">
              {{ $t('admin.addTags.addTags') }}
            </v-layout>
            <v-layout class="admin-add-text-field">
              <v-flex>
                <v-text-field
                  @keyup.enter="addTag"
                  :append-outer-icon="'mdi-plus'"
                  @click:append-outer="addTag"
                  v-model="newTag">
                </v-text-field>
              </v-flex>
            </v-layout>
            <v-layout :align-center="!hasNewTags"
              :justify-center="!hasNewTags"
              class="admin-add-entity-list" column>
              <v-list v-if="hasNewTags" :class="!hasNewTags ? 'v-list-empty' : ''">
                <v-list-item
                  v-for="(nTag, $index) in newTags"
                  :key="nTag"
                  >
                  <v-layout wrap>
                    <v-flex class="list-name-flex" shrink>{{ nTag }}</v-flex>
                    <v-flex shrink ml-auto>
                      <v-btn icon @click="deleteTag($index)">
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
                {{ $t('admin.addTags.emptyListText') }}
              </v-flex>
            </v-layout>
            <v-layout justify-end class="admin-add-add-btn">
              <v-flex shrink>
                <v-btn @click="postAllTags">{{ $t('admin.addTags.addNewTags') }}</v-btn>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
      </v-flex>
    </v-layout>
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
        this.newTag = ''
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
    hasNewTags () {
      return this.newTags.length > 0
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/adminAdd.scss';
</style>