<template>
  <NavigationMenu></NavigationMenu>
  <p>Send</p>
</template>

<script>
import NavigationMenu from "@/components/NavigationMenu";
export default {
  name: "SendPage",
  components: {NavigationMenu},
  data() {
    return {
      bodyBackgroundClass: 'pages-body'
    }
  }
}
</script>

<style scoped>

</style>