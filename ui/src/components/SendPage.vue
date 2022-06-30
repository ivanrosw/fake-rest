<template>
  <NavigationMenu/>
  <PageDiv>
    <div class="send-container">
      <h1 class="header">Send</h1>
      <form class="send-form" @submit="sendRequest">
        <div class="flex-form">
          <p>Url</p>
          <input type="text" v-model="requestUrl" placeholder="http://localhost:8080/example"/>
          <input type="submit" value="Send">
        </div>

        <p>Method</p>
        <select v-model="requestMethod">
          <option>GET</option>
          <option>POST</option>
          <option>PUT</option>
          <option>DELETE</option>
          <option>HEAD</option>
          <option>PATCH</option>
          <option>OPTIONS</option>
          <option>TRACE</option>
        </select>

        <p>Headers</p>
        <table class="request-headers-table">
          <thead>
            <tr>
              <th>Header</th>
              <th>Value</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(header, index) in requestHeaders" :key="index">
              <td>
                <input v-model="header.name"/>
              </td>
              <td>
                <input v-model="header.value"/>
              </td>
              <td>
                <div class="configuration-button" v-on:click="removeHeader(index)">
                  <p>x</p>
                </div>
              </td>
            </tr>
            <div class="configuration-button" v-on:click="addHeader">
              <p>+</p>
            </div>
          </tbody>
        </table>

        <p>Body</p>
        <textarea v-model="requestBody"/>
      </form>

      <p>Answer</p>
      <p>Status</p>
      <input v-model="responseStatus" readonly/>
      <p>Body</p>
      <textarea v-model="responseBody" readonly/>
      <div v-if="responseHeaders.length > 0">
        <p>Headers</p>
        <table class="request-headers-table">
          <thead>
          <tr>
            <th>Header</th>
            <th>Value</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(header, index) in responseHeaders" :key="index">
            <td>
              <input v-model="header.name"/>
            </td>
            <td>
              <input v-model="header.value"/>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="configuration-button" v-on:click="clearData">
        <p>Clear</p>
      </div>
    </div>
  </PageDiv>
</template>

<script>
import NavigationMenu from "@/components/NavigationMenu";
import PageDiv from "@/components/PageDiv";
export default {
  name: "SendPage",
  components: {PageDiv, NavigationMenu},
  data() {
    return {
      bodyBackgroundClass: 'pages-body',

      requestUrl: '',
      requestMethod: 'GET',
      requestHeaders: [],
      requestBody: '',

      responseStatus: 0,
      responseHeaders: [],
      responseBody: ''
    }
  },
  methods: {
    async sendRequest() {
      try {
        let prepareHeaders = {}
        for (let i = 0; i < this.requestHeaders; i++) {
          let header = this.requestHeaders[i]
          prepareHeaders[header.name] = header.value
        }

        let requestMeta = {
          method: this.requestMethod,
          headers: prepareHeaders
        }
        if (this.requestBody && this.requestBody.length > 0) {
          requestMeta.body = this.requestBody
        }
        let response = await fetch(this.requestUrl, requestMeta)

        this.responseStatus = response.status
        this.responseBody = await response.text()

        this.responseHeaders = []
        for (let [key, value] of response.headers) {
          this.responseHeaders.push({
            name: key,
            value: value
          })
        }
      } catch (e) {
        alert(e)
      }
    },
    removeHeader(index) {
      this.requestHeaders.splice(index, 1)
    },
    addHeader() {
      this.requestHeaders.push({
        name: '',
        value: ''
      })
    },
    clearData() {
      this.requestUrl = ''
      this.requestMethod = 'GET'
      this.requestHeaders = []
      this.requestBody = ''
      this.responseStatus = 0
      this.responseHeaders = []
      this.responseBody = ''
    }
  }
}
</script>

<style scoped>
.send-container {
  padding-top: 5%;
}

.header {
  font-family: 'Merriweather', serif;
  font-weight: bold
}

.flex-form {
  display: flex;
  justify-content: space-between;
}

.request-headers-table td, .request-headers-table th {
  border: 1px solid #ddd;
  padding: 8px;
}

.request-headers-table tr:nth-child(even){background-color: #f2f2f2;}

.request-headers-table tr:hover {background-color: #ddd;}

.request-headers-table th {
  text-align: left;
  background-color: #4f452b;
  color: white;
  font-family: 'Merriweather', serif;
  font-weight: normal;
}

.request-headers-table td {
  text-align: left;
  font-family: 'Lato', sans-serif;
  font-weight: lighter;
}

.request-headers-table td:last-child {
  width: 1%;
}

.configuration-button{
  background-color: #4f452b;
  transition: background-color 600ms ease-out 100ms;
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
</style>