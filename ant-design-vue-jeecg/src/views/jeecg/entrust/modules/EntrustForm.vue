<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form :form="form" slot="detail">
        <a-row>
          <a-col :span="12" >
            <a-form-item label="委托单编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['entrustCode']" placeholder="请输入委托单编号" disabled></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="委托单名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['entrustName', validatorRules.entrustName]" placeholder="请输入委托单名称" ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="委托单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-select-depart v-decorator="['entrustDept', validatorRules.entrustDept]" multi />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="委托人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-select-user-by-dep v-decorator="['entrustUser', validatorRules.entrustUser]" />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="计划开始时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择计划开始时间" v-decorator="['planStartDate', validatorRules.planStartDate]" :trigger-change="true" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="计划结束时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择计划结束时间" v-decorator="['planEndDate', validatorRules.planEndDate]" :trigger-change="true" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="委托人联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="['phoneNumber']" placeholder="请输入委托人联系电话" ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="测试项目" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-multi-select-tag type="list_multi" v-decorator="['testItems']" :trigger-change="true" dictCode="testItems" placeholder="请选择测试项目" />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['status']" :trigger-change="true" dictCode="entrust_status" placeholder="请选择状态" />
            </a-form-item>
          </a-col>
          <a-col :span="12" >
            <a-form-item label="密级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['classification']" :trigger-change="true" dictCode="classification" placeholder="请选择密级" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
      <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="被试件表" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="entrustTestpieceTable.loading"
          :columns="entrustTestpieceTable.columns"
          :dataSource="entrustTestpieceTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"/>
      </a-tab-pane>
    </a-tabs>
    <a-row v-if="showFlowSubmitButton" style="text-align: center;width: 100%;margin-top: 16px;"><a-button @click="handleOk">提 交</a-button></a-row>
  </a-spin>
</template>

<script>

  import pick from 'lodash.pick'
  import { getAction } from '@/api/manage'
  import { FormTypes,getRefPromise } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'EntrustForm',
    mixins: [JEditableTableMixin],
    components: {
    },
    data() {
      return {
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
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {
          entrustName: {
            rules: [
              { required: true, message: '请输入委托单名称!'},
            ]
          },
          entrustDept: {
            rules: [
              { required: true, message: '请输入委托单位!'},
            ]
          },
          entrustUser: {
            rules: [
              { required: true, message: '请输入委托人!'},
            ]
          },
          planStartDate: {
            rules: [
              { required: true, message: '请输入计划开始时间!'},
            ]
          },
          planEndDate: {
            rules: [
              { required: true, message: '请输入计划结束时间!'},
            ]
          },
        },
        refKeys: ['entrustTestpiece', ],
        tableKeys:['entrustTestpiece', ],
        activeKey: 'entrustTestpiece',
        // 被试件表
        entrustTestpieceTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '被试件编号',
              key: 'testpieceCode',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '被试件名称',
              key: 'testpieceName',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '被试件数量',
              key: 'testpieceNumbei',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ pattern: "z", message: "${title}格式不正确" }],
            },
          ]
        },
        url: {
          add: "/entrust/entrust/add",
          edit: "/entrust/entrust/edit",
          queryById: "/entrust/entrust/queryById",
          entrustTestpiece: {
            list: '/entrust/entrust/queryEntrustTestpieceByMainId'
          },
        }
      }
    },
    props: {
      //流程表单data
      formData: {
        type: Object,
        default: ()=>{},
        required: false
      },
      //表单模式：false流程表单 true普通表单
      formBpm: {
        type: Boolean,
        default: false,
        required: false
      },
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    computed: {
      formDisabled(){
        if(this.formBpm===true){
          if(this.formData.disabled===false){
            return false
          }
          return true
        }
        return this.disabled
      },
      showFlowSubmitButton(){
        if(this.formBpm===true){
          if(this.formData.disabled===false){
            return true
          }
        }
        return false
      }
    },
    created () {
      //如果是流程中表单，则需要加载流程表单data
      this.showFlowData();
    },
    methods: {
      addBefore(){
        this.form.resetFields()
        this.entrustTestpieceTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model,'entrustCode','entrustName','entrustDept','entrustUser','planStartDate','planEndDate','phoneNumber','testItems','status','classification')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.entrustTestpiece.list, params, this.entrustTestpieceTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          entrustTestpieceList: allValues.tablesValue[0].values,
        }
      },
      //渲染流程表单数据
      showFlowData(){
        if(this.formBpm === true){
          let params = {id:this.formData.dataId};
          getAction(this.url.queryById,params).then((res)=>{
            if(res.success){
              this.edit (res.result);
            }
          })
        }
      },
      validateError(msg){
        this.$message.error(msg)
      },
     popupCallback(row){
       this.form.setFieldsValue(pick(row,'entrustCode','entrustName','entrustDept','entrustUser','planStartDate','planEndDate','phoneNumber','testItems','status','classification'))
     },

    }
  }
</script>

<style scoped>
</style>