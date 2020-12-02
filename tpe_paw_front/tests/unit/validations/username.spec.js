import { expect } from 'chai'
import i18n from '@/i18n'
import validations from '@/functions/validations'

describe ('Username validations', () => {
  it('Usernames', () => {
    let invalidNotice = i18n.t('validations.usernamePattern')
    expect(validations.usernamePattern('User1')).to.be.equal(true)
    expect(validations.usernamePattern('user_2')).to.be.equal(true)
    expect(validations.usernamePattern('us3r.3')).to.be.equal(true)
    expect(validations.usernamePattern('user;4')).to.be.equal(invalidNotice)
    expect(validations.usernamePattern('user/5')).to.be.equal(invalidNotice)
    expect(validations.usernamePattern('user-6')).to.be.equal(true)
    expect(validations.usernamePattern('user&7')).to.be.equal(invalidNotice)
    expect(validations.usernamePattern('user!8')).to.be.equal(invalidNotice)
    expect(validations.usernamePattern('user(9)')).to.be.equal(invalidNotice)
    expect(validations.usernamePattern('user#10')).to.be.equal(invalidNotice)
    expect(validations.usernamePattern('user=11')).to.be.equal(invalidNotice)
    expect(validations.usernamePattern('user?12')).to.be.equal(invalidNotice)

})
})