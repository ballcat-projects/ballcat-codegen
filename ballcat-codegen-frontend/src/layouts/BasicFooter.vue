<template>
  <div class="ballcat-footer">
    <div class="footer-content">
      <span class="footer-text">BallCat CodeGen</span>
      <span class="footer-divider">•</span>
      <span class="footer-year">2022-{{ new Date().getFullYear() }}</span>
      <span class="footer-divider">•</span>
      <span class="footer-author">Created By Hccake</span>
    </div>
  </div>
</template>

<style scoped lang="less">
.ballcat-footer {
  text-align: center;
  padding: 24px 0;
  background: @background-color-light;
  border-top: 1px solid @border-color-base;
  
  .footer-content {
  color: @text-color-secondary;
  font-size: @font-size-base;
    
    .footer-text {
  font-weight: 500;
  color: @gray-8;
    }
    
    .footer-divider {
  margin: 0 12px;
  color: @gray-5;
    }
    
    .footer-year, .footer-author {
  color: @text-color-secondary;
    }
  }
}
</style>
