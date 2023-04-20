<template>
  <div class="footer-section-wrapper">
    <hr />
    <div class="actions">
      <FeatherButton
        @click="cancel.handler"
        secondary
        data-test="cancel-button"
        >{{ cancel.label || 'cancel' }}</FeatherButton
      >
      <ButtonWithSpinner
        :type="save.type || 'submit'"
        primary
        :isFetching="save.isFetching"
        data-test="save-button"
      >
        {{ save.label || 'save' }}
      </ButtonWithSpinner>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { fncVoid } from '@/types'

type Action = {
  label?: string
  handler?: fncVoid
  type?: string
  isFetching?: boolean
}

defineProps({
  save: {
    type: Object as PropType<Action>,
    required: true
  },
  cancel: {
    type: Object as PropType<Action>,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.actions {
  display: flex;
  justify-content: flex-end;
}
</style>
