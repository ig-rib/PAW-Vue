<template>
  <router-view ref="tagsSuperRouterView"></router-view>
</template>

<script>
// There is no way of knowing 
export default {
  title: 'Snippit - Tags',
  name: 'TagsSuper',
  mounted () {
    this.$on('searchResults', (r) => {
      this.$refs.tagsSuperRouterView.$emit('searchResults', r)
    })
  }
}
</script>
