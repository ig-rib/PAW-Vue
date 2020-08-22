<template>
  <v-layout row wrap>
    <v-menu
      v-model="dateMenu"
      :close-on-content-click="false"
      :nudge-right="40"
      transition="scale-transition"
      offset-y
    >
      <template v-slot:activator="{ on }">
        <v-text-field
          :label="label"
          prepend-icon="mdi-calendar"
          readonly
          :value="displayedDate"
          v-on="on"
        ></v-text-field>
      </template>
      <v-layout>
        <v-date-picker
          :min="minDate"
          :max="maxDate"
          v-model="dateValue"
          no-title
          @input="updateDate"
        ></v-date-picker>
      </v-layout>
      <v-layout>
        <v-btn @click="clearDate">{{ $t('components.tfDatePicker.clearDate') }}</v-btn>
      </v-layout>
    </v-menu>
  </v-layout>
</template>
<script>
export default {
  props: ['label', 'value'],
  data () {
    return {
      dateMenu: false,
      dateValue: null,
      minDate: null,
      maxDate: null
    }
  },
  methods: {
    updateDate () {
      this.dateMenu = false
      this.$emit('input', this.dateValue)
    },
    clearDate () {
      this.dateMenu = false
      this.dateValue = null
      this.$emit('input', null)
    }
  },
  computed: {
    displayedDate () {
      return this.dateValue
    }
  }
}
</script>
