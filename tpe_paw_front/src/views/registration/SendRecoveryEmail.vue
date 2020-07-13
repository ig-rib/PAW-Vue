<template>
  <v-container>
    <v-text-field
      :label="$t('registration.email')"
      outlined
      :rules="[rules.email]"
      v-model="email">
    </v-text-field>
    <v-btn :disabled="validEmail" @click="sendRecoveryEmail">
      {{ $t('registration.sendRecoveryEmail') }}
      </v-btn>
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
