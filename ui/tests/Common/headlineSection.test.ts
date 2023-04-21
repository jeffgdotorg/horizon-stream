import { shallowMount } from '@vue/test-utils'
import HeadlineSection from '@/components/Common/HeadlineSection.vue'

const defaultHeading = 'Section Headline'
const text = 'New Headline'

let wrapper: any

describe('HeadLineSection', () => {
  afterAll(() => {
    wrapper.unmount()
  })

  it(`should have ${defaultHeading} as heading`, () => {
    wrapper = shallowMount(HeadlineSection)

    const componentHeading = wrapper.get('[data-test="headline"]').text()
    expect(componentHeading).toBe(defaultHeading)
  })

  it(`should have ${text} as heading`, () => {
    wrapper = shallowMount(HeadlineSection, {
      propsData: {
        text
      }
    })

    const componentHeading = wrapper.get('[data-test="headline"]').text()
    expect(componentHeading).toBe(text)
  })

  it('should have rendered the slots', () => {
    wrapper = shallowMount(HeadlineSection, {
      slots: {
        infos: '<div>some infos</div>',
        actions: '<div>some actions</div>'
      },
      attachTo: document.body
    })

    let slot = wrapper.get('[data-test="infos"]')
    expect(slot.html()).toContain('some infos')

    slot = wrapper.get('[data-test="actions"]')
    expect(slot.html()).toContain('some actions')
  })
})
