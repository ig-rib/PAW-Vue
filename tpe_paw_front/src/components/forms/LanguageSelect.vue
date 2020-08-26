<template>
  <div>
    <v-multiselect
      v-model="selectedLangs"
      :options="languages"
      @search-change="updateSelectionSet"
      :close-on-select="closeOnSelect"
      :cler-on-select="false"
      :placeholder="$t('components.languageSelect.selectLanguage')"
      label="name"
      track-by="id"
      :multiple="multiple"
    >
      <template slot="afterList" v-if="hasNext">
        <v-btn
          @click="loadMore">
          {{ $t('components.shared.pagination.loadMore')}}
        </v-btn>
      </template>
    </v-multiselect>
  </div>
</template>
<script>
import languages from '@/services/languages'
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
      languages: [],
      langQuery: '',
      selectedLangs: null,
      links: () => {}
    }
  },
  methods: {
    // TODO handle all request errors
    updateSelectionSet (query) {
      this.langQuery = query
      languages.searchLanguages({ q: query }, true).then(r => {
        this.languages = r.data
        this.links = parseLinks(r.headers.link)
      })
    },
    loadMore () {
      console.log(this.links.next)
      axiosFetcher.get(this.links.next).then(r => {
        if (r.data.length > 0) {
          this.languages.push(...r.data)
        }
        this.links = parseLinks(r.headers.link)
      })
    }
  },
  watch: {
    selectedLangs: {
      handler: function (val, oldVal) {
        this.$emit('input', val)
      }
    }
  },
  mounted () {
    languages.searchLanguages({ q: this.langQuery }, true).then(r => {
      this.languages = r.data
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
