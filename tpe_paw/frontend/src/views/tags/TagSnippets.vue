<template>
  <v-container>
    <v-layout align-center>
      <v-flex class="view-title" shrink>
        {{ $t('tags.tagSnippets', { tagName: tag.name}) }}
      </v-flex>
      <v-flex v-if="isAdmin" ml-10 pb-5>
        <v-layout justify-start align-center>
          <v-btn @click="deleting = true" icon>
          <v-icon size="60">mdi-delete</v-icon>
          </v-btn>
        </v-layout>
      </v-flex>
    </v-layout>
    <snippet-grid ref="tagSnippets"></snippet-grid>
    <v-dialog content-class="delete-dialog" v-model="deleting">
          <v-card class="dialog-card">
            <v-card-title mb-5 class="justify-center">{{ $t('admin.confirmDeletion') }}</v-card-title>
            <v-card-subtitle class="justify-center dialog-subtitle">{{ $t('admin.tagDeletionDisclaimer', { tagName: tag.name}) }}</v-card-subtitle>
            <v-layout px-3 pb-5 justify-center>
              <v-flex shrink mr-2>
                <v-btn rounded outlined color="#2286c3" @click="deleteTag">{{ $t('admin.confirm') }}</v-btn>
              </v-flex>
              <v-flex shrink>
                <v-btn rounded outlined color="red" @click="deleting=false">{{ $t('admin.cancel') }}</v-btn>
              </v-flex>
            </v-layout>
          </v-card>
        </v-dialog>
  </v-container>
</template>

<script>
import tags from '@/services/tags.js'

export default {
  title: 'Snippit - Tags',
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

<style lang="scss">
@import '@/styles/adminAdd.scss';
</style>

