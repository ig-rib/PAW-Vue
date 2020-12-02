import i18n from '@/i18n'

const validPassword = (password) => password.length >= 8 || i18n.t('validations.invalidPassword')
const email = (wouldBeEmail) => {
  const regex = /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>().,;\s@"]+\.{1})+([^<>().,;:\s@"]{2,}))$/
  return (wouldBeEmail != null && regex.test(wouldBeEmail)) || i18n.t('validations.invalidEmail')
}
const maxLength = (text, length) => (text == null || text.length <= length) || i18n.t('validations.maxLength', { maxLength: length })
const minLength = (text, length) => (text != null && text.length >= length) || i18n.t('validations.minLength', { minLength: length })
const lengthBetween = (text, minLength, maxLength) => (text != null && text.length >= minLength && text.length <= maxLength) || i18n.t('validations.lengthBetween', { minLength, maxLength })
const usernamePattern = (text) => (text != null && /^[a-zA-Z0-9-_.]+$/.test(text)) || i18n.t('validations.usernamePattern')
const notBlankWithSpaces = (text) => (text != null && text.trim().length > 0) || i18n.t('validations.notBlankWithSpaces')
const codePattern = (code) => (code != null && /^[0-9]{6}$/.test(code)) || i18n.t('validations.codePattern')

export default {
    validPassword,
    email,
    maxLength,
    minLength,
    lengthBetween,
    usernamePattern,
    notBlankWithSpaces,
    codePattern
}
