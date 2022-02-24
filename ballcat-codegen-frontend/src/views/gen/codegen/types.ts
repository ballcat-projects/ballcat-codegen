import { FileEntry } from '@/api/gen/template-entry/types'

export interface GenerateModalInstance {
  open: (dsName: string, tableNames: string[]) => void
  close: () => void
}

export interface PreviewModalInstance {
  open: (fileEntryList?: FileEntry[]) => void
}
