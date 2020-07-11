<template>
  <v-container>
    <div>
        <SnippetComponent 
            title='Titulo'
            description='Descripcion'
            code='Codigo'
            language='lenguaje'
            owner='Augusto'
        />
        <SnippetComponent/>
    </div>
  </v-container>
</template>

<script>
import SnippetComponent from '@//components/Snippet.vue'

export default {
    name: 'languagesSnippet',
    components: {
        SnippetComponent
    },
    data () {
    return {
      snippetData: {
        title: 'hola'
      }
    }
  },

}

</script>

<style lang="scss">

</style>
