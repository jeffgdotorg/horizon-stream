import { defineStore } from 'pinia'
import { useLocationsQueries } from '../Queries/locationsQueries'

export const useLocationsStore = defineStore('locationsStore', () => {
  const locationsList = ref()
  const selectedLocationId = ref()

  const locationsQueries = useLocationsQueries()

  const fetchLocations = async () => {
    try {
      const locations = await locationsQueries.fetchLocations()

      locationsList.value = locations?.data?.value?.findAllLocations || []
    } catch (err) {
      locationsList.value = []
    }
  }

  const searchLocation = async (searchTerm = '') => {
    const { data } = await locationsQueries.searchLocation(searchTerm)

    locationsList.value = data.value?.searchLocation || []
  }

  const selectLocation = (id: number) => {
    selectedLocationId.value = id
  }

  return {
    locationsList,
    fetchLocations,
    searchLocation,
    selectedLocationId,
    selectLocation
  }
})
