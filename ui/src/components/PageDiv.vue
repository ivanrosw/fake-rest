<template>
  <div class="main-div">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "PageDiv"
}
</script>

<style scoped>
.main-div {
  height: 100vh;
  margin-left: 15%;
  margin-right: 15%;
  padding-left: 3%;
  padding-right: 3%;
  background: white;
  border: 0.4em #907C46;
  border-left-style: solid;
  border-right-style: solid;
}
</style>