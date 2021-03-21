function Plane(numEngines) {
  this.numEngines=numEngines;
  this.enginesActive=false;
}
// 由所有实例‘继承’的方法
Plane.prototype.startEngines = function() {
  console.log("starting engines...")
  this.enginesActive=true
};

function Man(age,name) {
  Plane.call()
  this.age=age;
  this.name=name;
}
Man.prototype=Object.create(Plane.prototype);
Man.prototype.constructor=Man;

