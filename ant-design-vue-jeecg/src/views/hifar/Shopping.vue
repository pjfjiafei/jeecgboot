<template>
  <div>
    <div v-if='isOpen'>
      <a-table
        :rowSelection='{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}'
        :dataSource='goods'
        :columns='goodColumns'
        rowKey='id'
        @change='change'
        :pagination="false"
        bordered
      >
        <template slot="index" slot-scope="text,record">
          <p>{{goods.indexOf(record)+1}}</p>
        </template>
        <template slot="img" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="text" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <a slot='name' slot-scope='text'>{{ text }}</a>
        <ul class='count' slot='num' slot-scope='text,record,index'>
          <li>
            <a-button class='num-jian' @click='subtractGood(index)' :disabled='record.num===1'>-</a-button>
          </li>
          <li>
            <a-input class='input-num' v-model='record.num'></a-input>
          </li>
          <li>
            <a-button class='num-jia' @click='addGood(index)'>+</a-button>
          </li>
        </ul>
        <span slot='action' slot-scope='text,record,index' @click='remGood(index)' class='remGood'>移除</span>
      </a-table>
      <div class='header'>
        <span v-show='myAddress'>地址：{{ myAddress }}</span>
        <p>已选商品： <span style='color: red'>{{selectedRows.length}}</span>件</p>
        <p></p>
        <p>合计:</p><p style='color: red;font-size: 20px'>{{ sumPrice }}</p>
        <a-button @click='open' type='primary'>结算 </a-button>
      </div>
    </div>
    <jie-suan :sumprice='sumPrice' :goods='selectedRows' @getaddress='getAddress'
              v-if='!isOpen'></jie-suan>
  </div>
</template>

<script>
import jieSuan from './JieSuan'
import imgPath from '@/assets/test.jpg'

export default {
  name: 'Shopping',
  components: {
    jieSuan
  },
  data() {
    return {
      isOpen: true,
      myAddress: '',
      goodList: [
        {
          "id": 1,
          "img":imgPath,
          "name": "红米10Pro",
          "brand": "小米",
          "price": 4999.00,
          "num": 8
        }, {
          "id": 2,
          "img":"http://localhost:5050/test.jpg",
          "name": "Iphone12",
          "brand": "苹果",
          "price": 8999.00,
          "num": 2
        },
        {
          "id": 3,
          "img":"http://localhost:5050/test.jpg",
          "name": "华为Note12",
          "brand": "华为",
          "price": 6999.00,
          "num": 3
        },
        {
          "id": 4,
          "img":"http://localhost:5050/test.jpg",
          "name": "三星手机",
          "brand": "三星",
          "price": 2999.00,
          "num": 3
        },  {
          "id": 5,
          "img":"http://localhost:5050/test.jpg",
          "name": "红米10Pro",
          "brand": "小米",
          "price": 4999.00,
          "num": 8
        }, {
          "id": 6,
          "img":"http://localhost:5050/test.jpg",
          "name": "Iphone12",
          "brand": "苹果",
          "price": 8999.00,
          "num": 2
        },
        {
          "id": 7,
          "img":"http://localhost:5050/test.jpg",
          "name": "华为Note12",
          "brand": "华为",
          "price": 6999.00,
          "num": 3
        },
        {
          "id": 8,
          "img":"http://localhost:5050/test.jpg",
          "name": "三星手机",
          "brand": "三星",
          "price": 2999.00,
          "num": 3
        },
        {
          "id": 11,
          "img":"http://localhost:5050/test.jpg",
          "name": "华为Note12",
          "brand": "华为",
          "price": 6999.00,
          "num": 3
        },
        {
          "id": 12,
          "img":"http://localhost:5050/test.jpg",
          "name": "三星手机",
          "brand": "三星",
          "price": 2999.00,
          "num": 3
        },  {
          "id": 13,
          "img":"http://localhost:5050/test.jpg",
          "name": "红米10Pro",
          "brand": "小米",
          "price": 4999.00,
          "num": 8
        }, {
          "id": 14,
          "img":"http://localhost:5050/test.jpg",
          "name": "Iphone12",
          "brand": "苹果",
          "price": 8999.00,
          "num": 2
        },
        {
          "id": 15,
          "img":"http://localhost:5050/test.jpg",
          "name": "华为Note12",
          "brand": "华为",
          "price": 6999.00,
          "num": 3
        },
        {
          "id": 16,
          "img":"http://localhost:5050/test.jpg",
          "name": "三星手机",
          "brand": "三星",
          "price": 2999.00,
          "num": 3
        },  {
          "id": 17,
          "img":"http://localhost:5050/test.jpg",
          "name": "红米10Pro",
          "brand": "小米",
          "price": 4999.00,
          "num": 8
        }, {
          "id": 18,
          "img":"http://localhost:5050/test.jpg",
          "name": "Iphone12",
          "brand": "苹果",
          "price": 8999.00,
          "num": 2
        },
        {
          "id": 19,
          "img":"http://localhost:5050/test.jpg",
          "name": "华为Note12",
          "brand": "华为",
          "price": 6999.00,
          "num": 3
        },
        {
          "id": 20,
          "img":"http://localhost:5050/test.jpg",
          "name": "三星手机",
          "brand": "三星",
          "price": 2999.00,
          "num": 3
        }
      ],
      goodColumns: [
        {
          title: '序号',
          dataIndex: '',
          width: '70px',
          align:"center",
          scopedSlots: {customRender: "index"}
        },
        {
          title: '图片',
          align: 'center',
          dataIndex: 'img',
          scopedSlots: {customRender: 'img'}
        },
        {
        title: '名称',
        align: 'center',
        dataIndex: 'name',
        scopedSlots: { customRender: 'name' }
      }, {
        title: '品牌',
        align: 'center',
        dataIndex: 'brand'
      }, {
        title: '价格',
        align: 'center',
        dataIndex: 'price',
        customRender: (text => {
            return `￥${text.toFixed(2)}`
          }
        )
      }, {
        title: '数量',
        align: 'center',
        dataIndex: 'num',
        width: '100px',
        scopedSlots: { customRender: 'num' }
      }, {
        title: '操作',
        dataIndex: 'action',
        align: 'center',
        fixed: 'right',
        width: 147,
        scopedSlots: { customRender: 'action' }
      }],
      selectedRowKeys: [],
      selectedRows: [],
      /* 分页参数 */
      ipagination:{
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['5', '10', '20'],
        showTotal: (total, range) => {
          return range[0] + "-" + range[1] + " 共" + total + "条"
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      goods:[],
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      // 分页数据
      /*let arr=JSON.parse(JSON.stringify(this.goodList));
      this.goods=arr.splice((this.ipagination.current-1)*this.ipagination.pageSize,this.ipagination.pageSize)
      this.ipagination.total=this.goodList.length*/
      this.goods=JSON.parse(JSON.stringify(this.goodList))
    },
    change(pagination,filters,sorter) {
      this.ipagination=pagination;
      this.loadData()
    },
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
    subtractGood(index) {
      let num = this.goods[index].num
      if (num <= 1) {
        return
      }
      this.goods[index].num--
    },
    addGood(index) {
      this.goods[index].num++
    },
    remGood(index) {
      this.selectedRows = this.selectedRows.filter(row => row !== this.goods[index])
      this.goods.splice(index, 1)
    },
    getAddress(address) {
      this.myAddress = address
    },
    open() {
      if (this.isOpen && this.selectedRows.length === 0) {
        this.$message.error("请选择商品")
        return
      }
      this.isOpen = !this.isOpen
    }
  },
  filters: {
    myFilter(value) {
      return '￥' + value.toFixed(2)
    }
  },
  computed: {
    sumPrice() {
      let sumPrice = 0
      this.selectedRows.map(good => sumPrice += good.price * good.num)
      return sumPrice.toFixed(2)
    },
    rowSelection() {
      return {
        onChange(selectedRowKeys, selectedRows) {

        },
        getCheckboxProps: record => ({
            props: {
              // disabled: record.id === 1,
              name: record.name
            }
          }
        )
      }
    }
  },
  watch: {}
}
</script>

<style scoped lang='less'>
* {
  margin: 0;
  padding: 0;
  outline: 0;
}

table {
  text-align: center;
}

button {
  width: 80px;
  height: 30px;
}

ul, li {
  list-style: none;
}

.count {
  display: flex;
}

.count li {
  flex: auto;
  height: 20px;
  line-height: 20px;
}

.input-num {
width: 20px;
}

.num-jian, .num-jia {
  width: 22px;
  background-color: #e1ebef;
  color: #151414;
}

.num-jia:hover, .num-jian:hover {
  cursor: pointer;
}

.remGood {
  color: blue;
  cursor: pointer;
}
.header {
  position: fixed;
  height: 50px;
  width: 88%;
  bottom: 0;
  background-color: #ccd6da;
  z-index: 2;
  display: flex;
  line-height: 50px;
  justify-content: flex-end;
  button{
    margin: 10px;
  }
  p{
    margin: 10px;
  }
}

</style>