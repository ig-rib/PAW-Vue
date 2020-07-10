import urls from './urls'
import axiosFetcher from './axiosFetcher'

const login = (username, password) => axiosFetcher.post(urls.registration.login, {}, {
    username,
    password
})

export default { 
    login
}
