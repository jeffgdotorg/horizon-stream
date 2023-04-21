<template>
  <div class="pill-color-wrapper">
    <span
      :class="['pill-type', `${type?.toLowerCase()}-color`]"
      data-test="pill-type"
    >
      {{ type }}
    </span>
  </div>
</template>

<script lang="ts" setup>
defineProps({
  type: { type: String, required: true }
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
