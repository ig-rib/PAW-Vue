import { expect } from 'chai'
import i18n from '@/i18n'
import validations from '@/functions/validations'

describe('Number and text code validations', () => {
  it('Code pattern', () => {
    const invalidNotice = i18n.t('validations.codePattern')
    expect(validations.codePattern('123412')).to.be.equal(true)
    expect(validations.codePattern('ad')).to.be.equal(invalidNotice)
    expect(validations.codePattern('')).to.be.equal(invalidNotice)
    expect(validations.codePattern('1234122')).to.be.equal(invalidNotice)
    expect(validations.codePattern(null)).to.be.equal(invalidNotice)
  })
  it('Not blank with spaces', () => {
    const invalidNotice = i18n.t('validations.notBlankWithSpaces')
    expect(validations.notBlankWithSpaces('              ')).to.be.equal(invalidNotice)
    expect(validations.notBlankWithSpaces('\t\n ')).to.be.equal(invalidNotice)
    expect(validations.notBlankWithSpaces('someCode')).to.be.equal(true)
    expect(validations.notBlankWithSpaces(null)).to.be.equal(invalidNotice)
  })
})
