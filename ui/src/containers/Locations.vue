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
        <LocationsMinionsList
          v-if="locationsStore.displayType === DisplayType.LIST"
          :minions="minionsList"
        />
        <LocationsAddForm
          v-if="locationsStore.displayType === DisplayType.ADD"
          data-test="location-add-form"
        />
        <LocationsEditForm
          v-if="locationsStore.displayType === DisplayType.EDIT"
          :id="selectedLocationId"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import Add from '@featherds/icon/action/Add'
import Search from '@featherds/icon/action/Search'
import Help from '@featherds/icon/action/Help'
import { useLocationsStore } from '@/store/Views/locationsStore'
import LocationsList from '@/components/Locations/LocationsList.vue'
import { DisplayType } from '@/types/locations.d'

const locationsStore = useLocationsStore()

const locationsList = computed(() => locationsStore.locationsList)
const minionsList = computed(() => locationsStore.minionsList)

onMounted(async () => {
  await locationsStore.fetchLocations()
  await locationsStore.fetchMinions()
})

const addLocation = () => {
  locationsStore.setDisplayType(DisplayType.ADD)
}

const selectedLocationId = computed(() => locationsStore.selectedLocationId)

const searchLocationListener = (val: string | number | undefined) => {
  locationsStore.searchLocations(val as string)
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
@use '@/styles/vars.scss';

.wrapper {
  .content-left {
    .search-location-input {
      width: 100%;

      @include mediaQueriesMixins.screen-md {
        width: 100%;
      }
      @include mediaQueriesMixins.screen-xl {
        width: 50%;
      }
    }
  }
}
</style>
