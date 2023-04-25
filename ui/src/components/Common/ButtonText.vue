<template>
  <button
    @click="btnProps.cb(btnProps.cbArgs?.id)"
    :type="btnProps.type as ButtonHTMLAttributes['type'] || 'button'"
    class="button-text"
    data-test="button-text"
  >
    <slot name="pre" />
    <span>{{ btnProps.label }}</span>
    <slot name="post" />
  </button>
</template>

<script lang="ts" setup>
import { ButtonHTMLAttributes } from 'vue'
import { fncArgVoid } from '@/types'

type ButtonText = {
  label: string
  type?: string
  cb: fncArgVoid
  cbArgs?: {
    id: number
  }
  textTransform?: string
}

const props = defineProps<{
  btnProps: ButtonText
}>()

const textTransform = props.btnProps.textTransform || 'none'
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
  text-transform: v-bind(textTransform);
  &:hover {
    cursor: pointer;
  }
}
</style>
