import i18n from '@/i18n'

const validPassword = (password) => password.length >= 8 || i18n.t('validations.invalidPassword')
const email = (wouldBeEmail) => {
   const regex = /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>().,;\s@"]+\.{1})+([^<>().,;:\s@"]{2,}))$/
    return regex.test(wouldBeEmail) || i18n.t('validations.invalidEmail')
  }
export default {
    validPassword,
    email
}
