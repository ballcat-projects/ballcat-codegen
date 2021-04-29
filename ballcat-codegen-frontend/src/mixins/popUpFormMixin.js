import FormMixin from './formMixin'

export default {
  mixins: [FormMixin],
  data () {
    return {
      // 标题
      title: '',
      visible: false
    }
  },
  methods: {
    show(attributes) {
      this.title = attributes.title
      this.visible = true
      this.submitLoading = false
    },
    add(attributes) {
      this.buildCreatedForm(attributes)
      this.show(attributes)
    },
    update(record, attributes) {
      this.buildUpdatedForm(record, attributes)
      this.show(attributes)
    },
    submitSuccess (res){
      this.$emit('reload-page-table', false)
      this.handleClose()
    },
    handleClose(e) {
      this.visible = false
      this.submitLoading = false
    }
  }
}
