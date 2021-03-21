<template>
<div class='main'>
  <aside></aside>
  <article></article>
</div>
</template>

<script>
import Article from '@views/account/center/page/App'
export default {
  name: 'Xuexi',
  components: { Article },
  setup() {

  },
  data(){
    return{

    }
  },
  methods:{

  },
  mounted() {
  },
  created() {
  }
}
</script>

<style scoped lang='less'>
  .main{
    display: flex;
    aside{
      height: 200px;
    }
    article{
      background-color: #00A0E9;
    }
  }
</style>