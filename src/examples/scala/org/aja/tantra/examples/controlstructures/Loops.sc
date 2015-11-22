
/**
 * Created by mdhandapani on 2/11/15.
 */

val list = List(1,2,3,4,5,6,7,8,9,10)

for(element <- list if element%2 == 0; if element > 5) {
  println(element)
}
