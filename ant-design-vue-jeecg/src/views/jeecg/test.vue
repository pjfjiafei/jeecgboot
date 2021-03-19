<template>
  <div>
  {{ message }}hhh
    <p v-if="seen">显示</p>

    <a-button type="primary">
      Primary
    </a-button>
    <a-button>Default</a-button>
    <a-button type="dashed">
      Dashed
    </a-button>
    <a-button type="danger">
      Danger    </a-button>
    <a-config-provider :auto-insert-space-in-button="false">
      <a-button type="primary">
        按钮
      </a-button>
    </a-config-provider>
    <a-button type="primary">
      按钮
    </a-button>
    <a-button type="link">
      Link
    </a-button>
  </div>
</template>

<script>
  import { getAction } from '@/api/manage'
  export default {
    data () {
      return {
          message : "",
          seen:false,
      }
    },
    methods: {
    },
    created (){
        getAction('/test/jeecgTest/hello').then((res) => {
                console.log("----------")
                console.log(res)
            this.message = res.result;
        })
    },
  }
</script>