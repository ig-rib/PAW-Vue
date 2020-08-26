<template>
  <div>
    <v-text-field
      :label="$t('registration.verificationCode')"
      outlined
      limit="6"
      v-model="code">
    </v-text-field>
    <v-btn @click="sendVerificationCode">
      {{ $t('registration.sendVerificationCode') }}
    </v-btn>
  </div>
</template>

<script>
import registration from '@/services/registration.js'
import validations from '@/functions/validations.js'

export default {
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
        .then(r => /* Success */ r)
        .catch(e => e)
        .finally(() => {
          this.code = ''
        })
    }
  }
}
</script>
