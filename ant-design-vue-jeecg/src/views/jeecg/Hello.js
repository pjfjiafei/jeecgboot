const render=function render(h, context){
  return h('h2',{
    'class':{
      foo:true,
      bar:true
    },
    style:{
      color:'yellow'
    },
    attrs:{
      id:'boo'
    },
    domProps:{
      innerHTML:'Hello世界aaaaa'
    }
  })
}

export default {
  render
}