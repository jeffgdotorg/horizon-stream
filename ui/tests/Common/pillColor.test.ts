import { shallowMount } from '@vue/test-utils'
import PillColor from '@/components/Common/PillColor.vue'

let wrapper: any

describe('PillColor', () => {
  beforeAll(() => {
    wrapper = shallowMount(PillColor, {
      props: {
        item: { type: 'CRITICAL' }
      }
    })
  })

  test('Mount', () => {
    expect(wrapper).toBeTruthy()
  })

  test('Should have a pill color', () => {
    const elem = wrapper.get('[data-test="pill-style"]')
    expect(elem.exists()).toBeTruthy()
  })
})
