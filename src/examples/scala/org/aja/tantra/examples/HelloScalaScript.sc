

/**
 * Created by mageswaran on 20/10/15.
 */

//val engine = new ScriptEngineManager().getEngineByName("scala")
//engine.put("n", 5)
//engine.eval("1 to n.asInstanceOf[Int] foreachprintln")
//

class Vector2D(var x: Int, var y: Int) {
  def magnify(v: Int): Vector2D = {
    x *= v
    y *= v
    this
  }

  override def toString = "Vector2D(" + x + "," + y + ")"

  def -(other: Vector2D) : Vector2D = {
    this.x = this.x - other.x
    this.y = this.y - other.y
    this
  }
}

val x = new Vector2D(1,1)
val y = new Vector2D(-1,1)

val z = x.magnify(3) - (x - y).magnify(3)
//val z = x.magnify(3)
//val a = (x-y)

