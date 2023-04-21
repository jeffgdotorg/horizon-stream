<template>
  <div class="minions-list-wrapper">
    <HeadlineSection
      text="Default"
      data-test="headline"
    >
      <template #left>
        <CountColor
          :count="minions?.length"
          data-test="count"
        />
      </template>
      <template #middle>
        <FeatherInput
          @update:model-value="searchMinionsListener"
          label="Search Minions"
          type="search"
          class="search-minions-input"
          data-test="search-input"
        >
          <template #pre>
            <FeatherIcon :icon="icons.Search" />
          </template>
        </FeatherInput>
      </template>
      <template #right>
        <FeatherIcon
          :icon="icons.Help"
          class="icon-help"
          data-test="icon-help"
        />
      </template>
    </HeadlineSection>
    <ul class="minions-list">
      <li
        v-for="minion in minions"
        :key="minion.id"
      >
        <LocationsMinionsCard :item="minion" />
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import HeadlineSection from '@/components/Common/HeadlineSection.vue'
import Help from '@featherds/icon/action/Help'
import Search from '@featherds/icon/action/Search'
import { useLocationsStore } from '@/store/Views/locationsStore'

defineProps({
  minions: {
    type: Array,
    required: true
  }
})

const locationsStore = useLocationsStore()

const searchMinionsListener = (val: string | number | undefined) => {
  locationsStore.searchMinions(val as string)
}

const icons = markRaw({
  Help,
  Search
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/vars.scss';

.minions-list-wrapper {
  padding: var(variables.$spacing-m) var(variables.$spacing-s);
  background: var(variables.$surface);
  border-radius: vars.$border-radius-s;

  .search-minions-input {
    width: 100%;
    :deep(.feather-input-sub-text) {
      display: none;
    }
  }
}
</style>
