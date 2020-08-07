<template>
  <v-container>
    <v-layout>
      <v-flex>
        <v-card>
          <v-layout>
            <v-layout>
              {{ $t('explore.order.title') }}
            </v-layout>
            <v-layout>
              <v-flex>
                <v-layout>
                  {{ $t('explore.order.orderBy') }}
                </v-layout>
                <v-layout>
                  <v-select v-model="orderBy"></v-select>
                </v-layout>
              </v-flex>
              <v-flex>
                <v-layout>
                  {{ $t('explore.order.sort')}}
                </v-layout>
                <v-layout v-model="sort"></v-layout>
              </v-flex>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.flagged.title') }}
            </v-layout>
            <v-layout>
              <v-checkbox v-model="includeFlagged"
                :label="$t('explore.flagged.includeFlagged')"
                ></v-checkbox>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.title.title') }}
            </v-layout>
            <v-layout>
              <v-text-field v-model="snippetTitle"
                :label="$t('explore.title.label')"></v-text-field>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-flex>
              <v-layout>
                {{ $t('explore.language') }}
              </v-layout>
              <v-layout>
                <v-select v-model="language"></v-select>
              </v-layout>
            </v-flex>
            <v-flex>
              <v-layout>
                {{ $t('explore.tag')}}
              </v-layout>
              <v-layout>
                <v-select v-model="tag"></v-select>
              </v-layout>
            </v-flex>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.username') }}
            </v-layout>
            <v-layout>
              <v-text-field v-model="username"
                :label="$t('explore.username')"></v-text-field>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.dateUploaded') }}
            </v-layout>
            <v-layout>
              <v-flex>
                <tf-date-picker
                  v-model="fromDate"
                  :label="$t('explore.from')"
                ></tf-date-picker>
              </v-flex>
              <v-flex>
                <tf-date-picker
                  v-model="toDate"
                  :label="$t('explore.to')"
                ></tf-date-picker>
              </v-flex>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.userReputation.title') }}
            </v-layout>
            <v-layout>
              <v-flex>
                <v-text-field
                  :label="$t('explore.min')"
                  v-model="repMin"
                ></v-text-field>
              </v-flex>
              <v-flex>
                <v-text-field
                  :label="$t('explore.max')"
                  v-model="repMax"
                ></v-text-field>
              </v-flex>
            </v-layout>
          </v-layout>
          <v-divider></v-divider>
          <v-layout>
            <v-layout>
              {{ $t('explore.snippetVotes.title') }}
            </v-layout>
            <v-layout>
              <v-flex>
                <v-text-field
                  :label="$t('explore.min')"
                  v-model="votesMin"
                ></v-text-field>
              </v-flex>
              <v-flex>
                <v-text-field
                  :label="$t('explore.max')"
                  v-model="votesMax"
                ></v-text-field>
              </v-flex>
            </v-layout>
          </v-layout>
        </v-card>
      </v-flex>
      <v-flex>

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
export default {
  data () {
    return {
      orderBy: '',
      sort: '',
      includeFlagged: '',
      snippetTitle: '',
      language: '',
      tag: '',
      username: '',
      fromDate: null,
      toDate: null,
      repMin: null,
      repMax: null,
      votesMin: null,
      votesMax: null
    }
  }
}
</script>
