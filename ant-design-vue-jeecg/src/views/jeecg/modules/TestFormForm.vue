<template>
  <j-form-container :disabled="disabled">
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-item label="啥也不是" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['testName']" placeholder="请输入啥也不是"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="外键" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['pid']" placeholder="请输入外键"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
  </j-form-container>
</template>
<script>
  import pick from 'lodash.pick'
  import { getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'TestFormForm',
    components: {
    },
    props:{
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        form: this.$form.createForm(this),
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        labelCol2: {
          xs: { span: 24 },
          sm: { span: 3 },
        },
        wrapperCol2: {
          xs: { span: 24 },
          sm: { span: 20 },
        },
        validatorRules: {
        },
        confirmLoading: false,
      }
    },
    methods:{
      initFormData(url,id){
        this.clearFormData()
        if(!id){
          this.edit({})
        }else{
          getAction(url,{id:id}).then(res=>{
            if(res.success){
              let records = res.result
              if(records && records.length>0){
                this.edit(records[0])
              }
            }
          })
        }
      },
      edit(record){
        this.model = Object.assign({}, record)
        console.log("TestFormForm-edit",this.model);
        let fieldval = pick(this.model,'testName','pid')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
      },
      getFormData(){
        let formdata_arr = []
        this.form.validateFields((err, values) => {
          if (!err) {
            let formdata = Object.assign(this.model, values)
            let isNullObj = true
            Object.keys(formdata).forEach(key=>{
              if(formdata[key]){
                isNullObj = false
              }
            })
            if(!isNullObj){
              formdata_arr.push(formdata)
            }
          }else{
            this.$emit("validateError","test从表表单校验未通过");
          }
        })
        console.log("test从表表单数据集",formdata_arr);
        return formdata_arr;
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'testName','pid'))
      },
      clearFormData(){
        this.form.resetFields()
        this.model={}
      },
    }
  }
</script>
