//object RichXmlParser extends App
//{
//  val musicElem = scala.xml.XML.loadFile("music.xml")
//
//  val artist = (musicElem \ "artist").foreach { artist =>
//    println((artist \ "@name").text + "\n")
//    val albums = (artist \ "album").foreach { album =>
//      println("  " + (album \ "@title").text + "\n")
//      val songs = (album \ "song").foreach { song =>
//        println("    " + (song \ "@title").text) }
//    println
//    }
//  }
//
//  //Shorter Version
//  (musicElem \ "artist").foreach { artist =>
//    println((artist \ "@name").text + "\n")
//    (artist \ "album").foreach { album =>
//      println("  " + (album \ "@title").text + "\n")
//      (album \ "song").foreach { song =>
//        println("    " + (song \ "@title").text) }
//    println
//    }
//  }
//
//  case class Song(val title: String, val length: String) {
//    //lazy to postpone the calculation till it gets accessed &
//    //"val" is used to compute time only once
//    lazy val time = {
//      val Array(minutes, seconds) = length.split(":")
//      minutes.toInt*60 + seconds.toInt
//    }
//  }
//
//  case class Album(val title: String, val songs: Seq[Song], val description: String) {
//    lazy val time = songs.map(_.time).sum
//    lazy val length = (time / 60)+":"+(time % 60)
//  }
//
//  case class Artist(val name: String, val albums: Seq[Album])
//
//  val foobar = Song("Foo Bar", "3:29")
//  println("foobar time:" + foobar.time)
//
//  val songs = (musicElem \\ "song").map { song =>
//    Song((song \ "@title").text, (song \ "@length").text)
//  }
//
//  val artists = (musicElem \ "artist").map { artist =>
//    val name = (artist \ "@name").text
//    val albums = (artist \ "album").map { album =>
//      val title = (album \ "@title").text
//      val description = (album \ "description").text
//      val songList = (album \ "song").map { song =>
//        Song((song \ "@title").text, (song \ "@length").text)
//      }
//    Album(title, songList, description)
//   }
//  Artist(name, albums)
//  }
//
//  val albumLengths = artists.flatMap { artist =>
//    artist.albums.map(album => (artist.name, album.title, album.length))
//  }
//  albumLengths.foreach(println)
//
//  //Marshalling
//  val bloom = artists(0).albums(0).songs(0)
//  val bloomXml = <song title={bloom.title} length={bloom.length}/>
//  val bloomXmlString = "<song title=\""+bloom.title+"\" length=\""+bloom.length+"\"/>"
//  val bloomXmlFromString = scala.xml.XML.loadString(bloomXmlString)
//
//  //use :paste commands while testing on REPL
//  val marshalled =
//    <music> {
//      artists.map { artist =>
//        <artist name={artist.name}> {
//          artist.albums.map { album =>
//            <album title={album.title}> {
//              album.songs.map(song =>
//                <song title={song.title} length={song.length}/>)
//             }
//             <description>{album.description}</description>
//             </album>
//           }
//        }
//        </artist>
//      }
//    } </music>
//
//  val marshalledYield =
//  <music>
//  { for (artist <- artists) yield
//    <artist name={artist.name}>
//    { for (album <- artist.albums) yield
//      <album title={album.title}>
//      { for (song <- album.songs) yield <song title={song.title} length={song.length}/> }
//        <description>{album.description}</description>
//      </album>
//    }
//    </artist>
//  }
//  </music>
//
//}
