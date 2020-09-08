<template>
  <v-container>
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
    <v-btn @click="resetPassword">{{ $t('registration.resetPassword') }}</v-btn>
  </v-container>
</template>

<script>
import registration from '@/services/registration'
import validations from '@/functions/validations'

export default {
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
          // Show password successfully reset...
          this.$router.push({
            name: 'feed'
          })
        })
        .catch(e => {
          // Show error
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
    }
  }
}
</script>
