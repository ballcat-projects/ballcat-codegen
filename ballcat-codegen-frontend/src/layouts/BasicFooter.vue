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
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
  
  .footer-content {
    color: #8c8c8c;
    font-size: 14px;
    
    .footer-text {
      font-weight: 500;
      color: #595959;
    }
    
    .footer-divider {
      margin: 0 12px;
      color: #d9d9d9;
    }
    
    .footer-year, .footer-author {
      color: #8c8c8c;
    }
  }
}
</style>
