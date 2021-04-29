import FormMixin from './formMixin'

export default {
  mixins: [FormMixin],
  data () {
    return {
      // 标题
      title: '',
    }
  },
  methods: {
    add (attributes) {
      this.title = attributes.title
      this.buildCreatedForm(attributes)
    },
    update (record, attributes) {
      this.title = attributes.title
      this.buildUpdatedForm(record, attributes)
    },
    submitSuccess (res){
      this.backToPage(true);
    },
    backToPage (needRefresh) {
      this.$emit('back-to-page', needRefresh)
    }
  }
}
