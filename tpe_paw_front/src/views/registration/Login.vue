<template>
  <v-container>
    <v-card>
      <v-text-field
        :label="$t('registration.username')"
        outlined
        v-model="username">
      </v-text-field>
      <v-text-field
        type="password"
        :label="$t('registration.password')"
        outlined
        v-model="password">
      </v-text-field>
      <div v-if="invalid" class="error-text">
        {{ $t('registration.validations.usernameOrPasswordInvalid') }}
      </div>
      <v-btn @click="login">{{ $t('registration.login') }}</v-btn>
    </v-card>
  </v-container>
</template>

<script>
import registration from '@/services/registration.js'
import tags from '@/services/tags.js'

export default {
  data () {
    return {
      username: '',
      password: '',
      invalid: false
    }
  },
  methods: {
    login () {
      registration.login(this.username, this.password)
        .then(r => {
          this.invalid = false
          this.$store.dispatch('setToken', r.data)
          })
        .catch(e => {
          this.invalid = true
        })
        .finally(() => this.blankFields())
    },
    blankFields () {
      this.username = ''
      this.password = ''
    }
  },
  mounted () {
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
</style>
