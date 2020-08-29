<template>
  <v-container>
    <div>
      <p> {{ $t('tags.title') }} </p>
      <v-layout wrap justify-center>
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
                <template v-if="tag.userFollowing">
                  {{ $t('tags.following') }}
                </template>
                <template v-else>
                  {{ $t('tags.follow') }}
                </template>
                </v-btn>
              </v-flex>
            </v-layout>            
          </v-card>
          <!-- </v-container> -->
        </v-flex>
      </v-layout>
    </div>
    <div class="text-center">
      <v-pagination
        v-model="pagination.page"
        @input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
      ></v-pagination>
    </div>
  </v-container>
</template>

<script>
import tagService from '@/services/tags.js'
import helpers from '@/functions/helpers.js'
import search from '@/services/search'

export default {
  name: 'tagsMain',
  data () {
    return {
      tags: [],
      links: [],
      pagination: {
        page: 1,
        length: 1,
        visible: 7
      }
    }
  },
  methods: {
    paginationChange () {
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
        })
        .catch(error => { console.log(error) })
    },
    followTag: function (tag) {
      // TODO: Verify no logged out user handling is necessary.
      tagService.followTag(tag.id)
        .then(
          tag.userFollowing = true
        )
      event.stopPropagation()
    },
    unfollowTag: function (tag) {
      // TODO: Verify no logged out user handling is necessary.
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
  border-radius: 20px;
  font-weight: 400;
  padding: 3px !important;
}

</style>
