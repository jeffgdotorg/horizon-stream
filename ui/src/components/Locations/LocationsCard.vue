<template>
  <div class="locations-card-wrapper selected">
    <div class="name">
      <ButtonText
        :btn-props="buttonProps"
        data-test="name"
      />
    </div>
    <PillColor
      :type="item.status"
      class="status"
      data-test="status"
    />
    <div class="expiry">
      <FeatherIcon
        :icon="iconExpiry.image"
        :viewBox="setViewBox(iconExpiry.image)"
        data-test="icon-expiry"
      />
    </div>
    <div class="context-menu">
      <HoverMenu
        :items="item.contextMenu"
        data-test="context-menu"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import Expiry from '@/assets/placeholder.svg'
import { IIcon, fncVoid } from '@/types'
import { setViewBox } from '@/components/utils'
import { useLocationsStore } from '@/store/Views/locationsStore'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const locationsStore = useLocationsStore()
const buttonProps = {
  label: props.item.location,
  cb: locationsStore.selectLocation,
  cbArgs: {
    id: props.item.id
  }
}

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
