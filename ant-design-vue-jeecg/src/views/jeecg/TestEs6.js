class Plane{
  // 类似构造函数，用来生成实例
  constructor(numEngines) {
    this.numEngines=numEngines;
    this.enginesActive=false
  }
  // 原型上的方法
  startEngines() {
    console.log("starting engines..")
    this.enginesActive=true
  }
  // 静态方法
  static badweather(...planes) {
    for (let plane of planes) {
      console.log(plane)
    }
  }
}

class man extends Plane {
  constructor(age,name) {
    super();
    this.age=age;
    this.name=name
  }
}


console.log(typeof Plane)
plane=new Plane(5);
plane.startEngines()

Plane.badweather([1,2,3])