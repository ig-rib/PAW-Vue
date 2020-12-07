<template>
  <div>
    <v-layout class="view-title-layout">
      <v-flex shrink class="view-title">
        {{ $t('tags.title') }}
      </v-flex>
    </v-layout>
    <v-layout 
      class="grid-error-layout"
      align-center
      column
      justify-center
      v-if="status === 'e'">
      <v-flex my-2 shrink>
        {{ $t('error.grid.tags') }}
      </v-flex>
      <v-flex my-2>
        <v-btn @click="tryLoadingAgain">{{ $t('error.grid.tryAgain') }}</v-btn>
      </v-flex>
    </v-layout>
    <v-layout class="grid-progress-circle" v-if="status === 'l'" justify-center>
      <v-progress-circular
        :size="70"
        :width="7"
        color="primary"
        indeterminate>
      </v-progress-circular>
    </v-layout>
    <v-layout v-else wrap justify-center>
      <v-flex my-2
        :class="`lg3 md4 sm4 ${ $vuetify.breakpoint.lgAndUp ? 'px-2' : 'mx-2' }`"
        v-for="tag in tags"
        :key="tag.id">
        <!-- <v-container> -->

        <v-card elevation="1"
          class="card-chip"
          @click="goToTagSnippets(tag.id)"
          >
          <v-layout width="100%">
            <v-flex class="tag-name-flex">
              {{ tag.name }}
            </v-flex>
            <v-flex shrink ml-auto>
              <v-btn
                elevation="0"
                class="tag-follow-btn ma-1"
                color="primary"
                :outlined="!tag.userFollowing"
                @click="tag.userFollowing ? unfollowTag(tag) : followTag(tag)"
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
          </v-layout>            
        </v-card>
        <!-- </v-container> -->
      </v-flex>
    </v-layout>

    <v-layout justify-center my-10 class="text-center">
      <v-pagination
        v-if="status === ''"
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </v-layout>
  </div>
</template>

<script>
import tagService from '@/services/tags.js'
import helpers from '@/functions/helpers.js'

export default {
  title: 'Snippit - Tags',
  name: 'tagsMain',
  data () {
    return {
      tags: [],
      links: [],
      pagination: {
        page: 1,
        length: 1,
        visible: 7
      },
      status: 'l'
    }
  },
  methods: {
    paginationChange () {
      this.status = 'l'
      const queryParams = {}
      Object.assign(queryParams, this.$route.query)
      queryParams.page = this.pagination.page
      this.$router.replace({
        query: queryParams
      })
      tagService.searchTags(queryParams)
        .then(values => {
          this.tags = values.data
          this.links = helpers.parseLinks(values.headers.link)
          this.status = ''
        })
        .catch(error => { 
          console.log(error)
          this.status = 'e'
          })
    },
    tryLoadingAgain () {
      this.status = 'l'
      const queryParams = {}
      Object.assign(queryParams, this.$route.query)
      queryParams.page = this.pagination.page
      tagService.searchTags(queryParams)
        .then(values => {
          this.tags = values.data
          this.links = helpers.parseLinks(values.headers.link)
          this.status = ''
        })
        .catch(error => { 
          console.log(error)
          this.status = 'e'
          })
    },
    followTag: function (tag) {
      tagService.followTag(tag.id)
        .then(
          tag.userFollowing = true
        )
      event.stopPropagation()
    },
    unfollowTag: function (tag) {
      tagService.unfollowTag(tag.id)
        .then(
          tag.userFollowing = false
        )
      event.stopPropagation()
    },
    handleSearchResponse (response) {
      this.tags = response.data
      this.links = helpers.parseLinks(response.headers.link)
      this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10)
      this.status = ''
    },
    goToTagSnippets (tagId) {
      this.$router.push({
        name: 'tag-snippets',
        params: {
          id: tagId
        }
      })
    }
  },
  mounted () {
    const queryParams = this.$route.query
    tagService.searchTags(queryParams)
      .then(response => {
        this.pagination.page = parseInt(queryParams.page) || 1
        this.handleSearchResponse(response)    
      })
      .catch(e => {
        console.log(e)
        this.status = 'e'
      })
    this.$on('searchResults', r => {
      this.pagination.page = parseInt(queryParams.page) || 1
      this.handleSearchResponse(r)
    })
  }
}

</script>

<style lang="scss">
@import '@/styles/cardChip.scss';

.tag-name-flex {
  // min-width: 50%;
  align-items: center;
  display: flex;
}

.tag-follow-btn {
  transition: 0.3s;
  border-radius: 20px !important;
  font-weight: 400;
  padding: 3px !important;
}

.tag-follow-btn-txt{
  padding-top: 18px;
  padding-left: 5px;
  padding-right: 5px;
  font-size: 13px;
}

</style>
