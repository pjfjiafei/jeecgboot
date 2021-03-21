<script>
export default {
  name: 'Hello',
    render(createElement, context) {
      return createElement('h2',{
        'class':{
          foo:true,
          bar:true
        },
        style:{
          color:'blue'
        },
        attrs:{
          id:'boo'
        },
        domProps:{
          innerHTML:'Hello世界'
        }
      })
    }
}
</script>