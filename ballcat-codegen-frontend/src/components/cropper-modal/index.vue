<template>
  <a-modal
    v-model:open="visible"
    title="图片上传"
    :mask-closable="false"
    :confirm-loading="confirmLoading"
    :width="720"
    :footer="null"
    @cancel="handleClose"
  >
    <a-row :gutter="16">
      <a-col :xs="24" :md="18" :style="{ height: '320px' }">
        <vue-cropper
          v-show="imageSrc"
          ref="cropperRef"
          img-cross-origin="anonymous"
          alt="avatar"
          drag-mode="none"
          :src="imageSrc"
          :aspect-ratio="1"
          style="height: 100%"
          preview=".cropper-modal-preview"
        />
        <div
          v-if="!imageSrc"
          class="cropper-container cropper-bg"
          style="width: 100%; height: 320px"
        />
      </a-col>
      <a-col>
        <div class="cropper-modal-preview" />
        <div class="cropper-modal-preview circle" style="margin-top: 20px" />
      </a-col>
    </a-row>
    <a-row justify="center">
      <a-col>
        <div style="margin-top: 20px" class="cropper-modal-buttons">
          <a-space :size="20">
            <a-button-group>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.zoom(0.1))">
                <template #icon>
                  <zoom-in-outlined />
                </template>
              </a-button>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.zoom(-0.1))">
                <template #icon>
                  <zoom-out-outlined />
                </template>
              </a-button>
            </a-button-group>

            <a-button-group>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.move(-10, 0))">
                <template #icon>
                  <arrow-left-outlined />
                </template>
              </a-button>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.move(10, 0))">
                <template #icon>
                  <arrow-right-outlined />
                </template>
              </a-button>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.move(0, -10))">
                <template #icon>
                  <arrow-up-outlined />
                </template>
              </a-button>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.move(0, 10))">
                <template #icon>
                  <arrow-down-outlined />
                </template>
              </a-button>
            </a-button-group>

            <a-button-group>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.rotate(-45))">
                <template #icon>
                  <rotate-left-outlined />
                </template>
              </a-button>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.rotate(45))">
                <template #icon>
                  <rotate-right-outlined />
                </template>
              </a-button>
            </a-button-group>

            <a-button-group>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.flipX())">
                <template #icon>
                  <swap-outlined />
                </template>
              </a-button>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.flipY())">
                <template #icon>
                  <swap-outlined :rotate="90" />
                </template>
              </a-button>
            </a-button-group>

            <a-button-group>
              <a-button type="primary" @click="applyWhenHasImage(() => cropperRef?.reset())">
                <template #icon>
                  <sync-outlined />
                </template>
              </a-button>
              <a-upload name="file" :before-upload="beforeUpload" :show-upload-list="false">
                <a-button type="primary">
                  <template #icon><upload-outlined /></template>
                </a-button>
              </a-upload>
            </a-button-group>

            <a-button type="primary" @click="applyWhenHasImage(() => upload())">保存</a-button>
          </a-space>
        </div>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script setup lang="ts">
import VueCropper from '@ballcat/vue-cropper'
import type { VueCropperInstance } from '@ballcat/vue-cropper'
import 'cropperjs/dist/cropper.css'
import type { UploadProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { usePopup } from '@/hooks/popup'
import { ref } from 'vue'
import type { R } from '@/utils/axios/types'
import {
  ZoomInOutlined,
  ZoomOutOutlined,
  ArrowLeftOutlined,
  ArrowRightOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  RotateLeftOutlined,
  RotateRightOutlined,
  SwapOutlined,
  SyncOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'

const props = defineProps<{
  uploadProcessor: (dataURL: string) => Promise<R>
}>()

const emits = defineEmits<{
  <T>(e: 'ok', data: R, extra?: T): void
}>()

const cropperRef = ref<VueCropperInstance>()

const imageSrc = ref<string>('')

const { visible, handleOpen, handleClose } = usePopup()

/* 执行方法前先检查是否存在图片 */
function applyWhenHasImage(func: () => void) {
  if (imageSrc.value && cropperRef.value) {
    func()
  } else {
    message.error('请先上传一张图片！')
  }
}

const confirmLoading = ref(false)

let currentFilename = ''
const beforeUpload: UploadProps['beforeUpload'] = file => {
  const reader = new FileReader()
  // 把Array Buffer转化为blob 如果是base64不需要
  // 转化为base64
  reader.readAsDataURL(file)
  reader.onload = () => {
    imageSrc.value = reader.result as string
  }
  currentFilename = file.name
  // 转化为blob
  // reader.readAsArrayBuffer(file)
  return false
}

let extra: any

// 上传图片（点击上传按钮）
const upload = () => {
  const croppedCanvas = cropperRef.value?.getCroppedCanvas()
  if (croppedCanvas) {
    try {
      const dataURL = croppedCanvas.toDataURL('image/png')
      props.uploadProcessor(dataURL).then(data => {
        emits('ok', data)
        handleClose()
      })
    } catch (e) {
      message.error('裁剪图片异常，请检查图片源是否不允许跨域加载')
    }
  }

  // TODO 图片上传有上传文件和Base64字符串两种方式，暂时为做Base64方式的处理
  // confirmLoading.value = true
}

defineExpose({
  open: (img: string, info?: any) => {
    extra = info
    imageSrc.value = img || ''
    handleOpen()
  }
})
</script>

<script lang="ts">
export default {
  name: 'CropperModal'
}
</script>

<style scoped lang="less">
.cropper-modal-preview {
  width: 145px;
  height: 145px;
  border-style: solid;
  border-color: #b5b5b5;
  border-width: 1px;
  background-color: #f7f7f7;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
  }

  &.circle {
    width: 150px;
    height: 150px;
    border-radius: 85px;
  }
}

.cropper-modal-buttons {
  .ant-btn {
    height: 40px;
  }

  .ant-btn-icon-only {
    width: 40px;
    height: 40px;
  }
}
</style>
