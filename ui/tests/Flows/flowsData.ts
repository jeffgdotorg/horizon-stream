import { TimeRange, RequestCriteriaInput } from '@/types/graphql'
import { IAutocompleteItemType } from '@featherds/autocomplete'
import { ChartData } from 'chart.js'

export const FlowsState = {
  tableDatasets: [{} as any],
  lineDatasets: [{} as any],
  tableChartOptions: {},
  totalFlows: '1,957',
  filters: {
    dateFilter: TimeRange.Today,
    traffic: {
      selectedItem: 'total'
    },
    dataStyle: {
      selectedItem: 'table'
    },
    //Application AutoComplete
    applications: [] as IAutocompleteItemType[],
    selectedApplications: [],
    isApplicationsLoading: false,
    filteredApplications: [] as IAutocompleteItemType[],
    //Exporter AutoComplete
    exporters: [] as IAutocompleteItemType[],
    selectedExporters: [],
    isExportersLoading: false,
    filteredExporters: [] as IAutocompleteItemType[]
  },
  applications: {
    isTableLoading: false,
    isLineLoading: false,
    tableChartData: {} as ChartData,
    lineChartData: {} as ChartData,
    expansionOpen: true,
    filterDialogOpen: false
  },
  exporters: {
    isLoading: false,
    tableChartData: {} as ChartData,
    lineChartData: {} as ChartData,
    expansionOpen: true,
    filterDialogOpen: false
  },
  requestCriteria: {
    count: 10,
    step: 2000000,
    timeRange: { startTime: 0, endTime: 0 },
    applications: [] as string[],
    exporters: []
  } as RequestCriteriaInput
}

export const TableGraphData = [
  {
    label: 'ms-wbt-server',
    bytesIn: 1406435,
    bytesOut: 1335483
  },
  {
    label: 'http',
    bytesIn: 1323685,
    bytesOut: 1458696
  },
  {
    label: 'bootpc',
    bytesIn: 1478831,
    bytesOut: 1345173
  },
  {
    label: 'isakmp',
    bytesIn: 1240613,
    bytesOut: 1306114
  },
  {
    label: 'ldap',
    bytesIn: 1556039,
    bytesOut: 1175289
  },
  {
    label: 'submission',
    bytesIn: 1282610,
    bytesOut: 1405345
  },
  {
    label: 'ipsec-nat-t',
    bytesIn: 1422966,
    bytesOut: 1286268
  },
  {
    label: 'imap',
    bytesIn: 1425064,
    bytesOut: 1268231
  },
  {
    label: 'https',
    bytesIn: 1426670,
    bytesOut: 1275992
  },
  {
    label: 'ssh',
    bytesIn: 1416432,
    bytesOut: 1300807
  }
]

export const LineGraphData = [
  {
    label: 'app0',
    data: [
      {
        timestamp: '2023-01-10T01:01:25Z',
        value: 138790.1750375429,
        direction: 'EGRESS'
      },
      {
        timestamp: '2023-01-10T01:09:47Z',
        value: 216861.9119974472,
        direction: 'EGRESS'
      },
      {
        timestamp: '2023-01-10T01:18:07Z',
        value: 202966.96568806906,
        direction: 'EGRESS'
      },
      {
        timestamp: '2023-01-10T01:26:27Z',
        value: 264173.0346710393,
        direction: 'EGRESS'
      }
    ]
  },
  {
    label: 'app1',
    data: [
      {
        timestamp: '2023-01-10T01:01:25Z',
        value: 138790.1750375429,
        direction: 'EGRESS'
      },
      {
        timestamp: '2023-01-10T01:09:47Z',
        value: 216861.9119974472,
        direction: 'EGRESS'
      },
      {
        timestamp: '2023-01-10T01:18:07Z',
        value: 202966.96568806906,
        direction: 'EGRESS'
      },
      {
        timestamp: '2023-01-10T01:26:27Z',
        value: 264173.0346710393,
        direction: 'EGRESS'
      }
    ]
  }
]
