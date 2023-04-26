<template>
  <button
    @click="button.callback(button.callbackArgs?.id)"
    :type="button.type as ButtonHTMLAttributes['type'] || 'button'"
    class="button-text"
    data-test="button-text"
  >
    <slot name="pre" />
    <span>{{ button.label }}</span>
    <slot name="post" />
  </button>
</template>

<script lang="ts" setup>
import { ButtonHTMLAttributes } from 'vue'
import { ButtonText } from '@/types'

defineProps<{
  button: ButtonText
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
