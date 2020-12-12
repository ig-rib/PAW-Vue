<template>
    <router-view ref="languagesSuperRouterView"></router-view>
</template>

<script>
export default {
    title (){  return this.$t('titles.languagesSuper')},
  name: 'LanguagesSuper',
  mounted () {
    this.$on('searchResults', (r) => {
      this.$refs.languagesSuperRouterView.$emit('searchResults', r)
    })
  }
}
</script>
