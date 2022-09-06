import { kebabCase } from 'unplugin-vue-components'

export interface IMatcher {
  pattern: RegExp
  styleDir: string
}

const antComponents = [
  'Affix',
  'Alert',
  'Anchor',
  'AnchorLink',
  'AutoComplete',
  'AutoCompleteOptGroup',
  'AutoCompleteOption',
  'Avatar',
  'AvatarGroup',
  'BackTop',
  'Badge',
  'BadgeRibbon',
  'Breadcrumb',
  'BreadcrumbItem',
  'BreadcrumbSeparator',
  'Button',
  'ButtonGroup',
  'Calendar',
  'Card',
  'CardGrid',
  'CardMeta',
  'Carousel',
  'Cascader',
  'CheckableTag',
  'Checkbox',
  'CheckboxGroup',
  'Col',
  'Collapse',
  'CollapsePanel',
  'Comment',
  'ConfigProvider',
  'DatePicker',
  'Descriptions',
  'DescriptionsItem',
  'DirectoryTree',
  'Divider',
  'Drawer',
  'Dropdown',
  'DropdownButton',
  'Empty',
  'Form',
  'FormItem',
  'FormItemRest',
  'Image',
  'ImagePreviewGroup',
  'Input',
  'InputGroup',
  'InputNumber',
  'InputPassword',
  'InputSearch',
  'Layout',
  'LayoutContent',
  'LayoutFooter',
  'LayoutHeader',
  'LayoutSider',
  'List',
  'ListItem',
  'ListItemMeta',
  'LocaleProvider',
  'Mentions',
  'MentionsOption',
  'Menu',
  'MenuDivider',
  'MenuItem',
  'MenuItemGroup',
  'Modal',
  'MonthPicker',
  'PageHeader',
  'Pagination',
  'Popconfirm',
  'Popover',
  'Progress',
  'Radio',
  'RadioButton',
  'RadioGroup',
  'RangePicker',
  'Rate',
  'Result',
  'Row',
  'Select',
  'SelectOptGroup',
  'SelectOption',
  'Skeleton',
  'SkeletonAvatar',
  'SkeletonButton',
  'SkeletonImage',
  'SkeletonInput',
  'Slider',
  'Space',
  'Spin',
  'Statistic',
  'StatisticCountdown',
  'Step',
  'Steps',
  'SubMenu',
  'Switch',
  'TabPane',
  'Table',
  'TableColumn',
  'TableColumnGroup',
  'TableSummary',
  'TableSummaryCell',
  'TableSummaryRow',
  'Tabs',
  'Tag',
  'Textarea',
  'TimePicker',
  'TimeRangePicker',
  'Timeline',
  'TimelineItem',
  'Tooltip',
  'Transfer',
  'Tree',
  'TreeNode',
  'TreeSelect',
  'TreeSelectNode',
  'Typography',
  'TypographyLink',
  'TypographyParagraph',
  'TypographyText',
  'TypographyTitle',
  'Upload',
  'UploadDragger',
  'WeekPicker'
]

const antMatchComponents: IMatcher[] = [
  {
    pattern: /^Avatar/,
    styleDir: 'avatar'
  },
  {
    pattern: /^AutoComplete/,
    styleDir: 'auto-complete'
  },
  {
    pattern: /^Anchor/,
    styleDir: 'anchor'
  },

  {
    pattern: /^Badge/,
    styleDir: 'badge'
  },
  {
    pattern: /^Breadcrumb/,
    styleDir: 'breadcrumb'
  },
  {
    pattern: /^Button/,
    styleDir: 'button'
  },
  {
    pattern: /^Checkbox/,
    styleDir: 'checkbox'
  },
  {
    pattern: /^Card/,
    styleDir: 'card'
  },
  {
    pattern: /^Collapse/,
    styleDir: 'collapse'
  },
  {
    pattern: /^Descriptions/,
    styleDir: 'descriptions'
  },
  {
    pattern: /^RangePicker|^WeekPicker|^MonthPicker/,
    styleDir: 'date-picker'
  },
  {
    pattern: /^Dropdown/,
    styleDir: 'dropdown'
  },

  {
    pattern: /^Form/,
    styleDir: 'form'
  },
  {
    pattern: /^InputNumber/,
    styleDir: 'input-number'
  },

  {
    pattern: /^Input|^Textarea/,
    styleDir: 'input'
  },
  {
    pattern: /^Statistic/,
    styleDir: 'statistic'
  },
  {
    pattern: /^CheckableTag/,
    styleDir: 'tag'
  },
  {
    pattern: /^TimeRangePicker/,
    styleDir: 'time-picker'
  },
  {
    pattern: /^Layout/,
    styleDir: 'layout'
  },
  {
    pattern: /^Menu|^SubMenu/,
    styleDir: 'menu'
  },

  {
    pattern: /^Table/,
    styleDir: 'table'
  },
  {
    pattern: /^TimePicker|^TimeRangePicker/,
    styleDir: 'time-picker'
  },
  {
    pattern: /^Radio/,
    styleDir: 'radio'
  },

  {
    pattern: /^Image/,
    styleDir: 'image'
  },

  {
    pattern: /^List/,
    styleDir: 'list'
  },

  {
    pattern: /^Tab/,
    styleDir: 'tabs'
  },
  {
    pattern: /^Mentions/,
    styleDir: 'mentions'
  },

  {
    pattern: /^Step/,
    styleDir: 'steps'
  },
  {
    pattern: /^Skeleton/,
    styleDir: 'skeleton'
  },

  {
    pattern: /^Select/,
    styleDir: 'select'
  },
  {
    pattern: /^TreeSelect/,
    styleDir: 'tree-select'
  },
  {
    pattern: /^Tree|^DirectoryTree/,
    styleDir: 'tree'
  },
  {
    pattern: /^Typography/,
    styleDir: 'typography'
  },
  {
    pattern: /^Timeline/,
    styleDir: 'timeline'
  },
  {
    pattern: /^Upload/,
    styleDir: 'upload'
  }
]

export const antdvStyleDeps = new Set(
  antComponents.map(compName => {
    const total = antMatchComponents.length
    for (let i = 0; i < total; i++) {
      const matcher = antMatchComponents[i]
      if (compName.match(matcher.pattern)) {
        return `ant-design-vue/es/${matcher.styleDir}/style`
      }
    }
    return `ant-design-vue/es/${kebabCase(compName)}/style`
  })
)
