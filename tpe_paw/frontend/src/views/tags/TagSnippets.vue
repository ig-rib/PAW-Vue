<template>
  <v-container>
    <v-layout justify-center align-center>
      <v-flex class="view-title" shrink>
        {{ $t('tags.tagSnippets', { tagName: tag.name}) }}
      </v-flex>
      <v-flex shrink ml-5>
        <v-btn
          elevation="0"
          class="title-tag-follow-btn ma-1"
          color="primary"
          :outlined="!tag.userFollowing"
          @click="followBtnClick"
          @mousedown.stop="null"
          @click.stop="null"
        >
        <p v-if="tag.userFollowing" class="tag-follow-btn-txt">
          {{ $t('tags.following') }}
        </p>
        <p v-else class="tag-follow-btn-txt">
          {{ $t('tags.follow') }}
        </p>
        </v-btn>
      </v-flex>
      <v-flex ml-5 v-if="isAdmin" shrink>
        <!-- <v-layout justify-start align-center> -->
          <v-btn class="title-delete-btn" @click="deleting = true" icon>
           <v-icon>mdi-delete</v-icon>
          </v-btn>
        <!-- </v-layout> -->
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
    title () { return this.$t('titles.tagsSnippet') },
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
          .then(r => { 
            this.deleting = false
            this.$store.dispatch('snackSuccess', this.$t('tags.successDelete'))
            this.$router.replace({
              name: 'tags-main'
            })
          })
          .catch(e => this.$store.dispatch('snackError', this.$t('tags.errorDelete')))
      },
      followBtnClick () {
        if (this.tag.userFollowing) {
          this.unfollowTag()
        } else {
          this.followTag()
        }
      },
      followTag: function () {
        tags.followTag(this.tag.id)
          .then(
            this.tag.userFollowing = true
          )
          .catch(e => this.$store.dispatch('snackError', this.$t('tags.errorFollow')))
        event.stopPropagation()
        },
      unfollowTag: function () {
        tags.unfollowTag(this.tag.id)
          .then(
            this.tag.userFollowing = false
          )
          .catch(e => this.$store.dispatch('snackError', this.$t('tags.errorUnfollow')))
        event.stopPropagation()
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
.title-tag-follow-btn {
  transition: 0.3s;
  // font-size: 50px;
  height: 60px;
  border-radius: 20px !important;
  font-weight: 500;
  padding: 3px !important;
}

.title-tag-follow-btn-txt{
  padding-top: 18px;
  padding-left: 5px;
  padding-right: 5px;
  font-size: 13px;
}
</style>
