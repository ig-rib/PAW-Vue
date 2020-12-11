<template>
  <v-container>
    <v-layout pt-10 align-center justify-center class="registration-snippit-logo">
      {{ $t('snippit') }}
    </v-layout>
    <v-layout py-10 class="registration-title-layout" align-center justify-center>
      {{ $t('registration.resetPassword') }}
    </v-layout>
    <v-text-field
      type="password"
      :label="$t('registration.password')"
      outlined
      :rules="[rules.password]"
      v-model="password">
    </v-text-field>
    <v-text-field
      type="password"
      :label="$t('registration.repeatPassword')"
      outlined
      :rules="[rules.equalsPassword]"
      v-model="repeatPassword">
    </v-text-field>
    <v-btn color="accent" :disabled="!allRulesAlright" @click="resetPassword">{{ $t('registration.resetPassword') }}</v-btn>
  </v-container>
</template>

<script>
import registration from '@/services/registration'
import validations from '@/functions/validations'

export default {
  title: 'Snippit - Reset Password',
  data () {
    return {
      id: '',
      token: '',
      password: '',
      repeatPassword: ''
    }
  },
  methods: {
    resetPassword () {
      console.log(this.repeatPassword)
      registration.resetPassword(this.id, this.password, this.repeatPassword, this.token)
        .then(r => {
          this.$store.dispatch('snackSuccess', this.$t('registration.successfullyResetPassword'))
          this.$router.push({name: 'login'})
        })
        .catch(e => {
          this.$store.dispatch('snackError', e.data)
        })
        .finally(() => {
          this.blankFields()
        })
    },
    blankFields () {
      this.id = this.token = this.password = this.repeatPassword = ''
    }
  },
  mounted () {
    this.id = this.$route.query.id
    this.token = this.$route.query.token
  },
  computed: {
    rules () {
      return {
        equalsPassword: () => this.password === this.repeatPassword || this.$t('validations.nonMatchingPasswords'),
        password: () => validations.validPassword(this.password)
      }
    },
    allRulesAlright () {
      return Object.keys(this.rules).filter(rule => this.rules[rule]() !== true).length === 0 && !this.usernameExists && !this.emailExists
    }
  }
}
</script>
