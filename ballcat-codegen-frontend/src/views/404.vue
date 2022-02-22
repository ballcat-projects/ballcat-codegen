<template>
  <a-result status="404" title="404" sub-title="Sorry, the page you visited does not exist.">
    <template #extra>
      <a-button type="primary" @click="handleToHome"> Back Home </a-button>
    </template>
  </a-result>
</template>
<script setup lang="ts">
  import { useRouter } from 'vue-router'

  const router = useRouter()
  const handleToHome = () => {
    router.replace({ path: '/' })
  }
</script>
