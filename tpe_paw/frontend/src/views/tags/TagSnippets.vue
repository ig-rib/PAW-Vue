<template>
  <v-container>
    <v-layout>
      <v-flex class="view-title">
        {{ $t('tags.tagSnippets', { tagName: tag.name}) }}
      </v-flex>
      <v-flex v-if="isAdmin" ml-auto>
        <v-btn @click="deleting = true" icon>
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </v-flex>
    </v-layout>
    <snippet-grid ref="tagSnippets"></snippet-grid>
    <v-dialog v-model="deleting">
      <v-card>
        <v-card-title>{{ $t('admin.confirmDeletion') }}</v-card-title>
        <v-card-text>{{ $t('admin.tagDeletionDisclaimer') }}</v-card-text>
        <v-card-actions>
          <v-btn @click="deleteTag">{{ $t('admin.confirm') }}</v-btn>
          <v-btn @click="deleting = false">{{ $t('admin.cancel') }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import tags from '@/services/tags.js'

export default {
    name: 'TagsSnippets',
    data () {
      return {
        tag: {},
        deleting: false
      }
    },
    methods: {
      deleteTag () {
        tags.deleteTag(this.$route.params.id)
          // TODO handle responses accordingly
          .then(r => { 
            this.deleting = false
            this.$router.push({
              name: 'tags'
            })
          })
      }
    },
    computed: {
      isAdmin () {
        return this.$store.getters.user.admin
      }
    },
    mounted () {
      tags.getTag(this.$route.params.id).then(r => {
        this.tag = r.data
    })
    this.$on('searchResults', (r) => this.$refs.tagSnippets.$emit('searchResults', r))
  }
}

</script>
