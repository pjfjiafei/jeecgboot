<template>
  <a-card :bordered="false" :class="'cust-erp-sub-tab'">
    <!-- 操作按钮区域 -->
    <div class="table-operator" v-if="mainId">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('委托样品')">导出</a-button>
      <a-upload
        name="file"
        :showUploadList="false"
        :multiple="false"
        :headers="tokenHeader"
        :action="importExcelUrl"
        @change="handleImportExcel">
          <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="beforeExit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => beforeDelete(record.fid)">
            <a>删除</a>
          </a-popconfirm>
        </span>
      </a-table>
    </div>

    <wuYp-modal ref="modalForm" @ok="modalFormOk" :mainId="mainId" :pid='selectedRowKeys[0]'></wuYp-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import WuYpModal from './modules/WuYpModal'
  import { getAction } from '@api/manage'

  export default {
    name: "WuYpList",
    mixins:[JeecgListMixin],
    components: { WuYpModal },
    props:{
      mainId:{
        type:String,
        default:'',
        required:false
      },
    },
    watch:{
      mainId:{
        immediate: true,
        handler(val) {
          if(!this.mainId){
            this.clearList()
          }else{
            this.queryParam['fid'] = val
            this.loadData(1);
          }
        }
      }
    },
    data () {
      return {
        description: '委托单管理页面',
        disableMixinCreated:true,
        // 表头
        columns: [
          {
            title:'样品名称',
            align:"center",
            dataIndex: 'name'
          },
          {
            title:'文件数量',
            align:"center",
            dataIndex: 'fileNum'
          },
          {
            title:'样品数量',
            align:"center",
            dataIndex: 'ypNum'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/wt/weiWtd/listWuYpByMainId",
          delete: "/wt/weiWtd/deleteWuYp",
          childList: "/wt/testTree/childList",
          deleteBatch: "/wt/weiWtd/deleteBatchWuYp",
          exportXlsUrl: "/wt/weiWtd/exportWuYp",
          importUrl: "/wt/weiWtd/importWuYp",
        },
        dictOptions:{
         wuDw:[],
        },
        superFieldList:[],
        expandedRowKeys:[],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl(){
        return `${window._CONFIG['domianURL']}/${this.url.importUrl}/${this.mainId}`;
      }
    },
    methods: {
      beforeExit(record){
        getAction('/wt/weiWtd/queryById',{id:record.fid}).then(res=>{
          console.log(res)
          if(res.result.status==1){
            this.$message.error("委托单已提交,数据无法修改")
            return
          }else {
            this.handleEdit(record)
          }
        })
      },
      beforeDelete(record){
        console.log("样品删除前")
        getAction('/wt/weiWtd/queryById',{id:record.fid}).then(res=>{
          console.log(res)
          if(res.result.status==1){
            this.$message.error("委托单已提交,数据无法删除")
            return
          }else {
            this.handleDelete(record)
          }
        })
      },
/*
      loadData(arg){
        debugger
        if(arg==1){
          this.ipagination.current=1
        }
        this.loading = true
        let params = this.getQueryParams()
        params.hasQuery = 'true'
        getAction(this.url.list,params).then(res=>{
          if(res.success){
            let result = res.result
            if(Number(result.total)>0){
              this.ipagination.total = Number(result.total)
              this.dataSource = this.getDataByResult(res.result.records)
              return this.loadDataByExpandedRows(this.dataSource)
            }else{
              this.ipagination.total=0
              this.dataSource=[]
            }
          }else{
            this.$message.warning(res.message)
          }
        }).finally(()=>{
          this.loading = false
        })
      },

      // 根据已展开的行查询数据（用于保存后刷新时异步加载子级的数据）
      loadDataByExpandedRows(dataList) {
        if (this.expandedRowKeys.length > 0) {
          return getAction('/wt/weiWtd/getChildListBatch',{ parentIds: this.expandedRowKeys.join(',') }).then(res=>{
            if (res.success && res.result.records.length>0) {
              //已展开的数据批量子节点
              let records = res.result.records
              const listMap = new Map();
              for (let item of records) {
                let pid = item[this.pidField];
                if (this.expandedRowKeys.join(',').includes(pid)) {
                  let mapList = listMap.get(pid);
                  if (mapList == null) {
                    mapList = [];
                  }
                  mapList.push(item);
                  listMap.set(pid, mapList);
                }
              }
              let childrenMap = listMap;
              let fn = (list) => {
                if(list) {
                  list.forEach(data => {
                    if (this.expandedRowKeys.includes(data.id)) {
                      data.children = this.getDataByResult(childrenMap.get(data.id))
                      fn(data.children)
                    }
                  })
                }
              }
              fn(dataList)
            }
          })
        } else {
          return Promise.resolve()
        }
      },
      getDataByResult(result){
        if(result){
          return result.map(item=>{
            //判断是否标记了带有子节点
            if(item[this.hasChildrenField]=='1'){
              let loadChild = { id: item.id+'_loadChild', name: 'loading...', isLoading: true }
              item.children = [loadChild]
            }
            return item
          })
        }
      },
      handleExpand(expanded, record){
        // 判断是否是展开状态
        if (expanded) {
          this.expandedRowKeys.push(record.id)
          if (record.children.length>0 && record.children[0].isLoading === true) {
            let params = this.getQueryParams(1);//查询条件
            params[this.pidField] = record.id
            params.hasQuery = 'false'
            params.superQueryParams=""
            getAction(this.url.childList,params).then((res)=>{
              if(res.success){
                if(res.result.records){
                  record.children = this.getDataByResult(res.result.records)
                  this.dataSource = [...this.dataSource]
                }else{
                  record.children=''
                  record.hasChildrenField='0'
                }
              }else{
                this.$message.warning(res.message)
              }
            })
          }
        }else{
          let keyIndex = this.expandedRowKeys.indexOf(record.id)
          if(keyIndex>=0){
            this.expandedRowKeys.splice(keyIndex, 1);
          }
        }
      },
      handleAddChild(record){
        this.loadParent = true
        let obj = {}
        obj[this.pidField] = record['id']
        this.$refs.modalForm.add(obj);
      },*/
      clearList(){
        this.dataSource=[]
        this.selectedRowKeys=[]
        this.ipagination.current = 1
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'kuName',text:'客户名称',dictCode:''})
        fieldList.push({type:'string',value:'wtNum',text:'委托单号',dictCode:''})
        fieldList.push({type:'sel_user',value:'sjr',text:'送件人'})
        fieldList.push({type:'string',value:'status',text:'状态',dictCode:'wu_status'})
        fieldList.push({type:'date',value:'wtTime',text:'委托日期'})
        fieldList.push({type:'datetime',value:'wcTime',text:'委托完成日期'})
        fieldList.push({type:'string',value:'wuDw',text:'委托单位',dictCode:'org_category'})
        fieldList.push({type:'string',value:'wuCsxm',text:'测试项目',dictCode:''})
        fieldList.push({type:'string',value:'wur',text:'委托人',dictCode:''})
        fieldList.push({type:'string',value:'wurPhone',text:'委托人电话',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  @import 'WeiWtd.less';
</style>
