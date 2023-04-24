<template>
  <div class="locations-edit-form-wrapper">
    <form
      @submit.prevent="onSubmit"
      novalidate
      class="locations-edit-form"
    >
      <HeadlineSection
        :text="selectedLocation.location"
        data-test="headline"
      />
      <div class="inputs">
        <div class="row">
          <FeatherInput
            label="Location Name *"
            v-model="inputs.name"
            :schema="nameV"
            required
            class="input-name"
            data-test="input-name"
          >
            <template #pre><FeatherIcon :icon="icons.Location" /></template
          ></FeatherInput>
        </div>
        <div class="row">
          <FeatherInput
            label="Longitude (optional)"
            v-model="inputs.longitude"
            class="input-longitude"
            data-test="input-longitude"
          >
            <template #pre><FeatherIcon :icon="icons.placeholder" /></template
          ></FeatherInput>
          <FeatherInput
            label="Latitude (optional)"
            v-model="inputs.latitude"
            class="input-latitude"
            data-test="input-latitude"
          >
            <template #pre><FeatherIcon :icon="icons.placeholder" /></template
          ></FeatherInput>
        </div>
      </div>
      <FooterSection
        :save="saveBtn"
        data-test="save-button"
      />
    </form>
  </div>
</template>

<script setup lang="ts">
import Location from '@featherds/icon/action/Location'
import placeholder from '@/assets/placeholder.svg'
import { string } from 'yup'
import { useForm } from '@featherds/input-helper'
import { Location as LocationType } from '@/types/graphql'
import { useLocationsStore } from '@/store/Views/locationsStore'

const props = defineProps<{
  id: number
}>()

const locationsStore = useLocationsStore()

const selectedLocation = computed(() => locationsStore.locationsList.filter((l: LocationType) => l.id === props.id)[0])

const inputs = reactive({
  name: selectedLocation.value.location,
  longitude: '',
  latitude: ''
})

const form = useForm()
const nameV = string().required('Location name is required.')

const onSubmit = () => {
  const formInvalid = form.validate().length > 0 // array of errors

  if (formInvalid) return

  console.log('call api endpoint to save form...', inputs)
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
