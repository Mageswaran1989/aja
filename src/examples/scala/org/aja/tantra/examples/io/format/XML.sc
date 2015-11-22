import scala.xml._

val someXMLString = """
  <Aja>
  <Topics> Scala Spark NeuralNetwork </Topics>
  <Examples>
  <example>Tej</example>
  <example>Tantra</example>
  <example>Dhira</example>
  </Examples>
  </Aja>
  """

val someXML = XML.loadString(someXMLString)
someXML.getClass

val someXML1 =
  <Aja>
    <Topics> Scala Spark NeuralNetwork </Topics>
    <Examples>
      <example>Tej</example>
      <example>Tantra</example>
      <example>Dhira</example>
    </Examples>
  </Aja>

someXML1.getClass


println("//////////////////////////////////////////////////////")

someXML \ "Topics"
(someXML \ "Topics").text
someXML \ "Examples"

someXML \ "example" //no child elements
someXML \\ "example"