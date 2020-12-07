import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import i18n from './i18n'

import TextFieldDatePicker from './components/forms/TextFieldDatePicker.vue'
import TagSelect from './components/forms/TagSelect.vue'
import LanguageSelect from './components/forms/LanguageSelect.vue'
import Snippet from './components/Snippet.vue'
import SnippetGrid from './components/SnippetGrid.vue'

// import titleMixin from './functions/titleMixin.js'

// import VueHighlightJS from 'vue-highlightjs'

Vue.config.productionTip = false

Vue.component('tf-date-picker', TextFieldDatePicker)
Vue.component('tag-select', TagSelect)
Vue.component('language-select', LanguageSelect)
Vue.component('snippet', Snippet)
Vue.component('snippet-grid', SnippetGrid)

// Vue.mixin(titleMixin)

// Vue.use(VueHighlightJS)

new Vue({
  router,
  store,
  vuetify,
  i18n,
  render: h => h(App)
}).$mount('#app')
