<template>
  <v-container class="tag-select-container">
    <v-multiselect
      v-model="selectedTags"
      :options="tags"
      @search-change="updateSelectionSet"
      :close-on-select="closeOnSelect"
      :clear-on-select="false"
      :placeholder="$t('components.tagSelect.selectTag')"
      label="name"
      :select-label="false"
      track-by="id"
      :multiple="multiple"
    >
      <template slot="afterList" v-if="hasNext">
        <v-layout justify-center py-2>
          <v-btn
            @click="loadMore">
            {{ $t('components.shared.pagination.loadMore')}}
          </v-btn>
        </v-layout>
      </template>
    </v-multiselect>
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
  props: {
    value: [Object, Array],
    multiple: {
      type: Boolean,
      default: false
    },
    closeOnSelect: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      tags: [],
      tagQuery: '',
      selectedTags: null,
      links: () => {}
    }
  },
  methods: {
    // TODO handle all request errors
    updateSelectionSet (query) {
      this.tagQuery = query
      tags.searchTags({ q: query }, true, false).then(r => {
        this.tags = r.data
        this.links = parseLinks(r.headers.link)
      })
    },
    loadMore () {
      axiosFetcher.get(this.links.next).then(r => {
        if (r.data.length > 0) {
          this.tags.push(...r.data)
        }
        this.links = parseLinks(r.headers.link)
      })
    }
  },
  watch: {
    selectedTags: {
      handler: function (val, oldVal) {
        this.$emit('input', val)
      }
    }
  },
  mounted () {
    tags.searchTags({ q: this.tagQuery }, true, false).then(r => {
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

<style lang="scss">
.tag-select-container {
  .multiselect__tags {
    max-height: 100px;
    overflow-y: hidden;
  }
  .multiselect__content-wrapper {
    min-width: 200px !important;
  }
}
</style>