<template>
  <div class="minions-card-wrapper">
    <div class="header-content">
      <div class="header">
        <div class="name">{{ item.name }}</div>
        <div class="latency">Latency</div>
        <div class="status">Status</div>
        <div class="utilization">Utilization</div>
        <div class="ip">IPv4</div>
      </div>
      <div class="content">
        <div class="version">{{ item.version }}</div>
        <div class="latency">{{ item.latency }}</div>
        <div class="status"><PillColor :item="statusPill" /></div>
        <div class="utilization">{{ item.utillization }}</div>
        <div class="ip">{{ item.ip }}</div>
      </div>
    </div>
    <HoverMenu
      :items="item.contextMenu"
      data-test="context-menu"
    />
  </div>
</template>

<script setup lang="ts">
import { Severity } from '@/types/graphql'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const statusPill =
  props.item.status === 'UP'
    ? { type: Severity.Normal, label: props.item.status }
    : { type: Severity.Critical, label: props.item.status }
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/vars.scss';

.minions-card-wrapper {
  display: flex;
  align-items: center;
  gap: 2%;
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-xs;
  padding: var(variables.$spacing-s);

  .header-content {
    width: 93%;

    .header,
    .content {
      display: flex;
      align-items: center;
      .name {
        width: 25%;
        font-weight: bold;
      }
      .version {
        width: 25%;
      }
      .latency {
        width: 15%;
        display: flex;
        justify-content: center;
      }
      .status {
        width: 15%;
        display: flex;
        justify-content: center;
      }
      .utilization {
        width: 19%;
        display: flex;
        justify-content: center;
      }
      .ip {
        width: 26%;
      }
    }
  }

  .hover-menu {
    width: 5%;
  }
}
</style>
