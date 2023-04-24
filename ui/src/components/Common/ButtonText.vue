<template>
  <button
    @click="btnProps.cb(btnProps.cbArgs.id)"
    :type="btnProps.type as ButtonHTMLAttributes['type'] || 'button'"
    class="button-text"
    data-test="button-text"
  >
    <span>{{ btnProps.label }}</span>
  </button>
</template>

<script lang="ts" setup>
import { PropType, ButtonHTMLAttributes } from 'vue'
import { fncVoid, fncArgVoid } from '@/types'

type ButtonText = {
  label: string
  type?: string
  cb: fncArgVoid
  cbArgs: {
    id: number
  }
}

defineProps<{
  btnProps: ButtonText
}>()
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.button-text {
  display: contents;
  color: var(variables.$primary);
  border: none;
  background-color: transparent;
  padding: 0;
  margin-bottom: var(variables.$spacing-l);
  &:hover {
    cursor: pointer;
  }
}
</style>
