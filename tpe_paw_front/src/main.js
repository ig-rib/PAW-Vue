import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import i18n from './i18n'

import TextFieldDatePicker from './components/forms/TextFieldDatePicker.vue'
import TagSelect from './components/forms/TagSelect.vue'

Vue.config.productionTip = false

Vue.component('tf-date-picker', TextFieldDatePicker)
Vue.component('tag-select', TagSelect)

new Vue({
  router,
  store,
  vuetify,
  i18n,
  render: h => h(App)
}).$mount('#app')
