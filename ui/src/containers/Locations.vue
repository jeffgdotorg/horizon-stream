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
        <!--  header -->
        <!--  inputs, actions -->
        <!--  footer: cancel, save -->
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import Add from '@featherds/icon/action/Add'
import Search from '@featherds/icon/action/Search'
import { useLocationsStore } from '@/store/Views/locationsStore'
import LocationsList from '@/components/Locations/LocationsList.vue'

const locationsStore = useLocationsStore()

const locationsList = computed(() => locationsStore.locationsList)

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
@use '@/styles/layout/headlineTwoColumns';
</style>
