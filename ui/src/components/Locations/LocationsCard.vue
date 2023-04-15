<template>
  <div class="container">
    <div class="name">Localhost</div>
    <div class="status">color pill</div>
    <FeatherIcon
      :icon="icon.AccountCircle"
      class="my-primary-icon"
    >
    </FeatherIcon>
  </div>
  <!-- context menu -->
</template>

<script lang="ts" setup>
import AccountCircle from '@featherds/icon/action/AccountCircle'

const icon = markRaw({
  AccountCircle
})
</script>

<style lang="scss" scoped></style>
