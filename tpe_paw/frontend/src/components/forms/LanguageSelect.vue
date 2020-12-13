<template>
  <v-container pt-0 class="language-select-container">
    <v-multiselect
      class="languages-multiselect"
      v-model="selectedLangs"
      :options="languages"
      @search-change="updateSelectionSet"
      :close-on-select="closeOnSelect"
      :clear-on-select="false"
      :allowEmpty="allowEmpty"
      :placeholder="$t('components.languageSelect.selectLanguage')"
      label="name"
      track-by="id"
      :select-label="''"
      :multiple="multiple"
    >
      <template slot="afterList" v-if="hasNext">
        <v-layout justify-center py-2>
          <v-btn
            rounded
            outlined
            color="primary"
            @click="loadMore">
            {{ $t('components.shared.pagination.loadMore')}}
          </v-btn>
        </v-layout>
      </template>
    </v-multiselect>
    <div v-if="displayEmptyError" class="error-message">
          {{ $t('components.languageSelect.notNull') }}
    </div>
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
  props: {
    value: [Object, Array],
    multiple: {
      type: Boolean,
      default: false
    },
    closeOnSelect: {
      type: Boolean,
      default: true
    },
    allowEmpty: {
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
    updateSelectionSet (query) {
      this.langQuery = query
      languages.searchLanguages({ q: query }, true)
      .then(r => {
        this.languages = r.data
        this.links = parseLinks(r.headers.link)
      })
      .catch(e => this.$store.dispatch('snackError', this.$t('components.languageSelect.errorUpdate')))

    },
    loadMore () {
      axiosFetcher.get(this.links.next).then(r => {
        if (r.data.length > 0) {
          this.languages.push(...r.data)
        }
        this.links = parseLinks(r.headers.link)
      })
      .catch(e => this.$store.dispatch('snackError', this.$t('components.languageSelect.errorLoadMore')))
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
    },
    displayEmptyError () {
      return !this.allowEmpty && (this.selectedLangs == null || this.selectedLangs.length === 0)
    }
  }
}
</script>

<style lang="scss">
.language-select-container {
  .multiselect__tags {
    max-height: 100px !important;
    overflow-y: hidden;
  }
  .multiselect__content-wrapper {
    min-width: 200px !important;
  }
}
.error-message{
  margin-top: 3px;
  line-height: 12px;
  font-size: 12px;
  font-weight: 400;
  word-break: break-word;
  overflow-wrap: break-word;
  word-wrap: break-word;
  -webkit-hyphens: auto;
  -ms-hyphens: auto;
  hyphens: auto;
  color: #ff5252 !important;
  caret-color: #ff5252 !important;
}
</style>
