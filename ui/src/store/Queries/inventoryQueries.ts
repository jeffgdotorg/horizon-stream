import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { 
  NodesListDocument,
  NodeLatencyMetricDocument,
  TsResult,
  IpInterface,
  Location
} from '@/types/graphql'
import { NodeContent } from '@/types/inventory'
import useSpinner from '@/composables/useSpinner'
import { TimeUnit } from '@/types'

export const useInventoryQueries = defineStore('inventoryQueries', () => {
  const nodes = ref<NodeContent[]>([])
  const metricsNodes = ref<NodeContent[]>([])
  
  const { startSpinner, stopSpinner } = useSpinner()

  const { data: nodesData, isFetching: nodesFetching, execute } = useQuery({
    query: NodesListDocument,
    cachePolicy: 'network-only' // always fetch and do not cache
  })

  const fetchNodeMetrics = async (nodeId: number) => {
    return await useQuery({
      query: NodeLatencyMetricDocument,
      variables: { id: nodeId },
      cachePolicy: 'network-only' // always fetch and do not cache
    })
  }

  watchEffect(async () => {
    nodesFetching.value ? startSpinner() : stopSpinner()

    const allNodes = nodesData.value?.findAllNodes

    if(allNodes?.length) {
      allNodes.forEach(async ({id, nodeLabel, location, ipInterfaces}) => {
        const {data, isFetching} = await fetchNodeMetrics(id)

        if(data.value && !isFetching.value) {
          const [{metric, value}] = data.value.nodeLatency?.data?.result as TsResult[]
          const [uptime, latency] = value as number[]
          const {location: nodeLocation} = location as Location
          const [{ipAddress}] = ipInterfaces as IpInterface[]
        
          metricsNodes.value.push({
            id: id,
            label: nodeLabel,
            status: '',
            metrics: [
              {
                type: 'latency',
                label: 'Latency',
                timestamp: latency || '--',
                status: ''
              },
              {
                type: 'uptime',
                label: 'Uptime',
                timestamp: uptime || '--',
                timeUnit: TimeUnit.MSecs,
                status: ''
              },
              {
                type: 'status',
                label: 'Status',
                status: metric.status || '--'
              }
            ],
            anchor: {
              profileValue: '--',
              profileLink: '',
              locationValue: nodeLocation || '--',
              locationLink: '',
              ipAddressValue: ipAddress || '',
              ipAddressLink: '',
              tagValue: '--',
              tagLink: ''
            }  
          })
        }
      })

      nodes.value = metricsNodes.value
    }
  })

  return { 
    nodes,
    fetch: execute
  }
})
