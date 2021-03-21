<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row>
          <a-col :span="24">
            <a-form-item label="客户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['kuName']" placeholder="请输入客户名称" ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="委托单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['wtNum']" placeholder="请输入委托单号" disabled></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="送件人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-select-user-by-dep v-decorator="['sjr']" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="委托日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择委托日期" v-decorator="['wtTime']" :trigger-change="true" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="委托完成日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择委托完成日期" v-decorator="['wcTime']" :trigger-change="true" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="委托单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['wuDw', validatorRules.wuDw]" :trigger-change="true" dictCode="org_category" placeholder="请选择委托单位" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="测试项目" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-decorator="['wuCsxm']" rows="4" placeholder="请输入测试项目" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="委托人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['wur']" placeholder="请输入委托人" disabled></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="委托人电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['wurPhone']" placeholder="请输入委托人电话" disabled></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

import { getAction, httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: "WeiWtdModal",
    components: { 
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        validatorRules: {
          wuDw: {
            rules: [
              { required: true, message: '请输入委托单位!'},
            ]
          },
        },
        url: {
          add: "/wt/weiWtd/add",
          edit: "/wt/weiWtd/edit",
        }
     
      }
    },
    created () {

    },
    mounted() {

    },
    methods: {
      add () {
        this.$nextTick(()=>{
          getAction('/wt/weiWtd/user').then(res=>{
            console.log('res',res)

            this.$nextTick(()=>{
              this.form.setFieldsValue({
                wur:res.result.realname,
                wurPhone:res.result.phone,
                wtNum:"WT-"+Date.now()
              })
            })
          });
        })
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'kuName','wtNum','sjr','status','wtTime','wcTime','wuDw','wuCsxm','wur','wurPhone'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
         
        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'kuName','wtNum','sjr','status','wtTime','wcTime','wuDw','wuCsxm','wur','wurPhone'))
      },

      
    }
  }
</script>