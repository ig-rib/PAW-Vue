<template>
  <v-container>
    <v-layout pt-10 align-center justify-center class="registration-snippit-logo">
      {{ $t('snippit') }}
    </v-layout>
    <v-layout py-10 class="registration-title-layout" align-center justify-center>
      {{ $t('registration.resetPassword') }}
    </v-layout>
    <v-layout class="registration-data-layout" column>
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
    </v-layout>
    <v-layout mt-5 justify-center>
      <v-flex shrink>
        <v-btn rounded outlined color="primary" :disabled="!allRulesAlright" @click="resetPassword">{{ $t('registration.resetPassword') }}</v-btn>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import registration from '@/services/registration'
import validations from '@/functions/validations'

export default {
    title () { return this.$t('titles.resetPassword') },
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
      registration.resetPassword(this.id, this.password, this.repeatPassword, this.token)
        .then(r => {
          this.$store.dispatch('snackSuccess', this.$t('registration.successfullyResetPassword'))
          this.$router.push({ name: 'login' })
        })
        .catch(e => this.$store.dispatch('snackError', this.$t('registration.errorResetPassword')))
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
