import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { CreateLocationDocument, UpdateLocationDocument, DeleteLocationDocument } from '@/types/graphql'

export const useLocationMutations = defineStore('locationMutations', () => {
  const createLocation = (name: string) => {}
})
