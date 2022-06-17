<template>
  <NavigationMenu></NavigationMenu>
  <p>Configure</p>
</template>

<script>
import NavigationMenu from "@/components/NavigationMenu";

export default {
  name: "ConfigurePage",
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