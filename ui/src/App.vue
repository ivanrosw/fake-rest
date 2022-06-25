<template>
  <router-view></router-view>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
body {
  margin: 0;
}

.home-body {
  height: 100vh;
  background-image: linear-gradient(to right bottom, #2c2617, #4f452b, #746641, #9c8958, #c6ae70);
}

.pages-body {
  height: 100vh;
  background: #c9c4bd;
}
</style>
