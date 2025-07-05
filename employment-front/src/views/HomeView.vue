<script setup lang="ts">
import TheWelcome from '../components/TheWelcome.vue'
</script>

<template>
  <main>
    <TheWelcome />
  </main>
</template>

<style scoped>
:root, html, body, #app {
  width: 100vw;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}
.main-container, .home-wrapper, .home-content {
  width: 100vw !important;
  min-height: 100vh !important;
  margin: 0 !important;
  padding: 0 !important;
  box-sizing: border-box;
}
</style>
