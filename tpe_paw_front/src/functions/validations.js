import i18n from '@/i18n'

const validPassword = (password) => password.length >= 8 || i18n.t('validations.invalidPassword')
const email = (wouldBeEmail) => {
   const regex = /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>().,;\s@"]+\.{1})+([^<>().,;:\s@"]{2,}))$/
    return regex.test(wouldBeEmail) || i18n.t('validations.invalidEmail')
  }
const maxLength = (text, length) => text.length <= length || i18n.t('validations.maxLength', { maxLength: length })
const minLength = (text, length) => text.length <= length || i18n.t('validations.minLength', { minLength: length })
const lengthBetween = (text, minLength, maxLength) => (text.length >= minLength && text.length <= maxLength) || i18n.t('validations.lengthBetween', { minLength, maxLength })
const usernamePattern = (text) => /^[a-zA-Z0-9-_.]+$/.test(text) || i18n.t('validations.usernamePattern')

export default {
    validPassword,
    email,
    maxLength,
    minLength,
    lengthBetween,
    usernamePattern
}
