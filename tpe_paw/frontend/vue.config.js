module.exports = {
  transpileDependencies: [
    'vuetify'
  ],
  pluginOptions: {
    i18n: {
      locale: 'en',
      fallbackLocale: 'en',
      localeDir: 'locales',
      enableInSFC: false
    }
  },
  publicPath: '/'
  // TODO change on deploy
  // publicPath: '/paw-2020a-2'
}
