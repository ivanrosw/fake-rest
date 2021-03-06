<template>
  <NavigationMenu/>
  <PageDiv>
    <div class="configuration-table-container">
      <h1 class="header">Configure</h1>

      <!-- Controllers -->
      <div class="flex-form">
        <h3 class="header">Controllers</h3>
        <div class="configuration-button-add configuration-button" v-on:click="confControllerVisible = !confControllerVisible;
                                                                               cId = -1">
          <p>+</p>
        </div>
        <form class="add-config-form" @submit="submitController" v-if="confControllerVisible">
          <div class="configuration-button" v-on:click="confControllerVisible = !confControllerVisible">
            <p>x</p>
          </div>

          <p>Method</p>
          <select v-model="cMethod">
            <option>GET</option>
            <option>POST</option>
            <option>PUT</option>
            <option>DELETE</option>
          </select>

          <p>Mode</p>
          <select v-model="cMode">
            <option>Static</option>
            <option>Dynamic</option>
          </select>

          <p>Uri</p>
          <input v-if="cMode === 'Static'" type="text" v-model="cUri" placeholder="/example"/>
          <input v-if="cMode === 'Dynamic'" type="text" v-model="cUri" placeholder="/example/{id-name}/{id-name2}"
                 v-on:input="calculateControllerIdsFromUri"/>

          <p>Answer</p>
          <input type="text" v-model="cAnswer" placeholder="Enter static answer"/>

          <div v-if="cMethod === 'POST'">
            <p>Generate id</p>
            <select v-model="cGenerateId">
              <option :value="true">Yes</option>
              <option :value="false">No</option>
            </select>
            <div v-if="cGenerateId === true">
              <p>Id patterns</p>
              <table class="configuration-table">
                <thead>
                <th>Id</th>
                <th>Pattern</th>
                </thead>
                <tbody v-for="(id, index) in cIdParams" v-bind:key="index">
                <td>{{ id }}</td>
                <td>
                  <select v-model="cIdPatterns[index]">
                    <option>UUID</option>
                    <option>SEQUENCE</option>
                  </select>
                </td>
                </tbody>
              </table>
            </div>
          </div>

          <p>Delay ms</p>
          <input type="number" v-model="cDelayMs" placeholder="0"/>
          <br>
          <input type="submit" value="Submit">
        </form>
      </div>

      <table class="configuration-table">
        <thead>
          <tr>
            <th>Method</th>
            <th>Uri</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(controller, index) in controllers" :key="index">
            <td>{{ controller.method }}</td>
            <td>{{ controller.uri }}</td>
            <td>
              <div class="flex-form">
                <div class="configuration-button" v-on:click="updateController(index)">
                  <p>u</p>
                </div>
                <div class="configuration-button" v-on:click="removeController(index)">
                  <p>x</p>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Routers -->
      <div class="flex-form">
        <h3 class="header">Routers</h3>
        <div class="configuration-button-add configuration-button" v-on:click="confRouterVisible = !confRouterVisible;
                                                                               rId = -1">
          <p>+</p>
        </div>
        <form class="add-config-form" @submit="submitRouter" v-if="confRouterVisible">
          <div class="configuration-button" v-on:click="confRouterVisible = !confRouterVisible">
            <p>x</p>
          </div>

          <p>Method</p>
          <select v-model="rMethod">
            <option>GET</option>
            <option>POST</option>
            <option>PUT</option>
            <option>DELETE</option>
          </select>

          <p>Uri</p>
          <input type="text" v-model="rUri" placeholder="/example/"/>
          <p>To Url</p>
          <input type="text" v-model="rToUrl" placeholder="/example"/>
          <br>
          <input type="submit" value="Submit">
        </form>
      </div>

      <table class="configuration-table">
        <thead>
        <tr>
          <th>Method</th>
          <th>Uri</th>
          <th>To Url</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(router, index) in routers" :key="index">
          <td>{{ router.method }}</td>
          <td>{{ router.uri }}</td>
          <td>{{ router.toUrl }}</td>
          <td>
            <div class="flex-form">
              <div class="configuration-button" v-on:click="updateRouter(index)">
                <p>u</p>
              </div>
              <div class="configuration-button" v-on:click="removeRouter(index)">
                <p>x</p>
              </div>
            </div>
          </td>
        </tr>
        </tbody>
      </table>

    </div>
  </PageDiv>
</template>

<script>
import NavigationMenu from "@/components/NavigationMenu";
import PageDiv from "@/components/PageDiv";

export default {
  name: "ConfigurePage",
  components: {PageDiv, NavigationMenu},
  data() {
    return {
      bodyBackgroundClass: 'pages-body',
      controllers: [],
      routers: [],
      confControllerVisible: false,
      confRouterVisible: false,

      cId: -1,
      cMethod: 'GET',
      cMode: 'Static',
      cUri: '',
      cAnswer: '',
      cDelayMs: 0,
      cGenerateId: true,
      cIdParams: [],
      cIdPatterns: [],

      rId: -1,
      rUri: '',
      rMethod: 'GET',
      rToUrl: ''
    }
  },
  methods: {
    getCurrentHost() {
      return window.location.host
    },

    compareControllers(a, b) {
      if (a.uri === b.uri) {
        return a.id < b.id ? -1 : 1
      } else {
        return a.uri < b.uri ? -1 : 1
      }
    },

    compareRouters(a, b) {
      return a.id < b.id ? -1 : 1
    },

    findIndexById(id, collection) {
      if (collection) {
        for (let i = 0; i < collection.length; i++) {
          let config = collection[i]
          if (config.id === id) {
            return i
          }
        }
      }
    },

    setDefaultControllerValues() {
      this.cId = -1
      this.cMethod = 'GET'
      this.cMode = 'Static'
      this.cUri = ''
      this.cAnswer = ''
      this.cDelayMs = 0
      this.cGenerateId = true
      this.cIdParams = []
      this.cIdPatterns = []
    },

    setDefaultRouterValues() {
      this.rId = -1
      this.rMethod = 'GET'
      this.rUri =''
      this.rToUrl = ''
    },

    logErrorResponse(status, responseData) {
      if (responseData) {
        alert(responseData.description)
      } else {
        alert('Something went wrong ' + status)
      }
    },

    async fetchResponseJson(response) {
      let responseData = null
      try {
        responseData = await response.json()
      } catch (e) {
        console.log(e)
      }
      return responseData
    },

    calculateControllerIdsFromUri() {
      if (this.cUri && this.cUri.length > 0) {
        this.cIdParams = this.cUri.match(new RegExp('(?<=\\{)[\\w]*(?=\\})', 'g'))
        if (!this.cIdParams) {
          this.cIdParams = []
        }

        let checkDuplicatesArray = []
        for (let i = 0; i < this.cIdParams.length; i++) {
          let id = this.cIdParams[i]
          if (checkDuplicatesArray.includes(id)) {
            alert('Uri has duplicate ids, fix it please')
            this.cIdParams = []
            return
          } else {
            checkDuplicatesArray.push(id)
          }
        }

        for (let i = 0; i < this.cIdParams.length; i++) {
          this.cIdPatterns[i] = 'UUID'
        }
      }
    },

    async getControllers() {
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/controller'
      let response = await fetch(url, {method: 'GET'})
      let responseData = await this.fetchResponseJson(response)

      if (response.status >= 200 && response.status < 300) {
        this.controllers = responseData
        this.controllers.sort(this.compareControllers)
      } else {
        this.logErrorResponse(response.status, responseData)
      }
    },

    async submitController() {
      let idParamsPatterns = new Map()
      if (this.cId && this.cId >= 0) {
        let controllerIndex = this.findIndexById(this.cId, this.controllers)
        await this.removeController(controllerIndex)
      }

      for (let i = 0; i < this.cIdParams.length; i++) {
        idParamsPatterns.set(this.cIdParams[i], this.cIdPatterns[i])
      }

      let controller = {
        uri: this.cUri,
        method: this.cMethod,
        delayMs: this.cDelayMs,
        idParams: this.cIdParams,
        generateId: this.cGenerateId,
        generateIdPatterns: idParamsPatterns
      }
      if (this.cAnswer && this.cAnswer.length > 0) {
        controller.answer = this.cAnswer
      }

      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/controller'
      let response = await fetch(url,
          {method: 'POST',
            body: JSON.stringify(controller),
            headers: {
              'Content-Type': 'application/json'
            }})
      if (response.status < 200 || response.status >= 300) {
        let responseData = await this.fetchResponseJson(response)
        this.cId = -1
        this.logErrorResponse(response.status, responseData)
      } else {
        this.confControllerVisible = false
        this.setDefaultControllerValues()
        await this.getControllers()
      }
    },

    async updateController(index) {
      let controller = this.controllers.at(index)
      if (!controller) {
        alert('Something went wrong')
        return
      }

      this.cId = controller.id
      this.cMethod = controller.method
      this.cUri = controller.uri
      this.calculateControllerIdsFromUri()
      if (this.cIdParams && this.cIdParams.length > 0) {
        this.cMode = 'Dynamic'
      } else {
        this.cMode = 'Static'
      }
      this.cAnswer = controller.answer
      this.cDelayMs = controller.delayMs
      this.cGenerateId = controller.generateId

      let idParamsPatterns = controller.generateIdPatterns
      if (idParamsPatterns && idParamsPatterns.size > 0) {
        this.cIdParams = idParamsPatterns.keys()
        this.cIdPatterns = idParamsPatterns.values()
      }
      this.confControllerVisible = true
    },

    async removeController(index) {
      let controller = this.controllers.at(index)
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/controller/' + controller.id

      let response = await fetch(url, {method: 'DELETE'})
      if (response.status < 200 || response.status > 300) {
        let responseData = await this.fetchResponseJson(response)
        this.logErrorResponse(response.status, responseData)
      }

      await this.getControllers()
    },

    async getRouters() {
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/router'
      let response = await fetch(url, {method: 'GET'})
      let responseData = await this.fetchResponseJson(response)

      if (response.status >= 200 && response.status < 300) {
        this.routers = responseData
        this.routers = this.routers.sort(this.compareRouters)
      } else {
        this.logErrorResponse(response.status, responseData)
      }
    },

    async submitRouter() {
      if (this.rId && this.rId >= 0) {
        let routerIndex = this.findIndexById(this.rId, this.routers)
        await this.removeRouter(routerIndex)
      }

      let router = {
        uri: this.rUri,
        method: this.rMethod,
        toUrl: this.rToUrl
      }

      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/router'
      let response = await fetch(url,
          {method: 'POST',
            body: JSON.stringify(router),
            headers: {
              'Content-Type': 'application/json'
            }})
      if (response.status < 200 || response.status >= 300) {
        let responseData = await this.fetchResponseJson(response)
        this.rId = -1
        this.logErrorResponse(response.status, responseData)
      } else {
        this.confRouterVisible = false
        this.setDefaultRouterValues()
        await this.getRouters()
      }
    },

    async updateRouter(index) {
      let router = this.routers.at(index)
      if (!router) {
        alert('Something went wrong')
        return
      }

      this.rId = router.id
      this.rMethod = router.method
      this.rUri = router.uri
      this.rToUrl = router.toUrl

      this.confRouterVisible = true
    },

    async removeRouter(index) {
      let router = this.routers.at(index)
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/router/' + router.id

      let response = await fetch(url, {method: 'DELETE'})
      if (response.status < 200 || response.status > 300) {
        let responseData = await this.fetchResponseJson(response)
        this.logErrorResponse(response.status, responseData)
      }

      await this.getRouters()
    },

  },

  mounted: function() {
    this.getControllers()
    this.getRouters()
    this.setDefaultControllerValues()
  }
}
</script>

<style scoped>
.header {
  font-family: 'Merriweather', serif;
  font-weight: bold
}

.configuration-table-container {
  padding-top: 5%;
}

.flex-form {
  display: flex;
  justify-content: space-between;
}

.configuration-table {
  border-collapse: collapse;
  width: 100%;
}

.configuration-button-add {
  margin-top: 1.5em;
}

.configuration-button-add p {
  line-height: 3vh !important;
}

.configuration-table td, .configuration-table th {
  border: 1px solid #ddd;
  padding: 8px;
}

.configuration-table tr:nth-child(even){background-color: #f2f2f2;}

.configuration-table tr:hover {background-color: #ddd;}

.configuration-table th {

  text-align: left;
  background-color: #4f452b;
  color: white;
  font-family: 'Merriweather', serif;
  font-weight: normal;
}

.configuration-table td {
  text-align: left;
  font-family: 'Lato', sans-serif;
  font-weight: lighter;
}

.configuration-table td:last-child {
  width: 1%;
}

.configuration-button{
  background-color: #4f452b;
  transition: background-color 600ms ease-out 100ms;
  float: right;
  height: 3vh;
  width: 3vh;
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 0.4em;
  cursor: pointer;
}

.configuration-button:hover {
  background-color: rgba(79, 69, 43, 0.6);
}

.configuration-button p {
  margin: 0;
  padding: 0;
  line-height: 2.5vh;
  text-align: center;
  color: white;
  font-size: 3vh;
  font-family: 'Lato', sans-serif;
}

.add-config-form {
  position: fixed;
  width: 63.5vw;
  left: 17vw;
  background: #9c8958;
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 0.4em;
  color: white;
  font-family: 'Lato', sans-serif;
  font-size: 1.4vh;
  padding: 0.2vh 1vw 0.5vh 1vw;
}

.add-config-form p {
  margin-bottom: 0;
}
</style>