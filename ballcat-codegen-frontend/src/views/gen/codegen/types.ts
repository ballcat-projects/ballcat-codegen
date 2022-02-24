import { FileEntry } from '@/api/gen/model/templateEntry'

export interface GenerateModalInstance {
  open: (dsName: string, tableNames: string[]) => void
  close: () => void
}

export interface PreviewModalInstance {
  open: (fileEntryList?: FileEntry[]) => void
}
