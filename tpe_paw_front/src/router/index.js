import Vue from 'vue'
import VueRouter from 'vue-router'
import PersistentNavigator from '@/components/PersistentNavigator.vue'
import FeedMain from '@/views/feed/FeedMain.vue'
import Nothing from '@/components/Nothing.vue'
import ExploreMain from '@/views/explore/ExploreMain.vue'
import LanguagesMain from '@/views/languages/LanguagesMain.vue'
import LanguagesSnippet from '@/views/languages/LanguagesSnippet.vue'
import LanguagesSuper from '@/views/languages/LanguagesSuper.vue'
import LanguagesSearch from '@/views/languages/LanguagesSearch.vue'
import SnippetDetail from '@/views/snippet/SnippetDetail.vue'
import TagsMain from '@/views/tags/TagsMain.vue'
import TagsSearch from '@/views/tags/TagsSearch.vue'
import TagsSnippet from '@/views/tags/TagsSnippet.vue'
import TagsSuper from '@/views/tags/TagsSuper.vue'
import Login from '@/views/registration/Login.vue'
import Register from '@/views/registration/Register.vue'
import RegistrationSuper from '@/views/registration/RegistrationSuper.vue'
import SendRecoveryEmail from '@/views/registration/SendRecoveryEmail.vue'
import ResetPassword from '@/views/registration/ResetPassword.vue'
import SendVerificationCode from '@/views/registration/SendVerificationCode.vue'
import UserProfileMain from '@/views/user/UserProfileMain.vue'

Vue.use(VueRouter)

const defaultLang = 'en'

const routes = [
  {
    path: '/reset-password',
    name: 'redirect-reset-password',
    redirect: '/' + defaultLang + '/registration/reset-password'
  },
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
            component: LanguagesSuper,
            children: [
              {
                path: '',
                name: 'languages-main',
                component: LanguagesMain
              },
              {
                path: 'search',
                name: 'languages-search',
                component: LanguagesSearch
              },
              {
                path: ':id',
                name: 'languages-snippet',
                component: LanguagesSnippet
              }
              
            ]
          },
          {
            path: 'snippet/:id',
            name: 'snippet-detail',
            component: SnippetDetail
          },
          {
            path: 'tags',
            name: 'tags',
            component: TagsSuper,
            children: [
              {
                path: '',
                name: 'tags-main',
                component: TagsMain
              },
              {
                path: 'search',
                name: 'tags-search',
                component: TagsSearch
              },
              {
                path: ':id',
                name: 'tags-snippet',
                component: TagsSnippet
              }
            ]
          },
          {
            path: 'registration',
            name: 'registration',
            component: RegistrationSuper,
            children: [
              {
                path: 'login',
                name: 'login',
                component: Login
              },
              {
                path: 'register',
                name: 'register',
                component: Register
              },
              {
                path: 'send-recovery-email',
                name: 'send-recovery-email',
                component: SendRecoveryEmail
              },
              {
                path: 'reset-password',
                name: 'reset-password',
                component: ResetPassword
              },
              {
                path: 'send-verification-code',
                name: 'send-verification-code',
                component: SendVerificationCode
              }
            ]
          },
          {
            path: 'user/:id',
            name: 'user-profile',
            component: UserProfileMain
          }
        ]
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})
export default router
