<template xmlns='http://www.w3.org/1999/html'>
  <div class='main'>
    <header>HEADER</header>
    <div class='content'>
      <aside>左边菜单</aside>
      <article>
        定义锚点<a href='WeiWtd.less'>锚点</a>
        定义缩写<abbr>WEI</abbr>
       <input v-focus/>
        <hello-j></hello-j>
        <hello></hello>
        <test-com></test-com>
      <p>{{level | capitalize}}</p>
        <a-form layout="inline" :form="form">
          <a-form-item>
            <j-dict-select-tag v-model="sex" title="性别" dictCode="sex" placeholder="请选择性别"/>
          </a-form-item>
        </a-form>
      </article>
    </div>
    <footer>FOOTER</footer>
  </div>
</template>

<script>
import hello from './Hello'
import helloJ from './Hello.js'
import { validateCheckRule } from '@/utils/util'
import Article from '@views/account/center/page/App'
import JDictSelectTag from '../../components/dict/JDictSelectTag.vue'

export default {
  name: 'TestHelloWorld',
  components: { Article,hello,JDictSelectTag,helloJ,
    testCom: {
      render(h, context) {
        return h('h2',{
          'class':{
            foo:true,
            bar:true
          },
          style:{
            color:'red'
          },
          attrs:{
            id:'boo'
          },
          domProps:{
            innerHTML:'HelloWorld'
          }
        })
      }
    }
  },
  //  过滤器
 filters:{
   capitalize: function(value) {
     console.log("--------------------------------")
     console.log(value)
     return value+"!!!"
   }
 },
  data() {
    return {
      level:5,
      msg: '',
      initiaValue: 0,
      form: this.$form.createForm(this),
      dataStr: '',
      sex: '',
      validatorRules: {
        name: {
          initiaValue: 1,
          rules: [
            { required: true, message: '请输入姓名' },
            { pattern: /^张三$/, message: '请输入正确格式' }
          ]
        },
        code: {
          rules: [
            { required: true, message: '必填' },
            { validator: (r, v, c) => validateCheckRule('TEST_JY_CODE', v, c) }
          ]
        }
      }
    }
  },
  // 自定义指令
  directives:{
    focus:{
      // 指令的定义
      inserted: function(el) {
        el.focus()
      }
    }
  },
  comments:{

  },
  methods: {
    hello() {
      hello().then(res => {
        console.log(res)
        this.msg = res.result
        /* this.$nextTick(() => {
           this.initiaValue=1;
           this.form.setFieldsValue(pick(this.model,'name'))
         })*/
        console.log('===================')
        let name = this.form.getFieldValue('name')
        console.log(name)
        // 修改
        name = '王五'
        this.$nextTick(() => {
          this.form.setFieldsValue({
            name: name
          })
        })
        console.log('======修改后========')
        console.log(this.form.getFieldValue('name'))
      })
    }
  }

}
</script>

<style scoped lang="less">
.main {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: red;
 /* article{
    flex: auto;
    background-color: #00A0E9;
  }*/
  .content{
    flex: auto;
    display: flex;
    article{
      flex: auto;
      background-color: #00A0E9;
    }
    aside{
      background-color: #00DB00;
    }
  }
}

</style>