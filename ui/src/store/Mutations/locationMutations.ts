import { defineStore } from 'pinia'
import { useMutation } from 'villus'
import { CreateLocationDocument, UpdateLocationDocument, DeleteLocationDocument } from '@/types/graphql'

export const useLocationMutations = defineStore('locationMutations', () => {
  const createLocation = async (name) => {
    const { execute, error } = useMutation(CreateLocationDocument)

    await execute({ location: name })

    return error
  }

  return { createLocation }
})
