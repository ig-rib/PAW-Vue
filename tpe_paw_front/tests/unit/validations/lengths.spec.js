import { expect } from 'chai'
import i18n from '@/i18n'
import validations from '@/functions/validations'

describe ('String lengths', () => {
  it('minimum length', () => {
    let length = 8
    expect(validations.minLength('d9f8asdf92', length)).to.be.equal(true)
    expect(validations.minLength('3h1u2', length)).to.be.equal(i18n.t('validations.minLength', { minLength: length }))
    expect(validations.minLength(null, length)).to.be.equal(i18n.t('validations.minLength', { minLength: length }))
    expect(validations.minLength('', length)).to.be.equal(i18n.t('validations.minLength', { minLength: length }))
  })
  it('maximum length', () => {
    let length = 8
    expect(validations.maxLength('d9f8asdf92', length)).to.be.equal(i18n.t('validations.maxLength', { maxLength: length }))
    expect(validations.maxLength('3h1u2', length)).to.be.equal(true)
    expect(validations.maxLength(null, length)).to.be.equal(true)
    expect(validations.maxLength('', length)).to.be.equal(true)
  })
  it('lenght between', () => {
    let minLength = 8
    let maxLength = 15
    expect(validations.maxLength('d9f8asdf92', length)).to.be.equal(true)
    expect(validations.maxLength('3h1u2', length)).to.be.equal(i18n.t('validations.lengthBetween', { minLength, maxLength }))
    expect(validations.maxLength('3h1u2ejklkj12kj52l;kj', length)).to.be.equal(i18n.t('validations.lengthBetween', { minLength, maxLength }))
    expect(validations.maxLength(null, length)).to.be.equal(i18n.t('validations.lengthBetween', { minLength, maxLength }))
    expect(validations.maxLength('', length)).to.be.equal(i18n.t('validations.lengthBetween', { minLength, maxLength }))
  })
})