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

export default { 
    login,
    register
}
