/* eslint-disable */

<template>
  <v-container>
    <div>
        <p> Languages </p>
        <p> {{pageSize}} </p>
        <v-row>
            <v-col v-for="lang in languages" :key="lang.id">
                <div>
                    <v-chip class="ma-2 language-chip" label>{{ lang.name }}</v-chip>
                </div>
            </v-col>
        </v-row>
    </div>
    <div class="text-center">
      <v-pagination
        v-model="page"
        :length="6"
      ></v-pagination>
    </div>
  </v-container>
</template>

<script>
import languages from '@//services/languages.js'

export default {
  name: 'languages',
  data () {
    return {
      languages: null,
      pageSize: 0
    }
  },
  methods: {
  },
  mounted () {
    // Promise
    languages.getLanguages()
      .then(values => {
        this.languages = values.data 

        let arrData = values.split("link:")
        data = arrData.length == 2? arrData[1]: data;
        let parsed_data = {}
        arrData = data.split(",")
        for (d of arrData){
            linkInfo = /<([^>]+)>;\s+rel="([^"]+)"/ig.exec(d)
            parsed_data[linkInfo[2]]=linkInfo[1]
        }
        })
      .catch(error => { console.log(error) })
  }
}

</script>

<style lang="scss">

.language-chip{
    width: 100px;
}

</style>
