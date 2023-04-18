import { mount } from '@vue/test-utils'
import PillColor from '@/components/Common/PillColor.vue'

let wrapper: any

describe('PillColor', () => {
  beforeAll(() => {
    wrapper = mount(PillColor, {
      props: {
        type: 'CRITICAL'
      }
    })
  })

  test('Mount', () => {
    expect(wrapper).toBeTruthy()
  })

  test('Should have a pill color', () => {
    const elem = wrapper.get('[data-test="pill-type"]')
    expect(elem.exists()).toBeTruthy()
  })
})
