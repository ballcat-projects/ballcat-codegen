import { EditorState, basicSetup } from '@codemirror/basic-setup'
import { EditorView, KeyBinding, keymap, ViewUpdate } from '@codemirror/view'
import { defaultKeymap, indentWithTab } from '@codemirror/commands'
import { Panel, showPanel } from '@codemirror/panel'
// import { oneDarkTheme } from '@codemirror/theme-one-dark'
import { StreamLanguage } from '@codemirror/stream-parser'
import { velocity } from '@codemirror/legacy-modes/mode/velocity'
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { javascript } from '@codemirror/lang-javascript'
import { java } from '@codemirror/lang-java'
import { html } from '@codemirror/lang-html'
import { StyleSpec } from 'style-mod';

class Editor {
  editorView: EditorView
  templateEngine?: string

  constructor(
    el: Element | DocumentFragment,
    content: string,
    updateListener: (v: ViewUpdate) => void,
    keyBinds: KeyBinding[],
    themes ?: {
      spec: {
        [selector: string]: StyleSpec;
      }, options?: {
        dark?: boolean;
      }
    }
  ) {
    const themeConf = themes ? EditorView.theme(themes.spec, themes.options ? themes.options : {}) : []
    this.editorView = new EditorView({
      parent: el,
      state: EditorState.create({
        doc: content,
        extensions: [
          // 基础设置
          basicSetup,
          // 快捷键
          keymap.of([...defaultKeymap, indentWithTab, ...keyBinds]),
          // 提示面板
          showPanel.of(this.tooltipPanel),
          // 主题
          // oneDarkTheme,
          themeConf,
          // 更新监听
          EditorView.updateListener.of(updateListener),

          // java 语法高亮
          java(),
          // javascript 语法高亮
          javascript({ typescript: true }),
          // html
          html(),
          // velocity 语法高亮
          StreamLanguage.define(velocity)
        ]
      })
    })
  }

  tooltipPanel = (view: EditorView): Panel => {
    const dom = document.createElement('div')
    dom.innerHTML = this.getToolTip()
    return {
      dom,
      update: () => {
        dom.innerHTML = this.getToolTip()
      }
    }
  }

  private getToolTip() {
    let innerHTML = ''
    if (this.templateEngine) {
      innerHTML = `<span style="margin-right: 20px">
                      <b>模板引擎</b>：<span style="color: red; font-weight: 600;">${this.templateEngine}</span>
                  </span>`
    }
    return innerHTML + '<b>F11</b>：全屏，<b>Ctrl+S</b>：保存'
  }

  /**
   * 获取编辑器里的文本内容
   */
  getEditorDoc = (): string => {
    return this.editorView.state.doc.toString() || ''
  }

  /**
   * 获取编辑器里的文本内容
   */
  setEditorDoc = (content: string): void => {
    const currentDoc = this.getEditorDoc()
    this.editorView.dispatch(
      this.editorView.state.update({
        changes: { from: 0, to: currentDoc.length, insert: content }
      })
    )
  }
}

export default Editor
