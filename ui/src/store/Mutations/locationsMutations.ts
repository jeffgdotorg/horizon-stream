/* import { defineStore } from 'pinia'
import { useMutation } from 'villus'

export const useLocationsMutations = defineStore('locationsMutations', () => {
  const { execute: acknowledgeAlerts, error: acknowledgeAlertsError } = useMutation(AcknowledgeAlertsDocument)

  const { execute: clearAlerts, error: clearAlertsError } = useMutation(ClearAlertsDocument)

  return {
    acknowledgeAlerts,
    acknowledgeAlertsError,
    clearAlerts,
    clearAlertsError
  }
})
 */
