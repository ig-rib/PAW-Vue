<template>
  <v-layout>
    <v-menu
      v-model="dateMenu"
      >
      <template v-slot:activtor="{ on }">
        <v-text-field
          readonly
          :value="displayedDate"
          v-on="on"
          ></v-text-field>
      </template>
      <v-date-picker
        :min="minDate"
        :max="maxDate"
        v-model="dateValue"
        @input="dateMenu = false"
      >
      </v-date-picker>
    </v-menu>
  </v-layout>  
</template>
<script>
export default {
  props: ['label'],
  data () {
    return {
      dateMenu: true,
      dateValue: null,
      minDate: null,
      maxDate: null,
      displayedDate: ''
    }
  }
}
</script>