<script setup>
import { globalError } from '@/stores/errorStore'

function clearError() {
  globalError.value = ''
}
</script>

<template>
  <div v-if="globalError" class="error-overlay">
    <div class="error-box">
      <p>{{ globalError }}</p>
      <button class="btn" @click="clearError">Закрыть</button>
    </div>
  </div>
  <router-view />
</template>

<style scoped>
  .error-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;

    background: rgba(15, 23, 42, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;

    z-index: 9999;
  }

  .error-box {
    background: white;
    padding: 24px 28px;
    border-radius: 16px;
    box-shadow: 0 12px 28px rgba(0,0,0,0.2);

    max-width: 420px;
    text-align: center;
  }
</style>
