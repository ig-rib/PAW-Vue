<template>
  <v-container>
    <div>
        <p> {{ $t('tags.title') }} </p>
        <v-row>
            <v-col v-for="tag in tags" :key="tag.id">
                <div>
                    <v-chip class="ma-2 tag-chip" label>
                      {{ tag.name }}
                      <v-chip
                        class="ma-1"
                        color="indigo darken-3"
                        outlined
                        v-on:click="followTag(tag.id)"
                      >
                        <v-icon left>mdi-check-circle-outline</v-icon>
                        Follow
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
  name: 'tagsMain',
  data () {
    return {
      tags: null,
      links: 0,
      pagination: {
          page: 1,
          length: 1,
          visible: 7
      }
    }
  },
  methods: {
      paginationChange: function () {
          tagService.getTags(this.pagination.page)
            .then(values => {
                this.tags = values.data 
                this.links = helpers.parseLinks(values.headers.link)
            })
            .catch(error => { console.log(error) })
      },
      followTag: function (id) {
          //TODO: Check if user logged in
          tagService.followTag(id)
      }
  },
  computed: {
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
