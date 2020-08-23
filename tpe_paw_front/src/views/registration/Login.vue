<template>
  <v-container>
    <v-text-field
      :label="$t('registration.username')"
      outlined
      v-model="username">
    </v-text-field>
    <v-text-field
      type="password"
      :label="$t('registration.password')"
      outlined
      v-model="password">
    </v-text-field>
    <div v-if="invalid" class="error-text">
      {{ $t('registration.validations.usernameOrPasswordInvalid') }}
    </div>
    <v-btn @click="login">{{ $t('registration.login') }}</v-btn>
    <v-layout>
      <v-btn text @click="goToRegister">{{ $t('registration.noAccount') }} {{ $t('registration.goToRegister') }}</v-btn>
    </v-layout>
    <v-layout>
      <v-btn text @click="goToPassRecovery">{{ $t('registration.forgotPassword') }} {{ $t('registration.goToPassRecovery') }}</v-btn>
    </v-layout>
  </v-container>
</template>

<script>
import registration from '@/services/registration.js'
import user from '@/services/user.js'

export default {
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
      registration.login(this.username, this.password)
        .then(r => {
          this.invalid = false
          this.$store.dispatch('setToken', r.data)
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
          })
        .catch(e => {
          this.invalid = true
        })
        .finally(() => this.blankFields())
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
        name:'send-recovery-email'
      })
    }
  },
  beforeRouteEnter (to, from, next) {
    next(vm => {
      console.log('beforeRouteEnter: from', from)
      vm.prevRoute = from
    })
  },
  mounted () {
    console.log(this.prevRoute)
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
</style>
