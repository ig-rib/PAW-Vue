<template>
  <v-container>
    <div>
      <p> {{ $t('tags.title') }} </p>
      <v-row>
        <v-col v-for="tag in tags" :key="tag.id">
          <div>
            <v-chip @click="goToTagSnippets(tag.id)" class="ma-2 tag-chip" label>
              {{ tag.name }}
              <!-- If tag is followed -->
              <v-chip 
                v-if="tag.userFollowing"
                class="ma-1" color="primary"
                v-on:click="unfollowTag(tag)"
              >
              {{ $t('tags.following') }}
              </v-chip>
              <!-- If tags is not followed -->
              <v-chip 
                v-else
                class="ma-1" color="indigo darken-3" outlined
                v-on:click="followTag(tag)"
              >
              {{ $t('tags.follow') }}
              </v-chip>
            </v-chip>
          </div>
        </v-col>
      </v-row>
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

.tag-chip{
    width: 200px;
    height: 50px !important;
}

</style>
