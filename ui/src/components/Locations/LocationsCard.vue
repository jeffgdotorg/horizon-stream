<template>
  <div class="locations-card-wrapper">
    <div class="name">
      <ButtonText :label="item.location" />
    </div>
    <PillColor
      :type="item.status"
      class="status"
    />
    <div class="expiry">
      <!-- TODO: to be used when icon avail in FDS -->
      <!-- <FeatherIcon
        :icon="Expiry"
        class="icon"
      /> -->
      <Icon :icon="iconExpiry" />
    </div>
    <div class="context-menu">
      <HoverMenu :items="item.contextMenu" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import Expiry from '@/assets/placeholder.svg'
import { IIcon } from '@/types'

defineProps({
  item: {
    type: Object,
    required: true
  }
})
const iconExpiry: IIcon = {
  image: Expiry
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.locations-card-wrapper {
  display: flex;
  align-items: center;
  gap: var(variables.$spacing-s);
}

.name {
  flex-grow: 3;
}
.status {
  display: flex;
  flex-grow: 2;
  justify-content: center;
}
.expiry {
  display: flex;
  flex-grow: 1;
  justify-content: center;
}
.context-menu {
  display: flex;
  flex-grow: 0.5;
  justify-content: flex-end;
}
</style>
