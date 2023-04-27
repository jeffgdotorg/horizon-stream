import { defineStore } from 'pinia'
import { useLocationQueries } from '../Queries/locationQueries'
import { DisplayType } from '@/types/locations.d'
import { useLocationMutations } from '../Mutations/locationMutations'

export const useLocationStore = defineStore('locationStore', () => {
  const locationsList = ref()
  const minionsList = ref()
  const selectedLocationId = ref()
  const displayType = ref(DisplayType.LIST)

  const saveIsFetching = ref()

  const locationQueries = useLocationQueries()
  const locationMutations = useLocationMutations()

  const fetchLocations = async () => {
    try {
      const locations = await locationQueries.fetchLocations()

      locationsList.value = locations?.data?.value?.findAllLocations || []
    } catch (err) {
      locationsList.value = []
    }
  }

  const searchLocations = async (searchTerm = '') => {
    try {
      const locations = await locationQueries.searchLocation(searchTerm)

      locationsList.value = locations?.data?.value?.searchLocation || []
    } catch (err) {
      locationsList.value = []
    }
  }

  const fetchMinions = async () => {
    try {
      // const minions = await locationQueries.fetchMinions()
      // minionsList.value = minions?.data?.value?.findAllMinions || []
      minionsList.value = [
        {
          id: 1,
          name: 'minion1',
          version: 'v.1.1.1',
          latency: '111ms',
          status: 'UP',
          utillization: '11%',
          ip: '111.111.111.111',
          contextMenu: [
            { label: 'edit', handler: () => ({}) },
            { label: 'delete', handler: () => ({}) }
          ]
        },
        {
          id: 2,
          name: 'minion3',
          version: 'v.3.3.3',
          latency: '333ms',
          status: 'DOWN',
          utillization: '33%',
          ip: '333.333.333.333',
          contextMenu: [
            { label: 'edit', handler: () => ({}) },
            { label: 'delete', handler: () => ({}) }
          ]
        },
        {
          id: 3,
          name: 'minion9',
          version: 'v.9.9.9',
          latency: '999ms',
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
    // const minions = await locationQueries.searchMinion(searchTerm)
    // minionsList.value = minions?.data?.value?.searchLocation || []
  }

  const selectLocation = (id: number | undefined) => {
    if (id) displayType.value = DisplayType.EDIT

    selectedLocationId.value = id
  }

  const setDisplayType = (type: DisplayType) => {
    displayType.value = type
  }

  const createLocation = async (name) => {
    saveIsFetching.value = true

    const error = await locationMutations.createLocation(name)

    saveIsFetching.value = false

    if (!error.value) {
      fetchLocations()
    }

    return !error.value
  }

  return {
    displayType,
    setDisplayType,
    locationsList,
    fetchLocations,
    selectedLocationId,
    selectLocation,
    searchLocations,
    minionsList,
    fetchMinions,
    searchMinions,
    createLocation,
    saveIsFetching
  }
})
