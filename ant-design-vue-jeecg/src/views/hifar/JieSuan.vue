<template>
  <div>
    <h4>商品信息</h4>
    <a-table
      :dataSource='goods'
      :columns='goodColumns'
      rowKey='id'
      :pagination='false'
      bordered
    >
    </a-table>
    <h3>总价:{{ sumprice }}</h3>
    <label for='address'>
      地址:
    </label> <a-input id='address' type="text" v-model="address"></a-input>
    <nav @click="goBack"><a href="javascript:void(0)">返回购物车</a></nav>
    <a-button @click="commit" type='primary'>提交</a-button>
    <a-modal
    title='结算'
    :visible='visible'
    @ok='handOk'
    @cancel='handleCancle'
    >
      <span>
        <p>总价：{{sumprice}}</p>
        <p>地址：{{address}}</p>
      </span>
    </a-modal>
  </div>
</template>

<script>
export default {
  name: 'JieSuan',
  props: {
    goods: {
      type: Array
    },
    sumprice: {
      type: String
    }
  },
  data() {
    return {
      visible:false,
      address: '',
      goodColumns:[{
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
      }]
    }
  },
  methods: {
    handOk() {
      this.visible=false;
    },
    handleCancle() {
      this.visible=false;
    },
    commit() {
      if (!this.address) {
        this.$message.error("请填写地址")
        return
      }
      this.visible=true;
    },
    goBack() {
      this.$parent.open();
      this.$emit('getaddress', this.address)
    }
  }
}
</script>

<style scoped>

</style>