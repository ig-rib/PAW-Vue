<template>
  <v-container class="registration-container">
    <v-layout py-10 class="registration-title-layout" align-center justify-center>
      <h1>
        WELCOME TO SNIPPIT
      </h1>
    </v-layout>
    <v-layout class="registration-data-layout" column>
      <v-flex>
        <v-text-field
          :label="$t('registration.username')"
          outlined
          v-model="username">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field
          :label="$t('registration.email')"
          outlined
          :rules="[rules.email]"
          v-model="email">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field
          type="password"
          :label="$t('registration.password')"
          outlined
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
          <v-btn @click="register">{{$t('registration.register')}}</v-btn>
        </v-layout>
      </v-flex>
    </v-layout>
    <v-card-text class="error-text" v-if="usesExistingEmail">{{ $t('validations.emailExists') }}</v-card-text>
    <v-card-text class="error-text" v-if="usesExistingUname">{{ $t('validations.userNameExists') }}</v-card-text>
    <v-layout class="registration-navigation-layout" column>
      <v-layout>
        <v-btn text @click="goToLogin">{{ $t('registration.hasAccount') }} {{ $t('registration.goToLogin') }}</v-btn>
      </v-layout>
      <v-layout>
        <v-btn text @click="goToPassRecovery">{{ $t('registration.forgotPassword') }} {{ $t('registration.goToPassRecovery') }}</v-btn>
      </v-layout>
    </v-layout>
  </v-container>
</template>

<script>
import validations from '@/functions/validations'
import registration from '@/services/registration'

export default {
  data () {
    return {
      username: '',
      email: '',
      password: '',
      repeatPassword: '',
      usesExistingUname: false,
      usesExistingEmail: false
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
          this.usesExistingUname = data.usernameExists
          this.usesExistingEmail = data.emailExists
        })
    },
    goToLogin () {
      this.$router.push({
        name: 'login'
      })
    },
    goToPassRecovery () {
      this.$router.push({
        name:'send-recovery-email'
      })
    }
  },
  computed: {
    rules () {
      return {
        email: () => validations.email(this.email),
        // nonUsedEmail: () => !this.usesExistingEmail || this.$t('validations.emailExists'),
        // nonUsedUname: () => !this.usesExistingUname || this.$t('validations.userNameExists'),
        equalsPassword: () => this.password === this.repeatPassword || this.$t('validations.nonMatchingPasswords'),
        password: () => validations.validPassword(this.password)
      }
    }
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
</style>
