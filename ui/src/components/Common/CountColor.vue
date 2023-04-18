<template>
  <div
    class="conut-color"
    data-test="count-color"
  >
    <div>{{ count }}</div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  count: number
}>()
</script>

<style lang="scss" scoped>
.total-flows {
  background-color: rgba(0, 102, 109, 0.12);
  color: #00666d;
  padding: 4px 8px;
  border-radius: 4px;
}
</style>
