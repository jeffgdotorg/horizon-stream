import { defineStore } from 'pinia'
import { useLocationsQueries } from '../Queries/locationsQueries'

export const useLocationsStore = defineStore('locationsStore', () => {
  const locationsList = ref()
  const minionsList = ref()
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

  const searchLocations = async (searchTerm = '') => {
    try {
      const locations = await locationsQueries.searchLocation(searchTerm)

      locationsList.value = locations?.data?.value?.searchLocation || []
    } catch (err) {
      locationsList.value = []
    }
  }

  const fetchMinions = async () => {
    try {
      // const minions = await locationsQueries.fetchMinions()
      // minionsList.value = minions?.data?.value?.findAllMinions || []
      minionsList.value = [
        {
          id: 1,
          name: 'minion0',
          version: 'v.0.0.0',
          latency: '000m',
          status: 'UP',
          utillization: '00%',
          ip: '000.000.000.000',
          contextMenu: [
            { label: 'edit', handler: () => ({}) },
            { label: 'delete', handler: () => ({}) }
          ]
        },
        {
          id: 2,
          name: 'minion9',
          version: 'v.9.9.9',
          latency: '999m',
          status: 'DOWN',
          utillization: '99%',
          ip: '999.999.999.999',
          contextMenu: [
            { label: 'edit', handler: () => ({}) },
            { label: 'delete', handler: () => ({}) }
          ]
        }
      ]
    } catch (err) {
      minionsList.value = []
    }
  }

  const searchMinions = async (searchTerm = '') => {
    // const minions = await locationsQueries.searchMinion(searchTerm)
    // minionsList.value = minions?.data?.value?.searchLocation || []
  }

  const selectLocation = (id: number) => {
    selectedLocationId.value = id
  }

  return {
    locationsList,
    fetchLocations,
    selectedLocationId,
    selectLocation,
    searchLocations,
    minionsList,
    fetchMinions,
    searchMinions
  }
})
