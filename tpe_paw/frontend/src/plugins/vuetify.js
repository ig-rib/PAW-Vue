import Vue from 'vue'
import Vuetify from 'vuetify/lib'

Vue.use(Vuetify)

export default new Vuetify({
  theme: {
      themes: {
        light: {
          primary: '#1565c0',
          accent: '#2286c3',
          secondary: '#64b5f6'
        }
      }
    }
})
