<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="状态">
              <a-input placeholder="请输入状态" v-model="queryParam.status"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('委托单')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-button @click="commit" type="primary" >提交</a-button>
      <a-button type="primary" icon="download" @click="rollback">驳回</a-button>
      <a-button type="primary" icon="download" @click="openRecycle">回收箱</a-button>
      <a-button type="primary" icon="download" @click="downloadOdd">委托单(PDF)下载</a-button>
      <a-button type="primary" icon="download" @click="downloadWord">委托单(word)下载</a-button>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
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
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :rowClassName="tableRowClass"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type:'radio'}"
        :customRow="clickThenSelect"
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
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <a-tabs defaultActiveKey="1">
      <a-tab-pane tab="委托样品" key="1" >
        <WuYpList :mainId="selectedMainId" />
      </a-tab-pane>
    </a-tabs>
    <a-modal title='回收箱' :visible='isOpenRecycle'  width='800px' @ok='openRecycle' @cancel='openRecycle' >
      <a-button @click='recycleEmpty' type='primary'>清空</a-button>
      <a-button type='primary' @click='recycleRecover'>恢复</a-button>
      <a-table
      ref="recycleTable"
      size="middle"
      bordered
      rowKey="id"
      class="j-table-force-nowrap"
      :scroll="{x:true}"
      :columns="columns"
      :dataSource="RecycleData"
      :pagination="ipagination"
      :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type:'radio'}"
      :customRow="clickThenSelect"
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
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
      </a-table>
    </a-modal>
    <weiWtd-modal ref="modalForm" @ok="modalFormOk"></weiWtd-modal>
  </a-card>
</template>
<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import WeiWtdModal from './modules/WeiWtdModal'
  import { deleteAction, downloadFile, getAction, postAction, putAction } from '@/api/manage'
  import WuYpList from './WuYpList'
  import { initDictOptions, filterMultiDictText, filterDictTextByCache } from '@/components/dict/JDictSelectUtil'
  import '@/assets/less/TableExpand.less'
  import { axios } from '@/utils/request'
  import { getToken } from '@/utils/auth'

  export default {
    name: "WeiWtdList",
    mixins:[JeecgListMixin],
    components: {
      WuYpList,
      WeiWtdModal
    },
    setup() {
      let h="哈哈哈"
      return {h}
    },
    data () {
      return {
        // 回收箱数据
        RecycleData:[],
        isOpenRecycle:false,
        description: '委托单管理页面',
        // 表头
        columns: [
          {
            title:'客户名称',
            align:"center",
            dataIndex: 'kuName'
          },
          {
            title:'委托单号',
            align:"center",
            dataIndex: 'wtNum'
          },
          {
            title:'送件人',
            align:"center",
            dataIndex: 'sjr'
          },
          {
            title:'状态',
            align:"center",
            dataIndex: 'status',
            customRender:(text => {
              return filterDictTextByCache('wu_status',text)
            })
          },
          {
            title:'委托日期',
            align:"center",
            sorter: true,
            dataIndex: 'wtTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'委托完成日期',
            align:"center",
            dataIndex: 'wcTime'
          },
          {
            title:'委托单位',
            align:"center",
            dataIndex: 'wuDw_dictText',
          },
          {
            title:'测试项目',
            align:"center",
            dataIndex: 'wuCsxm'
          },
          {
            title:'委托人',
            align:"center",
            dataIndex: 'wur'
          },
          {
            title:'委托人电话',
            align:"center",
            dataIndex: 'wurPhone'
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
          list: "/wt/weiWtd/list",
          delete: "/wt/weiWtd/delete",
          deleteBatch: "/wt/weiWtd/deleteBatch",
          exportXlsUrl: "/wt/weiWtd/exportXls",
          importExcelUrl: "wt/weiWtd/importExcel",
        },
        dictOptions:{
         wuDw:[],
        },
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 5,
          pageSizeOptions: ['5', '10', '50'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        selectedMainId:'',
        superFieldList:[],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    comments(){

    },
    methods: {
      tableRowClass(record,index) {
        console.log('record',record)
        if (record.status == "1") {
          return "commit";
        }
        return "notcommit";
      },
      commit(){
        console.log("选中行Id",this.selectedRowKeys)
        let id=this.selectedRowKeys[0]
        getAction('/wt/weiWtd/commit',{"id":id}).then(res=>{
          console.log('res',res)
          if(!res.success){
            alert('请添加样品后进行提交');
          }
          this.loadData();
        })
      },
      rollback(){
        console.log("选中行Id",this.selectedRowKeys)
        let id=this.selectedRowKeys[0]
        getAction('/wt/weiWtd/reject',{"id":id}).then(res=>{
          this.loadData();
        })
      },
      // 打开回收箱
      openRecycle(){
        this.isOpenRecycle=!this.isOpenRecycle;
        if (this.isOpenRecycle) {
          this.getRecycleData()
        }
      },
      downloadOdd(){
        console.group("委托单")
        let id=this.selectedRowKeys[0]
        downloadFile("/wt/weiWtd/testexportPdf?id="+id,"委托单.pdf").then(res=>{
          console.log(res)
        })
      },
      downloadWord(){
        console.group("委托单")
        let id=this.selectedRowKeys[0]
        downloadFile("/wt/weiWtd/testexportWord?id="+id,"委托单.docx").then(res=>{
          console.log("下载成功",res)
        })
      },
      // 回收箱清空
      recycleEmpty(){
        debugger
        let ids = "";
        for (let a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ",";
        }
          deleteAction("/wt/weiWtd/recycleEmpty",{ids:ids}).then(res=>{
            console.log(res)
            this.getRecycleData();
          })
      },
      // 回收箱恢复
      recycleRecover(){
        let ids = "";
        for (let a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ",";
        }
        getAction("/wt/weiWtd/recycleRecover",{ids:ids}).then(res=>{
          console.log(res)
          this.getRecycleData();
        })
      },
      // 获取回收箱数据
      getRecycleData(){
        getAction('/wt/weiWtd/recyclelist').then(res=>{
          console.log(res.result)
          this.RecycleData=res.result.records;
        })
      },
      initDictConfig(){
        initDictOptions('org_category').then((res) => {
          if (res.success) {
            this.$set(this.dictOptions, 'wuDw', res.result)
          }
        })
      },
      clickThenSelect(record) {
        return {
          on: {
            click: () => {
              this.onSelectChange(record.id.split(","), [record]);
            }
          }
        }
      },
      onClearSelected() {
        this.selectedRowKeys = [];
        this.selectionRows = [];
        this.selectedMainId=''
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedMainId=selectedRowKeys[0]
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      loadData(arg) {
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.onClearSelected()
        var params = this.getQueryParams();//查询条件
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
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
    },
  }
</script>
<style lang='less' scoped>
  @import '~@assets/less/common.less';
</style>