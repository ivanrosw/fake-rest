/* eslint-disable */
import { createApp } from 'vue'
import * as VueRouter from 'vue-router'

import App from './App.vue'
import HomePage from './components/HomePage.vue'
import ConfigurePage from './components/ConfigurePage.vue'
import SendPage from './components/SendPage.vue'

const routes = [
    { path: '/', component: HomePage},
    { path: '/configure', component: ConfigurePage},
    { path: '/send', component: SendPage}
]
const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes
})

createApp(App)
    .use(router)
    .mount('#app')

router.beforeEach( (to) => {
    let backgroundClass = to.matched[0].components.default.data?.().bodyBackgroundClass
    if (backgroundClass) {
        window.document.body.className = backgroundClass;
    } else {
        window.document.body.className = '';
    }
})
