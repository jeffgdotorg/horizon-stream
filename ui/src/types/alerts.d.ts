import { Alert } from './graphql'
import { TimeRange } from '@/types/graphql'

interface IAlert extends Alert {
  isSelected?: boolean
  label?: string
  nodeType?: string
}

interface Pagination {
  page: number
  pageSize?: number
}

interface AlertsFilters {
  timeRange: TimeRange //number
  pagination: Pagination
  search?: string
  severities?: string[]
  sortAscending: boolean
  sortBy?: string
}