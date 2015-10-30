import scala.io.Source

val carMilageCSVBuffer = Source.fromFile("/opt/aja/data/car-milage-no-hdr.csv")

val lines = carMilageCSVBuffer.getLines().toList
lines.getClass

lines.foreach{ line =>
  println(line)
  val cols =  line.split(",")
  println(s"${cols(0)}|${cols(1)}|${cols(2)}|${cols(3)}|${cols(4)}|${cols(5)}|" +
    s"${cols(6)}|${cols(7)}|${cols(8)}${cols(9)}|${cols(10)}|${cols(11)}|")
}
