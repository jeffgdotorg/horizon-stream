<template>
  <div class="locations-list-wrapper">
    <HeadlineSection
      text="locations"
      data-test="headline"
      @click="locationsStore.setDisplayType(DisplayType.LIST)"
    >
      <template #left>
        <CountColor
          :count="items?.length"
          data-test="count"
        />
      </template>
      <template #right>
        <FeatherIcon
          :icon="icons.Help"
          data-test="icon-help"
        />
      </template>
    </HeadlineSection>
    <div class="locations-list">
      <div class="header">
        <div
          class="name"
          data-test="name"
        >
          Name
        </div>
        <div
          class="status"
          data-test="status"
        >
          Status
        </div>
      </div>
      <ul>
        <li
          v-for="(item, index) in items"
          :key="index"
          data-test="card"
        >
          <LocationsCard :item="item" />
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import HeadlineSection from '@/components/Common/HeadlineSection.vue'
import Help from '@featherds/icon/action/Help'
import { LocationTemp } from '@/types/locations.d'
import { PropType } from 'vue'
import { useLocationsStore } from '@/store/Views/locationsStore'
import { DisplayType } from '@/types/locations.d'

defineProps({
  items: {
    type: Array as PropType<LocationTemp[]>,
    required: true
  }
})

const locationsStore = useLocationsStore()

const icons = markRaw({
  Help
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/vars.scss';

.locations-list-wrapper {
  padding: var(variables.$spacing-m) var(variables.$spacing-s);
  background: var(variables.$surface);
  border-radius: vars.$border-radius-s;

  .header {
    display: flex;
    align-items: center;
    gap: var(variables.$spacing-s);
    padding: var(variables.$spacing-xs) var(variables.$spacing-s);
    background-color: var(variables.$background);
    > * {
      &:nth-child(1) {
        width: 40%;
      }
      &:nth-child(2) {
        width: 30%;
        display: flex;
        justify-content: center;
      }
    }
  }
}
</style>
