<template>
  <div class="about">
    <h1>This is an about page</h1>
  </div>
</template>

<style scoped>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}
:root, html, body, #app {
  width: 100vw;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}
.main-container, .about-wrapper, .about-content {
  width: 100vw !important;
  min-height: 100vh !important;
  margin: 0 !important;
  padding: 0 !important;
  box-sizing: border-box;
}
</style>
