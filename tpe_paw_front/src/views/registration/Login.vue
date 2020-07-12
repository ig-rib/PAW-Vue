<template>
  <v-container>
    <v-card>
      <v-text-field
        :label="$t('registration.login.username')"
        outlined
        v-model="username">
      </v-text-field>
      <v-text-field
        :label="$t('registration.login.password')"
        outlined
        v-model="password">
      </v-text-field>
      <div v-if="invalid" class="error-text">
        {{ $t('registration.login.usernameOrPasswordInvalid') }}
      </div>
      <v-btn @click="login">{{ $t('registration.login.login') }}</v-btn>
      <v-btn @click="test">TEST ENDPOINT</v-btn>
      {{ $store.getters.token }}
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
    },
    test () {
      tags.followTag(450)
        .then(r => console.log(r))
    }
  },
  mounted () {
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
</style>
