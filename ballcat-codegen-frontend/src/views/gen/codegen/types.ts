import { TemplateEntryTree } from '@/api/gen/model/generate'

export interface GenerateModalInstance {
  open: (dsName: string, tableNames: string[]) => void
  close: () => void
}

export interface PreviewModalInstance {
  open: (templateEntryTreeList?: TemplateEntryTree[]) => void
}
