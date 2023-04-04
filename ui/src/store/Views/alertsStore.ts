import { defineStore } from 'pinia'
import { TimeRange } from '@/types/graphql'
import { useAlertsQueries } from '../Queries/alertsQueries'
import { useAlertsMutations } from '../Mutations/alertsMutations'
import { Alert } from '@/types/graphql'
import { AlertsFilters } from '@/types/alerts'

const alertsFilterDefault: AlertsFilters = {
  timeRange: TimeRange.All,
  pagination: {
    page: 0, // api has base of 0 (first page)
    pageSize: 10
  },
  // search: '', // not avail for EAR
  severities: [],
  sortAscending: true,
  // sortBy: 'lastUpdateTimeMs'
  sortBy: 'alertId'
}

const alertsPaginationDefault = {
  page: 1, // pagination component has base of 1 (first page)
  pageSize: 10,
  total: 0
}

export const useAlertsStore = defineStore('alertsStore', () => {
  const alertsList = ref()
  const alertsFilter = ref(alertsFilterDefault)
  const alertsPagination = ref(alertsPaginationDefault)
  const alertsSelected = ref([] as number[] | undefined)
  const alertsListSearched = ref([]) // TODO: not avail for EAR

  const alertsQueries = useAlertsQueries()
  const alertsMutations = useAlertsMutations()

  const fetchAlerts = async () => {
    await alertsQueries.fetchAlerts(alertsFilter.value)

    alertsList.value = alertsQueries.fetchAlertsData

    alertsPagination.value = {
      ...alertsPagination.value,
      total: alertsList.value.totalAlerts
    }
  }

  watch(
    alertsFilter,
    () => {
      fetchAlerts()
      // setPage(1) // TODO go to 1st page on filters change
    },
    { deep: true }
  )

  const toggleSeverity = (selected: string): void => {
    const exists = alertsFilter.value.severities?.some((s) => s === selected)

    if (exists) {
      alertsFilter.value = {
        ...alertsFilter.value,
        severities: alertsFilter.value.severities?.filter((s) => s !== selected)
      }

      if (!alertsFilter.value.severities?.length) alertsFilter.value = { ...alertsFilter.value, severities: [] }
    } else {
      alertsFilter.value = {
        ...alertsFilter.value,
        severities: [...(alertsFilter.value.severities as string[]), selected]
      }
    }
  }

  const selectTime = (selected: TimeRange): void => {
    alertsFilter.value = {
      ...alertsFilter.value,
      timeRange: selected
    }
  }

  const setPage = (page: number): void => {
    const apiPage = page - 1 // pagination component has base of 1; hence first page is 1 - 1 = 0 as api payload

    if (apiPage !== Number(alertsFilter.value.pagination.page)) {
      alertsFilter.value = {
        ...alertsFilter.value,
        pagination: {
          ...alertsFilter.value.pagination,
          page: apiPage
        }
      }
    }
  }

  const setPageSize = (pageSize: number): void => {
    if (pageSize !== alertsFilter.value.pagination.pageSize) {
      alertsFilter.value = {
        ...alertsFilter.value,
        pagination: {
          page: 0,
          pageSize
        }
      }

      alertsPagination.value = {
        ...alertsPagination.value,
        page: 1,
        pageSize
      }
    }
  }

  const clearAllFilters = (): void => {
    alertsFilter.value = {
      timeRange: TimeRange.All,
      pagination: {
        page: 0,
        pageSize: 10
      },
      // search: '', // not avail for EAR
      severities: [],
      sortAscending: true,
      sortBy: 'alertId'
    }

    alertsPagination.value = alertsPaginationDefault
  }

  // all toggle (select/deselect): true/false / individual toggle: number (alert id)
  const setAlertsSelected = (selectedAlert: number | boolean) => {
    if (Number.isInteger(selectedAlert)) {
      const found = alertsSelected.value?.indexOf(selectedAlert as number) as number
      found === -1 ? alertsSelected.value?.push(selectedAlert as number) : alertsSelected.value?.splice(found, 1)
    } else {
      if (!selectedAlert) alertsSelected.value = []
      else {
        alertsSelected.value = alertsList.value.alerts.map((al: Alert) => al.databaseId)
      }
    }
  }

  const clearSelectedAlerts = async () => {
    await alertsMutations.clearAlerts({ ids: alertsSelected.value })

    fetchAlerts()
  }

  const acknowledgeSelectedAlerts = async () => {
    await alertsMutations.acknowledgeAlerts({ ids: alertsSelected.value })

    fetchAlerts()
  }

  return {
    alertsList,
    fetchAlerts,
    alertsFilter,
    toggleSeverity,
    selectTime,
    alertsPagination,
    setPage,
    setPageSize,
    clearAllFilters,
    setAlertsSelected,
    clearSelectedAlerts,
    acknowledgeSelectedAlerts
  }
})
