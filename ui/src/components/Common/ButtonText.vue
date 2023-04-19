<template>
  <button
    :type="type"
    class="button-text"
    data-test="button-text"
  >
    <span>{{ label }}</span>
  </button>
</template>

<script lang="ts" setup>
import { PropType, ButtonHTMLAttributes } from 'vue'

defineProps({
  label: {
    type: String,
    required: true
  },
  type: {
    type: String as PropType<ButtonHTMLAttributes['type']>,
    default: 'button'
  }
})
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
