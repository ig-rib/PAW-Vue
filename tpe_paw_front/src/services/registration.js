import urls from './urls'
import axiosFetcher from './axiosFetcher'

const login = (username, password) => axiosFetcher.post(urls.registration.login, {}, {
  username,
  password
})

const register = (username, email, password) => axiosFetcher.post(urls.registration.register, {}, {
  username,
  email,
  password
})

const sendRecoveryEmail = (email) => {
  axiosFetcher.put(urls.registration.sendRecoveryEmail, {}, {
  email
})
}

const resetPassword = (id, password, repeatPassword, token) => axiosFetcher.put(urls.registration.resetPassword, {
  queryParams: {
    id
  }
}, {
  password,
  token,
  repeatPassword
})

const verifyEmail = () => axiosFetcher.get(urls.registration.verifyEmail)

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

export default { 
  login,
  register,
  sendRecoveryEmail,
  resetPassword,
  verifyEmail,
  sendVerificationCode,
  usernameExists,
  emailExists
}
