import Vue from 'vue'
import VueRouter from 'vue-router'
import PersistentNavigator from '@/components/PersistentNavigator.vue'
import FeedMain from '@/views/feed/FeedMain.vue'
import Nothing from '@/components/Nothing.vue'
import ExploreMain from '@/views/explore/ExploreMain.vue'
import LanguagesMain from '@/views/languages/LanguagesMain.vue'
import LanguageSnippets from '@/views/languages/LanguageSnippets.vue'
import LanguagesSuper from '@/views/languages/LanguagesSuper.vue'
import SnippetDetail from '@/views/snippet/SnippetDetail.vue'
import TagsMain from '@/views/tags/TagsMain.vue'
import TagSnippets from '@/views/tags/TagSnippets.vue'
import TagsSuper from '@/views/tags/TagsSuper.vue'
import Login from '@/views/registration/Login.vue'
import Register from '@/views/registration/Register.vue'
import RegistrationSuper from '@/views/registration/RegistrationSuper.vue'
import SendRecoveryEmail from '@/views/registration/SendRecoveryEmail.vue'
import ResetPassword from '@/views/registration/ResetPassword.vue'
import SendVerificationCode from '@/views/registration/SendVerificationCode.vue'
import UserProfileMain from '@/views/user/UserProfileMain.vue'
import CreateSnippet from '@/views/snippet/CreateSnippet.vue'
import SnippetGrid from '@/components/SnippetGrid.vue'
import Favorites from '@/views/user/Favorites.vue'
import Following from '@/views/user/Following.vue'
import Upvoted from '@/views/user/Upvoted.vue'
import Flagged from '@/views/admin/Flagged.vue'
import AddTags from '@/views/admin/AddTags.vue'
import AddLanguages from '@/views/admin/AddLanguages.vue'

import ErrorView from '@/views/error/Error.vue'

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
    path: '/:language',
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
                path: ':id',
                name: 'language-snippets',
                component: LanguageSnippets
              }
            ]
          },
          {
            path: 'snippet/create',
            name: 'create-snippet',
            component: CreateSnippet
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
                path: ':id',
                name: 'tag-snippets',
                component: TagSnippets
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
            redirect: {
              name: 'active-snippets'
            },
            component: UserProfileMain,
            children: [
              {
                path: 'active',
                name: 'active-snippets',
                component: SnippetGrid
              },
              {
                path: 'deleted',
                name: 'deleted-snippets',
                component: SnippetGrid
              }
            ]
          },
          {
            path: 'current-user/following',
            name: 'following',
            component: Following
          },
          {
            path: 'current-user/favorites',
            name: 'favorites',
            component: Favorites
          },
          {
            path: 'current-user/upvoted',
            name: 'upvoted',
            component: Upvoted
          },
          {
            path: 'current-user/flagged',
            name: 'flagged',
            component: Flagged
          },
          {
            path: 'admin/add-tags',
            name: 'add-tags',
            component: AddTags
          },
          {
            path: 'admin/add-languages',
            name: 'add-languages',
            component: AddLanguages
          },
          {
            path: 'error/:errNo',
            name: 'error',
            component: ErrorView
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
