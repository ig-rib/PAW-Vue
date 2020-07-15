<template>
  <v-container>
     <!-- Search bar -->
    <v-row align="start">
        <v-col>
            <!--TODO: Maybe change to component?-->
            <v-card height=55>
                <v-row fill-height>
                    <v-col>
                        <v-text-field
                        v-model="search.input"
                        v-on:keydown.enter="searchTags"
                        :label="$t('tags.search')"
                        dense
                        rounded
                        hide-details
                        >
                        </v-text-field>
                    </v-col>
                    <v-divider vertical></v-divider>
                    <v-col class="flex-grow-0">
                        <v-btn
                        class="pr-2"
                        height="100%"
                        icon
                        v-on:click="searchTags"
                        >
                            <v-icon>mdi-magnify</v-icon>
                        </v-btn>
                    </v-col>
                </v-row>
            </v-card>
        </v-col>
        <v-col class="flex-grow-0">
             <v-btn 
             rounded :outlined="!search.showEmpty" color="primary" small dark
             v-on:click="showEmptyChange"
             >
                 {{ $t('tags.showEmpty') }}
                 <v-icon v-if="search.showEmpty">
                     mdi-check
                 </v-icon>
             </v-btn>
             <v-btn 
             class="mt-1" rounded :outlined="!search.showOnlyFollowing" color="primary" small dark
             v-on:click="showOnlyFollowingChange"
             >
                 {{ $t('tags.showOnlyFollowing') }}
                 <v-icon v-if="search.showOnlyFollowing">
                     mdi-check
                 </v-icon>
             </v-btn>
        </v-col>
    </v-row>
    <div>
        <p> {{ $t('tags.title') }} </p>
        <v-row>
            <v-col v-for="tag in tags" :key="tag.id">
                <div>
                    <v-chip class="ma-2 tag-chip" label>
                      {{ tag.name }}
                      <!-- If tag is followed -->
                      <v-chip 
                        v-if="tag.userFollowing"
                        class="ma-1" color="primary"
                        v-on:click="unfollowTag(tag)"
                      >
                      {{ $t('tags.following') }}
                      </v-chip>
                      <!-- If tags is not folloed -->
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
        v-on:input="paginationChange"
        :length="pagination.length" 
        :total-visible="pagination.visible"
        circle
      ></v-pagination>
    </div>
  </v-container>
</template>

<script>
import tagService from '@//services/tags.js'
import helpers from '@//functions/helpers.js'

export default {
  name: 'tagsSearch',
  data () {
    return {
      tags: [],
      links: [],
      search: {
        input: '',
        showEmpty: true,
        showOnlyFollowing: false
      },
      pagination: {
          page: 1,
          length: 1,
          visible: 7
      }
    }
  },
  methods: {
      paginationChange: function () {
          tagService.searchTags(this.pagination.page, this.search.input, this.search.showEmpty, this.search.showOnlyFollowing)
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
            .catch()
      },
      unfollowTag: function (tag) {
          // TODO: Verify no logged out user handling is necessary.
          tagService.unfollowTag(tag.id)
            .then(
              tag.userFollowing = false
            )
            .catch()
      },
      searchTags: function () {
        tagService.searchTags(this.pagination.page, this.search.input, this.search.showEmpty, this.search.showOnlyFollowing)
          .then(values => {
            console.log(values.data)
            this.tags = values.data
            this.links = helpers.parseLinks(values.headers.link)
            this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10);
          })
          .catch(error => { console.log(error) })
      },
      showEmptyChange: function () {
        this.search.showEmpty = !this.search.showEmpty
        this.searchTags()
      },
      showOnlyFollowingChange: function () {
        this.search.showOnlyFollowing = !this.search.showOnlyFollowing
        this.searchTags()
      }

  },
  mounted () {
    tagService.getTags(this.pagination.page)
      .then(values => {
        this.tags = values.data
        this.links = helpers.parseLinks(values.headers.link)
        this.pagination.length = parseInt(this.links.last.match(/page=(.*)/)[1], 10);
      })
      .catch(error => { console.log(error) })
  }
}

</script>

<style lang="scss">

.tag-chip{
    width: 200px;
    height: 50px !important;
}

</style>
