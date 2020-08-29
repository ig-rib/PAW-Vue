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
    <v-layout class="registration-navigation-layout" column>
      <v-layout>
        <v-btn text @click="goToLogin">{{ $t('registration.knowYourPassword') }} {{ $t('registration.goToLogin') }}</v-btn>
      </v-layout>
      <v-layout>
        <v-btn text @click="goToRegister">{{ $t('registration.noAccount') }} {{ $t('registration.goToRegister') }}</v-btn>
      </v-layout>
    </v-layout>
  </v-container>
</template>

<script>
import registration from '@/services/registration.js'
import validations from '@/functions/validations.js'

export default {
  data () {
    return {
      email: ''
    }
  },
  methods: {
    sendRecoveryEmail () {
      registration.sendRecoveryEmail(this.email)
        // TODO send to success page
        // or move to success tab
        .then(r => /* Success */ r)
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
