<template>
  <NavigationMenu/>
  <PageDiv>
    <div class="configuration-table-container">
      <h1 style="font-family: 'Merriweather', serif; font-weight: bold">Configure</h1>
      <h3 style="font-family: 'Merriweather', serif; font-weight: bold">Controllers</h3>
      <div class="configuration-button-add configuration-button" v-on:click="getControllers">
        <p>+</p>
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
              <button v-on:click="removeController(index)">Delete</button>
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
      controllers: [ { id: 1, data: 'data'}, {id: 2, data: 'data2'}]
    }
  },
  methods: {
    getCurrentHost() {
      //return window.location.host
      return 'localhost:8450'
    },

    compareControllers(a, b) {
      if (a.id < b.id) {
        return -1
      } else if (a.id > b.id) {
        return 1
      } else {
        return 0
      }
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

    async removeController(index) {
      let controller = this.controllers.at(index)
      let url = 'http://' + this.getCurrentHost() + '/api/conf/mapping/controller/' + controller.id

      let response = await fetch(url, {method: 'DELETE'})
      if (response.status < 200 && response.status > 300) {
        let responseData = await this.fetchResponseJson(response)
        this.logErrorResponse(response.status, responseData)
      }

      await this.getControllers()
    }
  },
  mounted: function() {
    this.getControllers()
  }
}
</script>

<style scoped>
.configuration-table-container {
  padding-top: 5%;
}

.configuration-table {
  border-collapse: collapse;
  width: 100%;
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

.configuration-button-add {
  background-color: #4f452b;
}

.configuration-button{
  float: right;
  height: 1.8em;
  width: 1.8em;
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 0.4em;
  margin-bottom: 1%;
}

.configuration-button p {
  margin: 0;
  padding: 0;
  line-height: 1em;
  text-align: center;
  color: white;
  font-size: 1.8em;
  font-family: 'Merriweather', serif;
}
</style>