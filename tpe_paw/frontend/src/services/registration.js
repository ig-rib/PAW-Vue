import urls from './urls'
import axiosFetcher from './axiosFetcher'
import i18n from '@/i18n.js'
import Store from '@/store'

const login = (username, password) => axiosFetcher.post(urls.registration.login, {}, {
  username,
  password
})

const register = (username, email, password) => axiosFetcher.post(urls.registration.register, {}, {
  username,
  email,
  password
})

const sendRecoveryEmail = (email) => axiosFetcher.put(urls.registration.sendRecoveryEmail, {}, {
  email
})

const resetPassword = (id, password, repeatPassword, token) => axiosFetcher.put(urls.registration.resetPassword, {
  queryParams: {
    id
  }
}, {
  password,
  token,
  repeatPassword
})

const verifyEmail = () => axiosFetcher.put(urls.registration.verifyEmail)

const sendVerificationCode = (code) => axiosFetcher.post(urls.registration.verifyEmail, {}, {
  code
})

const usernameExists = (username) => axiosFetcher.get(urls.registration.usernameExists, {
  queryParams: {
    uname: username
  }
})

const emailExists = (email) => axiosFetcher.get(urls.registration.emailExists, {
  queryParams: {
    email
  }
})

const sendVerificationEmail = function sVE () {
  verifyEmail()
    .then(r => {
      Store.dispatch('snackSuccess', i18n.t('user.profile.verifyAccount.emailSent'))
    })
    .catch(e => {
      Store.dispatch('snackError', {
        message: i18n.t('user.profile.verifyAccount.emailError'),
        action: {
          func: () => {
            sVE()
            Store.dispatch('hideSnackbarNoAction')
          },
          text: i18n.t('error.grid.tryAgain')
        }
      })
    })
}

export default { 
  login,
  register,
  sendRecoveryEmail,
  resetPassword,
  verifyEmail,
  sendVerificationCode,
  sendVerificationEmail,
  usernameExists,
  emailExists
}
