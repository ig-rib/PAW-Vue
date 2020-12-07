<template>
  <v-container class="registration-container">
    <v-layout pt-10 align-center justify-center class="registration-snippit-logo">
      {{ $t('snippit') }}
    </v-layout>
    <v-layout py-10 class="registration-title-layout" align-center justify-center>
      {{ $t('registration.verifyAccount') }}
    </v-layout>
    <v-layout column pb-2 class="registration-subtitle-layout" align-center>
      {{ $t('registration.enterCode') }}
    </v-layout>
    <v-layout column class="registration-data-layout">
      <v-flex>
        <v-text-field
          :label="$t('registration.verificationCode')"
          outlined
          limit="6"
          :rules="[rules.codePattern]"
          return-masked-value
          v-model="code">
        </v-text-field>
      </v-flex>
      <v-flex>
        <v-layout justify-center>
          <v-btn
            :disabled="!allRulesAlright" 
            @click="sendVerificationCode">
            {{ $t('registration.sendVerificationCode') }}
          </v-btn>
        </v-layout>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import registration from '@/services/registration.js'
import validations from '@/functions/validations.js'

export default {
  title: 'Snippit - Code',
  data () {
    return {
      code: ''
    }
  },
  methods: {
    sendVerificationCode () {
      registration.sendVerificationCode(this.code)
        // TODO send to success page
        // or move to success tab
        .then(r => {
          this.$store.dispatch('snackSuccess', this.$t('registration.successfulVerification'))
          // TODO do not allow going back to login
          this.$router.push({
            name: 'feed'
          })
        })
        .catch(e => e)
        .finally(() => {
          this.code = ''
        })
    }
  },
  computed: {
    rules () {
      return {
        codePattern: () => validations.codePattern(this.code)
      }
    },
    allRulesAlright () {
      return Object.keys(this.rules).filter(rule => this.rules[rule]() !== true).length === 0 && !this.usernameExists && !this.emailExists
    }
  }
}
</script>
