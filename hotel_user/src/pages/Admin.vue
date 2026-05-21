<template>
  <div class="home-page">
    <router-view></router-view>
  </div>
  <div class="footer-nav">
    <FooterNav />
  </div>
</template>


<script setup>
import FooterNav from '../components/FooterNav.vue';
</script>

<style scoped>

</style>