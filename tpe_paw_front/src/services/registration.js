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

export default { 
  login,
  register,
  sendRecoveryEmail,
  resetPassword
}
