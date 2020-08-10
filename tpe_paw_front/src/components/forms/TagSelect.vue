<template>
  <v-container>
    <v-multiselect
      v-model="selectedTag"
      :options="tags"
      @search-change="updateSelectionSet"
      :close-on-select="true"
      :cler-on-select="false"
      placeholder="Select A Tag"
      label="name"
      track-by="id"
    >
      <template slot="afterList" v-if="hasNext">
        <v-btn
          @click="loadMore">
          LOAD MORE
        </v-btn>
      </template>
    </v-multiselect>
    {{links}}
  </v-container>
</template>
<script>
import tags from '@/services/tags'
import Multiselect from 'vue-multiselect'
import { parseLinks } from '@/functions/helpers.js'
import axiosFetcher from '@/services/axiosFetcher.js'

export default {
  components: {
    'v-multiselect': Multiselect
  },
  props: ['value'],
  data () {
    return {
      tags: [],
      tagQuery: '',
      selectedTag: '',
      links: () => {}
    }
  },
  methods: {
    updateSelectionSet (query) {
      this.tagQuery = query
      tags.searchTags(1, query, true, false).then(r => {
        this.tags = r.data
        this.links = parseLinks(r.headers.link)
      })
    },
    loadMore () {
      axiosFetcher.get(this.links.next).then(r => {
        if (r.data.length > 0) {
          console.log(r.data)
          this.tags.push(...r.data)
          console.log(this.tags)
        }
        this.links = parseLinks(r.headers.link)
      })
    }
  },
  watch: {
    selectedTag: {
      handler: function (val, oldVal) {
        this.$emit('input', val.id)
      }
    }
  },
  mounted () {
    tags.searchTags(1, this.tagQuery, true, false).then(r => {
      this.tags = r.data
      this.links = parseLinks(r.headers.link)
    })
  },
  computed: {
    hasNext () {
      return this.links.next != null
    }
  }
}
</script>