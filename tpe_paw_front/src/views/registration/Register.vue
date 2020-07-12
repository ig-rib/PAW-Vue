<template>
  <v-container>
    <v-card>
      <v-text-field
        :label="$t('registration.username')"
        outlined
        v-model="username">
      </v-text-field>
      <v-text-field
        :label="$t('registration.email')"
        outlined
        :rules="[rules.email]"
        v-model="email">
      </v-text-field>
      <v-text-field
        :label="$t('registration.password')"
        outlined
        :rules="[rules.password]"
        v-model="password">
      </v-text-field>
      <v-text-field
        :label="$t('registration.repeatPassword')"
        outlined
        :rules="[rules.equalsPassword]"
        v-model="repeatPassword">
      </v-text-field>
      <v-btn @click="register">{{$t('registration.register')}}</v-btn>
      <v-card-text class="error-text" v-if="usesExistingEmail">{{ $t('validations.emailExists') }}</v-card-text>
      <v-card-text class="error-text" v-if="usesExistingUname">{{ $t('validations.userNameExists') }}</v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import validations from '@/functions/validations'
import registration from '@/services/registration'

export default {
  data () {
    return {
      username: '',
      email: '',
      password: '',
      repeatPassword: '',
      usesExistingUname: false,
      usesExistingEmail: false
    }
  },
  methods: {
    register () {
      registration.register(this.username, this.email, this.password)
        .then(r => this.$router.push({
          name: 'login'
        }))
        .catch(e => {
          const data = e.response.data
          this.usesExistingUname = data.usernameExists
          this.usesExistingEmail = data.emailExists
        })
    }
  },
  computed: {
    rules () {
      return {
        email: () => validations.email(this.email),
        // nonUsedEmail: () => !this.usesExistingEmail || this.$t('validations.emailExists'),
        // nonUsedUname: () => !this.usesExistingUname || this.$t('validations.userNameExists'),
        equalsPassword: () => this.password === this.repeatPassword || this.$t('validations.nonMatchingPasswords'),
        password: () => validations.validPassword(this.password)
      }
    }
  }
}
</script>

<style lang="scss">
  @import '@/styles/main.scss';
</style>
