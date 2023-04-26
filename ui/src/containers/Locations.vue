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
          @update:model-value="searchLocationsListener"
          label="Search Location"
          type="search"
          class="search-location-input"
          data-test="search-input"
        >
          <template #pre>
            <FeatherIcon :icon="icons.Search" />
          </template>
        </FeatherInput>
        <!-- <LocationsList :items="locationsList" /> -->
      </div>
      <div class="content-right">
        <LocationsAddForm
          v-if="isAddingLocation"
          data-test="location-add-form"
        />
        <!-- edit location form -->
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import Add from '@featherds/icon/action/Add'
import Search from '@featherds/icon/action/Search'
import Help from '@featherds/icon/action/Help'
import { useLocationStore } from '@/store/Views/locationStore'
import LocationsList from '@/components/Locations/LocationsList.vue'

const btns = {
  save: { label: 'save', handler: () => ({}) },
  cancel: { label: 'cancel', handler: () => ({}) }
}

const locationStore = useLocationStore()

const locationsList = computed(() => locationStore.locationsList)
const minionsList = computed(() => locationStore.minionsList)

onMounted(async () => {
  await locationStore.fetchLocations()
  await locationStore.fetchMinions()
})

const isAddingLocation = ref(false)
const addLocation = () => {
  isAddingLocation.value = true
}

const searchLocationsListener = (val: string | number | undefined) => {
  locationStore.searchLocations(val as string)
}

const icons = markRaw({
  Add,
  Search,
  Help
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/layout/headlineTwoColumns';
@use '@/styles/mediaQueriesMixins.scss';

.content-left {
  .search-location-input {
    width: 100%;

    @include mediaQueriesMixins.screen-sm {
      width: 49%;
    }
    @include mediaQueriesMixins.screen-md {
      width: 100%;
    }
    @include mediaQueriesMixins.screen-xl {
      width: 50%;
    }
  }
}
</style>
