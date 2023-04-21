<template>
  <div class="pill-color-wrapper">
    <span
      :class="['pill-type', `${item.type?.toLowerCase()}-color`]"
      data-test="pill-type"
    >
      {{ item.label || item.type }}
    </span>
  </div>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'

type Pill = {
  type: string
  label?: string
}

defineProps({
  item: {
    type: Object as PropType<Pill>,
    required: true
  }
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/mixins/typography';
@use '@/styles/vars.scss';
@use '@/styles/pillColor.scss';

.pill-type {
  @include typography.title();
  display: inline-block;
  font-size: 0.8rem;
  line-height: normal;
  padding: 2px 8px;
  border-radius: vars.$border-radius-sm;
}
</style>
