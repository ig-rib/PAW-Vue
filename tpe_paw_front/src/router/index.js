import Vue from 'vue'
import VueRouter from 'vue-router'
import PersistentNavigator from '@/components/PersistentNavigator.vue'
import Home from '@/views/Home.vue'
import Nothing from '@/components/Nothing.vue'

Vue.use(VueRouter)

const defaultLang = 'en'

const routes = [
  {
    path: '/',
    redirect: '/' + defaultLang + '/feed'
  },
  {
    path: '/:langauge',
    name: 'nothing',
    redirect: {
      name: 'feed'
    },
    comopnent: Nothing,
    children: [
      {
        path: '',
        redirect: {
          name: 'feed'
        },
        component: PersistentNavigator,
        children: [
          {
            path: 'feed',
            name: 'feed',
            component: Home
          }
        ]
      }
    ]
  }
]

const router = new VueRouter({
  routes
})

export default router
