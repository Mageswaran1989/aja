////#!/bin/sh
////exec scala "$0" "$@"
////!#
////scalac simple_xml_parser.scala
////scala -cp . SimpleXMLParse
//
//object SimpleXMLParse extends App
//{
//
////Simply type XML elements Scala can intrepet the XML without ""
//val foo = <foo><bar type="greet">hi</bar><bar type="count">1</bar><bar type="color">yellow</bar></foo>
//
////Print the text in XML
//println("Text in foo : " + foo.text)
//
////Selector \
//println(foo \ "bar")
//
////Map the NodeSeq
//println((foo \ "bar").map(_.text).mkString(" "))
//
////To grab the value of the type attribute on each node, we can use the \ selector followed by “@type”.
//println((foo \ "bar").map(barNode => (barNode \ "@type", barNode.text)))
//
//val baz = <a><z x="1"/><b><z x="2"/><c><z x="3"/></c><z x="4"/></b></a>
//// To dig arbitrarily deep to pull out all nodes of a given type no matter where they are, use the \\ selector
//println(baz \\ "z")
//
//println((baz \\ "z").map(_ \ "@x"))
//
////Tocreate XML from string
//val fooString = """<foo><bar type="greet">hi</bar><bar type="count">1</bar><bar type="color">yellow</bar></foo>"""
//val fooElemFromString = scala.xml.XML.loadString(fooString)
//
//
//val weather =
//<rss>
//  <channel>
//    <title>Yahoo! Weather - Boulder, CO</title>
//    <item>
//     <title>Conditions for Boulder, CO at 2:54 pm MST</title>
//     <forecast day="Thu" date="10 Nov 2011" low="37" high="58" text="Partly Cloudy"
//               code="29" />
//    </item>
//  </channel>
//</rss>
//
//val forecast = weather \ "channel" \ "item" \ "forecast"
//println(forecast)
//val day = forecast \ "@day"     // Thu
//println(day)
//val date = forecast \ "@date"   // 10 Nov 2011
//val low = forecast \ "@low"     // 37
//val high = forecast \ "@high"   // 58
//val text = forecast \ "@text"   // Partly Cloudy
//println(text)
//
//val day0 = (forecast \ "@day").text
//
//val day1 = weather \\ "forecast" \ "@day"
//
//val day2 = weather \\ "@day"
//
//println(day)
//}
//
//
