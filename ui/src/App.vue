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
  background-image: linear-gradient(to right bottom, rgb(0, 21, 36), rgb(0, 45, 63), rgb(0, 70, 80), rgb(0, 96, 84), rgb(9, 121, 76));
}

.pages-body {
  height: 100vh;
  
  background: black;
}
</style>
