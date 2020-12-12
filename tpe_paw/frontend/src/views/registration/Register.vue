<template>
  <v-container class="registration-container">
    <v-layout pt-10 align-center justify-center class="registration-snippit-logo">
      {{ $t('snippit') }}
    </v-layout>
    <v-layout py-10 class="registration-title-layout" align-center justify-center>
      {{ $t('registration.createAccount') }}
    </v-layout>
    <v-layout class="registration-data-layout" column>
      <v-flex>
        <v-text-field
          :label="$t('registration.username')"
          outlined
          @input="checkUsernameExists"
          :rules="[rules.usernameLength,
              rules.usernamePattern]"
          :error-messages="usernameErrorMessages"
          ref="usernameTextField"
          v-model="username">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field
          :label="$t('registration.email')"
          outlined
          @input="checkEmailExists"
          :rules="[this.rules.email]"
          :error-messages="emailErrorMessages"
          v-model="email">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field
          type="password"
          :label="$t('registration.password')"
          outlined
          ref="emailTextField"
          :rules="[rules.password]"
          v-model="password">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field
          type="password"
          :label="$t('registration.repeatPassword')"
          outlined
          :rules="[rules.equalsPassword]"
          v-model="repeatPassword">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-layout justify-center>
          <v-btn color="accent" :disabled="!allRulesAlright" @click="register">{{$t('registration.register')}}</v-btn>
        </v-layout>
      </v-flex>
    </v-layout>
    <v-layout mt-8 align-center class="registration-navigation-layout" column>
        <v-flex>
          {{ $t('registration.hasAccount') }}
          <a @click="goToLogin">{{ $t('registration.goToLogin') }}</a>
        </v-flex>
        <v-flex>
          {{ $t('registration.forgotPassword') }}
          <a @click="goToPassRecovery">{{ $t('registration.goToPassRecovery') }}</a>
        </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import validations from '@/functions/validations'
import registration from '@/services/registration'

export default {
    title (){  return this.$t('titles.register')},
  data () {
    return {
      username: '',
      email: '',
      password: '',
      repeatPassword: '',
      usernameExists: false,
      emailExists: false
    }
  },
  methods: {
    register () {
      registration.register(this.username, this.email, this.password)
        .then(r => this.$router.push({
          name: 'login'
        }))
        .catch(e => {
          const data = e.response.data
          this.usernameExists = data.usernameExists
          this.emailExists = data.emailExists
        })
    },
    goToLogin () {
      this.$router.push({
        name: 'login'
      })
    },
    goToPassRecovery () {
      this.$router.push({
        name: 'send-recovery-email'
      })
    },
    checkUsernameExists (newUsername) {
      registration.usernameExists(newUsername)
        .then(r => {
          this.usernameExists = r.data.exists
        })
    },
    checkEmailExists (newEmail) {
      registration.emailExists(newEmail)
        .then(r => {
          this.emailExists = r.data.exists
        })
    }
  },
  computed: {
    rules () {
      return {
        email: () => validations.email(this.email),
        usernameLength: () => validations.lengthBetween(this.username, 6, 50),
        usernamePattern: () => validations.usernamePattern(this.username), 
        equalsPassword: () => this.password === this.repeatPassword || this.$t('validations.nonMatchingPasswords'),
        password: () => validations.validPassword(this.password),
        nonexistingUsername: () => !this.usernameExists || this.$t('validations.userNameExists'),
        nonexistingEmail: () => !this.emailExists || this.$t('validations.emailExists')
      }
    },
    // TODO check if worth it to create external function
    allRulesAlright () {
      return Object.keys(this.rules).filter(rule => this.rules[rule]() !== true).length === 0 && !this.usernameExists && !this.emailExists
    },
    usernameErrorMessages () {
      return [this.rules.nonexistingUsername]
              .filter(rule => rule() !== true)
    },
    emailErrorMessages () {
      return [this.rules.nonexistingEmail]
              .filter(rule => rule() !== true)
    }
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
</style>
