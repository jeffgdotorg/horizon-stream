<template>
  <div class="locations-add-form-wrapper">
    <form
      @submit.prevent="onSubmit"
      novalidate
      class="locations-add-form"
    >
      <HeadlineSection text="Add Location" />
      <div class="inputs">
        <div class="row">
          <FeatherInput
            label="Location Name *"
            v-model="inputs.name"
            :schema="nameV"
            required
            class="input-name"
          >
            <template #pre> <FeatherIcon :icon="icons.Location" /> </template
          ></FeatherInput>
        </div>
        <div class="row">
          <FeatherInput
            label="Longitude (optional)"
            v-model="inputs.longitude"
            class="input-longitude"
          >
            <template #pre> <FeatherIcon :icon="icons.placeholder" /> </template
          ></FeatherInput>
          <FeatherInput
            label="Latitude (optional)"
            v-model="inputs.latitude"
            class="input-latitude"
          >
            <template #pre> <FeatherIcon :icon="icons.placeholder" /> </template
          ></FeatherInput>
        </div>
      </div>
      <FooterSection :save="saveBtn" />
    </form>
  </div>
</template>

<script setup lang="ts">
import Location from '@featherds/icon/action/Location'
import placeholder from '@/assets/placeholder.svg'
import { string } from 'yup'
import { useForm } from '@featherds/input-helper'

const inputs = reactive({
  name: '',
  longitude: '',
  latitude: ''
})

const form = useForm()
const nameV = string().required('Location name is required.')

const onSubmit = () => {
  const formInvalid = form.validate().length > 0 // array of errors

  if (formInvalid) return

  console.log('call api endpoint to save form...')
}

const saveBtn = {
  label: 'Add Location',
  cb: () => ({})
  // isDisabled: computed(() => !inputs.name)
}

const icons = markRaw({
  Location,
  placeholder
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/mixins.scss';
@use '@/styles/mediaQueriesMixins.scss';

.locations-add-form-wrapper {
  @include mixins.wrapper-on-background();

  .row {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    margin-bottom: var(variables.$spacing-xs);
    :deep(.feather-icon) {
      &.error-icon {
        color: var(variables.$error);
      }
    }
    > * {
      width: 100%;
    }

    @include mediaQueriesMixins.screen-sm {
      > * {
        width: 49%;
      }
    }
    @include mediaQueriesMixins.screen-md {
      > * {
        width: 100%;
      }
    }
    @include mediaQueriesMixins.screen-lg {
      > * {
        width: 49%;
      }
    }
  }
}
</style>
