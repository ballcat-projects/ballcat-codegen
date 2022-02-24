import { ref } from 'vue'

/**
 * popup hook
 */
export const usePopup = () => {
  // 弹窗显示隐藏
  const visible = ref(false)
  // 弹窗组件相关
  const handleOpen = () => {
    visible.value = true
  }
  const handleClose = () => {
    visible.value = false
  }
  return {
    visible,
    handleOpen: handleOpen,
    handleClose: handleClose
  }
}
