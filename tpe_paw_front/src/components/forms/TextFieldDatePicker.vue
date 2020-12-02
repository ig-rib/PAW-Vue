<template>
  <v-layout>
    <v-menu
      v-model="dateMenu"
      :close-on-content-click="false"
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
          rounded
          outlined
          dense
          hide-details
        ></v-text-field>
      </template>
      <v-card>
        <v-layout column justify-center align-center>
          <v-flex>
            <v-date-picker
              :min="minDate"
              :max="maxDate"
              v-model="dateValue"
              no-title
              @input="updateDate"
            ></v-date-picker>
          </v-flex>
          <v-flex py-2>
            <v-btn @click="clearDate">{{ $t('components.tfDatePicker.clearDate') }}</v-btn>
          </v-flex>
        </v-layout>
      </v-card>
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
