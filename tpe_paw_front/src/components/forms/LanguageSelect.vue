<template>
  <v-container>
    <v-multiselect
      v-model="selectedLang"
      :options="languages"
      @search-change="updateSelectionSet"
      :close-on-select="true"
      :cler-on-select="false"
      :placeholder="$t('components.languageSelect.selectLanguage')"
      label="name"
      track-by="id"
    >
      <template slot="afterList" v-if="hasNext">
        <v-btn
          @click="loadMore">
          {{ $t('components.shared.pagination.loadMore')}}
        </v-btn>
      </template>
    </v-multiselect>
  </v-container>
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
  props: ['value'],
  data () {
    return {
      languages: [],
      langQuery: '',
      selectedLang: '',
      links: () => {}
    }
  },
  methods: {
    // TODO handle all request errors
    updateSelectionSet (query) {
      this.langQuery = query
      languages.searchLanguage(1, query, true).then(r => {
        this.languages = r.data
        this.links = parseLinks(r.headers.link)
      })
    },
    loadMore () {
      axiosFetcher.get(this.links.next).then(r => {
        if (r.data.length > 0) {
          this.languages.push(...r.data)
        }
        this.links = parseLinks(r.headers.link)
      })
    }
  },
  watch: {
    selectedLang: {
      handler: function (val, oldVal) {
        this.$emit('input', val.id)
      }
    }
  },
  mounted () {
    languages.searchLanguage(1, this.langQuery, true).then(r => {
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
