val postsXML = sc.textFile("Posts.xml")

val postIDTags = postsXML.flatMap { line =>
  // Matches Id="..." ... Tags="..." in  line
  val idTagRegex = "Id=\"(\\d+)\".+Tags=\"([^\"]+)\"".r
 
  // // Finds tags like <TAG> value from above
  val tagRegex = "&lt;([^&]+)&gt;".r
 
  // Yields 0 or 1 matches:
  idTagRegex.findFirstMatchIn(line) match {
    // No match -- not a  line
    case None => None
    // Match, and can extract ID and tags from m
    case Some(m) => {
      val postID = m.group(1).toInt
      val tagsString = m.group(2)
      // Pick out just TAG matching group
      val tags = tagRegex.findAllMatchIn(tagsString).map(_.group(1)).toList
      // Keep only question with at least 4 tags, and map to (post,tag) tuples
      if (tags.size >= 4) tags.map((postID,_)) else None
     }
  }
  // Because of flatMap, individual lists will concatenate
  // into one collection of tuples
}
