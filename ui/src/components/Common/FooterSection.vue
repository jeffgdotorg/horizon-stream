<template>
  <div class="footer-section-wrapper">
    <hr />
    <div class="actions">
      <FeatherButton
        v-if="cancel"
        @click="cancel ? cancel.callback(cancel.callbackArgs?.type) : () => ({})"
        secondary
        data-test="cancel-button"
        >{{ cancel.label || 'cancel' }}</FeatherButton
      >
      <ButtonWithSpinner
        v-if="save"
        @click="save ? save.callback(save.callbackArgs) : () => ({})"
        :type="save.type || 'submit'"
        :isFetching="saveIsFetching"
        :disabled="saveIsDisabled"
        primary
        data-test="save-button"
      >
        {{ save.label || 'save' }}
      </ButtonWithSpinner>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ButtonCallbackArgs } from '@/types'

const props = defineProps<{
  save?: ButtonCallbackArgs
  cancel?: ButtonCallbackArgs
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
