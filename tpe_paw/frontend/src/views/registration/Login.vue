<template>
  <v-container id="login-container">
    <v-layout pt-10 align-center justify-center class="registration-snippit-logo">
      {{ $t('snippit') }}
    </v-layout>
    <v-layout py-10 class="registration-title-layout" align-center justify-center>
      {{ $t('registration.welcome') }}
    </v-layout>
    <v-layout class="registration-data-layout" column>
      <v-flex>
        <v-text-field
          @keypress.enter="login"
          :label="$t('registration.username')"
          outlined
          v-model="username">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field
          @keypress.enter="login"
          type="password"
          :label="$t('registration.password')"
          outlined
          v-model="password">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-layout justify-center>
          <div v-if="invalid" class="error-text">
            {{ $t('validations.usernameOrPasswordInvalid') }}
          </div>
        </v-layout>
        <v-layout mt-5 justify-center>
          <v-flex shrink>
            <v-btn @click="login">{{ $t('registration.login') }}</v-btn>
          </v-flex>
        </v-layout>
      </v-flex>
    </v-layout>
    <v-layout mt-8 shrink justify-center align-center class="registration-navigation-layout" column>
      <v-flex shrink>
          {{ $t('registration.noAccount') }}
          <a @click="goToRegister">{{ $t('registration.goToRegister') }}</a>
      </v-flex>
      <v-flex shrink>
          {{ $t('registration.forgotPassword') }}
          <a @click="goToPassRecovery">{{ $t('registration.goToPassRecovery') }}</a>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import registration from '@/services/registration.js'
import user from '@/services/user.js'

export default {
  title: 'Snippit - Login',
  data () {
    return {
      username: '',
      password: '',
      invalid: false,
      prevRoute: null
    }
  },
  methods: {
    login () {
      this.$Progress.start()
      registration.login(this.username, this.password)
        .then(r => {
          this.invalid = false
          this.$store.dispatch('setToken', r.data)
          window.localStorage.setItem('token', JSON.stringify(r.data))
          // Go to feed if user doesn't have a relevant navigation
          // history in this website
          if (this.prevRoute == null || this.prevRoute.name === 'register') {
            this.$router.push({
              name: 'feed'
            })
          } else {
            this.$router.push({
              name: this.prevRoute.name,
              query: this.prevRoute.query
            })
          }
          user.getLoggedInUser().then(r => {
            this.$store.dispatch('setUser', r.data)
          })
          })
        .catch(e => {
          this.invalid = true
          this.$Progress.fail()
        })
        .finally(() => {
          this.blankFields()
          this.$Progress.finish()
        })
    },
    blankFields () {
      this.username = ''
      this.password = ''
    },
    goToRegister () {
      this.$router.push({
        name: 'register'
      })
    },
    goToPassRecovery () {
      this.$router.push({
        name: 'send-recovery-email'
      })
    }
  },
  beforeRouteEnter (to, from, next) {
    next(vm => {
      vm.prevRoute = from
    })
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
  @import '@/styles/registration.scss';

  #login-container {
    height: 100%;
    padding: 2% 5% 2% 5%;
    display: flex;
    flex-direction: column;    
  }
</style>
