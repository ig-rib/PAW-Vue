<template>
  <v-container class="registration-container">
    <template v-if="!sent">
      <v-layout pt-10 align-center justify-center class="registration-snippit-logo">
        {{ $t('snippit') }}
      </v-layout>
      <v-layout py-10 class="registration-title-layout" align-center justify-center>
        {{ $t('registration.recoverPassword') }}
      </v-layout>
      <v-layout class="registration-data-layout" column>
        <v-flex>
          <v-text-field
            :label="$t('registration.email')"
            outlined
            :rules="[rules.email]"
            v-model="email">
          </v-text-field>
        </v-flex>
        <v-flex>
          <v-layout justify-center>
            <v-btn :disabled="validEmail" @click="sendRecoveryEmail">
              {{ $t('registration.sendRecoveryEmail') }}
            </v-btn>
          </v-layout>
        </v-flex>
      </v-layout>
      <v-layout mt-8 justify-center align-center class="registration-navigation-layout" column>
        <v-flex>
          {{ $t('registration.knowYourPassword') }}
          <a text @click="goToLogin">{{ $t('registration.goToLogin') }}</a>
        </v-flex>
        <v-flex>
          {{ $t('registration.noAccount') }}
          <a text @click="goToRegister">{{ $t('registration.goToRegister') }}</a>
        </v-flex>
      </v-layout>
    </template>
    <template v-if="sent">
      <v-layout pt-10 align-center justify-center class="registration-snippit-logo">
        {{ $t('snippit') }}
      </v-layout>
      <v-layout pt-10 class="registration-title-layout" align-center>
        {{ $t('registration.recoveryEmailSent') }}
      </v-layout>
      <v-layout py-5>
        {{ $t('registration.mailSentInfo') }}
      </v-layout>
    </template>
  </v-container>
</template>

<script>
import registration from '@/services/registration.js'
import validations from '@/functions/validations.js'

export default {
  title: 'Snippit - Email',
  data () {
    return {
      email: '',
      sent: false
    }
  },
  methods: {
    sendRecoveryEmail () {
      registration.sendRecoveryEmail(this.email)
        // TODO send to success page
        // or move to success tab
        .then(r => {
          this.sent = true
        })
        .catch(e => e)
        .finally(() => {
          this.email = ''
        })
    },
    goToLogin () {
      this.$router.push({
        name: 'login'
      })
    },
    goToRegister () {
      this.$router.push({
        name: 'register'
      })
    }
},
  computed: {
    rules () {
      return {
        email: () => validations.email(this.email)
      }
    },
    validEmail () {
      return validations.email(this.email) !== true
    }
  }
}
</script>
