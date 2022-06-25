<template>
  <NavigationMenu/>
  <PageDiv></PageDiv>
</template>

<script>
import NavigationMenu from "@/components/NavigationMenu";
import PageDiv from "@/components/PageDiv";
export default {
  name: "SendPage",
  components: {PageDiv, NavigationMenu},
  data() {
    return {
      bodyBackgroundClass: 'pages-body'
    }
  }
}
</script>

<style scoped>

</style>