<template>
  <div class="wrapper">
    <div class="header">
      <HeadlinePage
        text="Locations"
        data-test="locations-headline"
      />
      <div><AppliancesNotificationsCtrl data-test="locations-notification-ctrl" /></div>
    </div>
    <div class="content">
      <div class="content-left">
        <FeatherButton
          primary
          @click="addLocation"
          data-test="add-location-btn"
        >
          <FeatherIcon :icon="icons.Add" />
          Location
        </FeatherButton>
        <hr />
        <FeatherInput
          @update:model-value="searchLocationListener"
          label="Search Location"
          type="search"
          class="search-location-input"
          data-test="search-input"
        >
          <template #pre>
            <FeatherIcon :icon="icons.Search" />
          </template>
        </FeatherInput>
        <LocationsList :items="locationsList" />
      </div>
      <div class="content-right">
        <!-- minions list of a location -->
        <!--  header -->
        <!--  card list -->
        <!-- OR -->
        <!-- location add form -->
        <HeadlineSection text="Default" />
        <ul class="locations-list">
          <li
            v-for="location in locationsList"
            :key="location.id"
          >
            <LocationsMinionsCard :item="location" />
          </li>
        </ul>

        <!--  inputs, actions -->
        <FooterSection
          :save="btns.save"
          :cancel="btns.cancel"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import Add from '@featherds/icon/action/Add'
import Search from '@featherds/icon/action/Search'
import { useLocationsStore } from '@/store/Views/locationsStore'
import LocationsList from '@/components/Locations/LocationsList.vue'

const btns = {
  save: { label: 'save', handler: () => ({}) },
  cancel: { label: 'cancel', handler: () => ({}) }
}

const items = [
  {
    id: 1,
    location: 'minion0',
    version: 'v.0.0.0',
    latency: '000m',
    status: 'UP',
    utillization: '00%',
    ip: '000.000.000.000',
    contextMenu: [
      { label: 'edit', handler: () => ({}) },
      { label: 'delete', handler: () => ({}) }
    ]
  },
  {
    id: 2,
    location: 'minion9',
    version: 'v.9.9.9',
    latency: '999m',
    status: 'DOWN',
    utillization: '99%',
    ip: '999.999.999.999',
    contextMenu: [
      { label: 'edit', handler: () => ({}) },
      { label: 'delete', handler: () => ({}) }
    ]
  }
]

const locationsStore = useLocationsStore()

const locationsList = computed(() => items) //locationsStore.locationsList)

onMounted(async () => {
  await locationsStore.fetchLocations()
})

const addLocation = () => ({})

const searchLocationListener = (val: string | number | undefined) => {
  locationsStore.searchLocation(val as string)
}

const icons = markRaw({
  Add,
  Search
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/layout/headlineTwoColumns';

.content-right {
  .locations-list {
    > li {
      margin-bottom: var(variables.$spacing-xs);
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>
