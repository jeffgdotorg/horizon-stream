import { defineConfig } from 'cypress'
// import cypressViteConfig from './cypress.vite.config'
import viteConfig from './vite.config'
import { loadEnv } from 'vite'

const env = loadEnv('development', process.cwd())
console.log(env)

export default defineConfig({
  component: {
    specPattern: 'cypress/component/**/*.cy.*.{js,jsx,ts,tsx}',
    devServer: {
      framework: 'vue',
      bundler: 'vite',
      viteConfig
      // viteConfig: cypressViteConfig
    }
  },

  e2e: {
    // baseUrl: 'https://onmshs',
    baseUrl: env.VITE_CYPRESS_BASE_URL, // local
    specPattern: 'cypress/e2e/**/*.cy.*.{js,jsx,ts,tsx}',
    setupNodeEvents(on, config) {
      // implement node event listeners here
    }
  }
})