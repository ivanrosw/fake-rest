<template>
  <div class="home-configure-send-container">
    <router-link to="/configure" class="home-button-container">
      <button class="home-button">Configure</button>
    </router-link>
    <div class="divider"></div>
    <router-link to="/send" class="home-button-container">
      <button class="home-button">Send</button>
    </router-link>
  </div>
</template>

<script>
export default {
  name: "HomePage",
  data() {
    return {
      bodyBackgroundClass: 'home-body'
    }
  }
}

</script>

<style scoped>
.home-configure-send-container {
  margin: 0;
  height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: row;
}

.home-button-container {
  flex: 1;
}

.home-button {
  height: 100%;
  width: 100%;
  background: none;
  border: none;
  cursor: pointer;
  transition: background-color 600ms ease-out 100ms;
  color: white;
  font-family: 'Lato', sans-serif;
  font-size: 4vw;

}

.home-button:hover {
  background: rgba(0,0,0,0.33);
}

.divider {
  position: fixed;
  left: 49.85%;
  top: 10%;
  height: 80%;
  width: 0.3%;
  background: white;
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 2px;
}
</style>