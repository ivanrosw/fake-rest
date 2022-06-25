<template>
  <div class="navigation-menu">
    <div class="navigation-menu-open-button" v-on:click="navigationMenuOpen = !navigationMenuOpen">
      <div class="navigation-menu-open-block-container">
        <div class="navigation-menu-open-button-block"></div>
        <div class="navigation-menu-open-button-block"></div>
        <div class="navigation-menu-open-button-block"></div>
      </div>
    </div>
    <transition name="navigation-menu-list-slide">
      <ul v-if="navigationMenuOpen" class="navigation-menu-list borders">
        <li class="navigation-menu-list-element">
          <router-link to="/configure">
            <button class="navigation-menu-list-element-button borders">Configure</button>
          </router-link>
        </li>
        <li class="navigation-menu-list-element">
          <router-link to="/send">
            <button class="navigation-menu-list-element-button borders">Send</button>
          </router-link>
        </li>
      </ul>
    </transition>
  </div>
</template>

<script>
export default {
  name: "NavigationMenu",
  data() {
    return {
      navigationMenuOpen: false
    }
  }
}
</script>

<style scoped>
.navigation-menu {
  display: block;
  position: fixed;
  left: 86%;
  height: 90%;
  width: 100%;
  z-index: 99999;
}

.navigation-menu-open-button {
  position: fixed;
  background: #4F452BFF;
  height: 4vh;
  width: 4vh;
  margin-top: 0.4%;
  margin-left: 11%;
  z-index: 199999;
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 0.4em;
  transition: box-shadow 600ms ease-out 100ms;
}

.navigation-menu-open-button:hover {
  box-shadow: 1em 0 2em #746641FF;
}

.navigation-menu-open-block-container {
  height: 100%;
  width: 100%;
  margin-top: 23%;
}

.navigation-menu-open-button-block {
  height: 11%;
  width: 80%;
  margin-left: 11%;
  margin-top: 10%;
  background: rgb(255, 255, 255);
}

.navigation-menu-list {
  margin-top: 6vh;
  padding: 0;
  height: 100%;
  width: 100%;
  background-image: linear-gradient(to right bottom, #2c2617, #4f452b, #746641, #9c8958, #c6ae70);
}

.navigation-menu-list-slide-enter-active {
  animation: navigation-menu-list-slide-in 0.8s;
}

@keyframes navigation-menu-list-slide-in {
  from {
    margin-left: 50%;
  }
  to {
    margin-left: 0;
  }
}

.navigation-menu-list-slide-leave-active {
  animation: navigation-menu-list-slide-in-out 1s;
}

@keyframes navigation-menu-list-slide-in-out {
  from {
    margin-left: 0;
  }
  to {
    margin-left: 100%;
  }
}

.navigation-menu-list-element {
  list-style-type: none;
}

.navigation-menu-list-element-button {
  background: none;
  cursor: pointer;
  width: 100%;
  height: 1.7em;
  transition: background-color 600ms ease-out 100ms;
  font-family: 'Lato', sans-serif;
  font-size: 2.2vw;
  text-align: left;
  text-indent: 1%;
  color: white;
}

.navigation-menu-list-element-button:hover {
  background: rgba(0,0,0,0.33);
}

.borders {
  border: 1px solid rgba(0,0,0,0.0);
  border-radius: 2em;
}
</style>