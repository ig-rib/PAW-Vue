<template>
    <router-view ref="languagesSuperRouterView"></router-view>
</template>

<script>
export default {
  name: 'LanguagesSuper',
  mounted () {
    this.$on('searchResults', (r) => {
      this.$refs.languagesSuperRouterView.$emit('searchResults', r)
    })
  }
}
</script>
