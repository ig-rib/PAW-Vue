import Vue from 'vue'
import VueRouter from 'vue-router'
import PersistentNavigator from '@/components/PersistentNavigator.vue'
import FeedMain from '@/views/feed/FeedMain.vue'
import Nothing from '@/components/Nothing.vue'
import ExploreMain from '@/views/explore/ExploreMain.vue'
import LanguagesMain from '@/views/languages/LanguagesMain.vue'
import LanguagesSnippet from '@/views/languages/LanguagesSnippet.vue'
import SnippetDetail from '@/views/snippet/SnippetDetail.vue'
import TagsMain from '@/views/tags/TagsMain.vue'

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
    component: Nothing,
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
            component: FeedMain
          },
          {
            path: 'explore',
            name: 'explore',
            component: ExploreMain
          },
          {
            path: 'languages',
            name: 'languages',
            component: LanguagesMain,
          },
          {
            path: 'languages/:id',
            name: 'languages-snippet',
            component: LanguagesSnippet
          },
          {
            path: 'snippet/:id',
            name: 'snippet-detail',
            component: SnippetDetail
          },
          {
            path: 'tags',
            name: 'tags',
            component: TagsMain
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
