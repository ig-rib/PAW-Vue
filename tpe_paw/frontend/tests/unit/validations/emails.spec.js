import i18n from '@/i18n'
import { expect } from 'chai'
import validations from '@/functions/validations'

// examples from several sources

describe('Emails', () => {
    it('valid emails', () => {
      const invalidNotice = i18n.t('validations.invalidEmail')
      expect(validations.email('Abc@example.com')).to.be.equal(true)
      expect(validations.email('disposable.style.email.with+symbol@example.com')).to.be.equal(true)
      expect(validations.email('" "@example.org')).to.be.equal(true)
      expect(validations.email('!#$%&\'*+-/=?^_`.{|}~@example.com')).to.be.equal(true)
      expect(validations.email('user.name+tag+sorting@example.com')).to.be.equal(true)
      expect(validations.email('Abc.123@example.com')).to.be.equal(true)
    })
    it('some more valid emails', () => {
      const invalidNotice = i18n.t('validations.invalidEmail')
      expect(validations.email('user+mailbox/department=shipping@example.com')).to.be.equal(true)
      expect(validations.email('Loïc.Accentué@voilà.fr')).to.be.equal(true)
      expect(validations.email('"()<>[]:,;@\\"!#$%&\'-/=?^_`{}| ~.a"@example.org')).to.be.equal(true)
      expect(validations.email('"very.(),:;<>[]".VERY."very@\\ "very".unusual"@strange.example.com')).to.be.equal(true)
      expect(validations.email('"Joe.\\Blow"@example.com')).to.be.equal(true)
      expect(validations.email('"Abc@def"@example.com')).to.be.equal(true)
      expect(validations.email('"Fred Bloggs"@example.com')).to.be.equal(true)
    })
    it('some other "emails"', () => {
      const invalidNotice = i18n.t('validations.invalidEmail')
      expect(validations.email('user@localserver')).to.be.equal(invalidNotice)
      expect(validations.email('Abc@example.com.')).to.be.equal(invalidNotice)
      expect(validations.email('Abc@10.42.0.1')).to.be.equal(invalidNotice)
      expect(validations.email('Abc.example.com')).to.be.equal(invalidNotice)
      expect(validations.email('john..doe@example.com')).to.be.equal(invalidNotice)
      expect(validations.email('this is"notallowed@example.com')).to.be.equal(invalidNotice)
    })
    it('more emails', () => {
      const invalidNotice = i18n.t('validations.invalidEmail')
      expect(validations.email(null)).to.be.equal(invalidNotice)
      expect(validations.email('just"not"right@example.com')).to.be.equal(invalidNotice)
      expect(validations.email('A@b@c@example.com')).to.be.equal(invalidNotice)
      expect(validations.email('this still"not\\allowed@example.com')).to.be.equal(invalidNotice)
      expect(validations.email('user@[IPv6:2001:DB8::1]')).to.be.equal(invalidNotice)
      expect(validations.email('john.doe@example..com')).to.be.equal(invalidNotice)
      expect(validations.email('a"b(c)d,e:f;g<h>i[jk]l@example.com')).to.be.equal(invalidNotice)
    })
  })
