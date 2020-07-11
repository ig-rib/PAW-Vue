<template>
  <v-container> 
      <v-card max-width="420">
          <!-- User and language -->
          <v-container fill-height fluid>
            
              <v-row class="pt-0" align="start" justify="space-between">
                  <v-col class="pt-0" cols="8">
                    <v-list-item two-line>
                      <v-avatar class="mr-2" color="indigo">
                          <v-icon dark>mdi-account-circle</v-icon>
                      </v-avatar>
                      <v-list-item-content> 
                        <v-list-item-title class="headline mb-1">{{ owner }}</v-list-item-title>
                        <v-list-item-subtitle>{{ date }}</v-list-item-subtitle>
                      </v-list-item-content>
                    </v-list-item>
                  </v-col>

                  <v-col cols='4'>
                    <v-icon class="mr-1 mt-1">
                      mdi-heart
                    </v-icon>
                    <v-chip
                        class="mt-2"
                        color="light-blue"
                        label
                        text-color="white"
                    >
                        {{ language }}
                    </v-chip>
                  </v-col>
              </v-row>

              <!-- Title and description -->
              <v-list-item two-line>
                <v-list-item-content class="pa-0"> 
                  <v-list-item-title class="headline mb-1">{{ title }}</v-list-item-title>
                  <p>{{ description }}</p>
                </v-list-item-content>
              </v-list-item>

              <!-- Code preview -->
              <v-textarea
                class="pl-2"
                name="input-7-1"
                filled
                label="Code preview"
                :value="code"
              ></v-textarea>

          </v-container>
      </v-card>
  </v-container>
  
</template>

<script>
export default {
  name: 'SnippetComponent', 

  components: {
    // TagComponent
  },
  props: {
      title: String,
      description: String,
      code: String,
      language: String,
      favorite: Boolean,
      date: Date,
      owner: String
  },

  data () {
    return {
      
    }
  },
  methods: {
  }
}
</script>

<style lang="scss">

</style>
