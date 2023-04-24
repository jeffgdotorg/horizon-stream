<template>
  <div class="footer-section-wrapper">
    <hr />
    <div class="actions">
      <FeatherButton
        v-if="cancel"
        @click="cancel.cb"
        secondary
        data-test="cancel-button"
        >{{ cancel.label || 'cancel' }}</FeatherButton
      >
      <ButtonWithSpinner
        v-if="save"
        :type="save.type || 'submit'"
        primary
        :isFetching="saveIsFetching"
        :disabled="saveIsDisabled"
        data-test="save-button"
      >
        {{ save.label || 'save' }}
      </ButtonWithSpinner>
      <!-- <ButtonWithSpinner
        v-if="save"
        :type="save.type || 'submit'"
        primary
        :isFetching="save.isFetching"
        :disabled="saveIsDisabled"
        data-test="save-button"
      >
        {{ save.label || 'save' }}
      </ButtonWithSpinner> -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { fncVoid } from '@/types'
// import { ComputedRef } from 'vue'

type Btn = {
  label?: string
  cb?: fncVoid
  type?: string
  isFetching?: boolean
  // isDisabled?: ComputedRef<boolean>
}

const props = defineProps<{
  save?: Btn
  cancel?: Btn
}>()

const saveIsFetching = false //computed(() => props.save?.isFetching?.value)
const saveIsDisabled = false //computed(() => props.save?.isDisabled?.value)
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
.footer-section-wrapper {
  margin-top: var(variables.$spacing-l);
}
.actions {
  display: flex;
  justify-content: flex-end;
  margin-top: var(variables.$spacing-l);
}
</style>
