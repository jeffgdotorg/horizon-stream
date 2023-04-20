<template>
  <div class="locations-card-wrapper selected">
    <div class="name">
      <ButtonText :label="item.location" />
    </div>
    <PillColor
      :type="item.status"
      class="status"
    />
    <div class="expiry">
      <FeatherIcon
        :icon="iconExpiry.image"
        :viewBox="setViewBox(iconExpiry.image)"
      />
    </div>
    <div class="context-menu">
      <HoverMenu :items="item.contextMenu" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import Expiry from '@/assets/placeholder.svg'
import { IIcon } from '@/types'
import { setViewBox } from '@/components/utils'

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
  padding: 0 var(variables.$spacing-s);
}

.name {
  width: 40%;
}
.status {
  width: 30%;
  display: flex;
  justify-content: center;
}
.expiry {
  width: 15%;
  display: flex;
  justify-content: center;
}
.context-menu {
  width: 7%;
  display: flex;
  justify-content: flex-end;
}

.selected {
  background-color: var(variables.$shade-4);
}
</style>
