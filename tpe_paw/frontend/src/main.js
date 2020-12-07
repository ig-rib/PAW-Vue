import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import i18n from './i18n'
import VueProgressBar from 'vue-progressbar'

import TextFieldDatePicker from './components/forms/TextFieldDatePicker.vue'
import TagSelect from './components/forms/TagSelect.vue'
import LanguageSelect from './components/forms/LanguageSelect.vue'
import Snippet from './components/Snippet.vue'
import SnippetGrid from './components/SnippetGrid.vue'

import helpers from './functions/helpers.js'

// import VueHighlightJS from 'vue-highlightjs'

Vue.config.productionTip = false

Vue.component('tf-date-picker', TextFieldDatePicker)
Vue.component('tag-select', TagSelect)
Vue.component('language-select', LanguageSelect)
Vue.component('snippet', Snippet)
Vue.component('snippet-grid', SnippetGrid)

Vue.mixin(helpers)

Vue.use(VueProgressBar, {
  color: 'rgb(143, 255, 199)',
  failedColor: 'red',
  thickness: '4px',
  transition: {
    speed: '0.1s',
    opacity: '0.6s',
    termination: 300
  },
})

// Vue.use(VueHighlightJS)

new Vue({
  router,
  store,
  vuetify,
  i18n,
  render: h => h(App)
}).$mount('#app')
