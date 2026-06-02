<template>
  <div class="home-page">
    <router-view></router-view>
  </div>
  <div class="footer-nav">
    <FooterNav />
  </div>
</template>


<script setup>
import FooterNav from '../components/FooterEmplNav.vue';
</script>

<style scoped>

</style>