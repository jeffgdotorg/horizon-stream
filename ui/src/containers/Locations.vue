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
        <LocationsList :items="locationsList" />
      </div>
      <div class="content-right">
        <!-- minions list of a location -->
        <!--  header -->
        <!--  card list -->
        <!-- OR -->
        <!-- location add form -->
        <LocationsMinionsList :minions="minionsList" />

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
import Help from '@featherds/icon/action/Help'
import { useLocationsStore } from '@/store/Views/locationsStore'
import LocationsList from '@/components/Locations/LocationsList.vue'

const btns = {
  save: { label: 'save', handler: () => ({}) },
  cancel: { label: 'cancel', handler: () => ({}) }
}

const locationsStore = useLocationsStore()

const locationsList = computed(() => locationsStore.locationsList)
const minionsList = computed(() => locationsStore.minionsList)

onMounted(async () => {
  await locationsStore.fetchLocations()
  await locationsStore.fetchMinions()
})

const addLocation = () => ({})

const searchLocationsListener = (val: string | number | undefined) => {
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
