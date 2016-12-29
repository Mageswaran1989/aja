package org.dhira.core.optimize.listeners

import org.dhira.core.containers.Pair
import org.dhira.core.nnet.api.Model
import org.dhira.core.optimize.api.IterationListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.List

/**
 * CollectScoresIterationListener simply stores the model scores internally (along with the iteration) every 1 or N
 * iterations (this is configurable). These scores can then be obtained or exported.
 *
 * @author Alex Black
 */
class CollectScoresIterationListener extends IterationListener {
  private var frequency: Int = 1
  private var iterationCount: Int = 0
  private var scoreVsIter: List[Pair[Integer, Double]] = _ //TODO


  /**
   * Constructor for collecting scores with the specified frequency.
   * @param freq    Frequency with which to collect/save scores
   */
  def this(freq: Int) {
    this()
    if (freq <= 0) frequency = 1
    else frequency = freq
  }

  /**
   * Constructor for collecting scores with default saving frequency of 1
   */
//  def this() {
//  }



  def invoked: Boolean = {
    return false
  }

  def invoke {
  }

  def iterationDone(model: Model, iteration: Int) {
    if (({
      iterationCount += 1; iterationCount
    }) % frequency == 0) {
      val score: Double = model.score
      scoreVsIter.add(new Nothing(iterationCount, score))
    }
  }

  def getScoreVsIter: List[Pair[Integer, Double]] = {
    return getScoreVsIter
  }

  /**
   * Export the scores in tab-delimited (one per line) UTF-8 format.
   */
  @throws(classOf[IOException])
  def exportScores(outputStream: OutputStream) {
    exportScores(outputStream, "\t")
  }

  /**
   * Export the scores in delimited (one per line) UTF-8 format with the specified delimiter
   *
   * @param outputStream Stream to write to
   * @param delimiter    Delimiter to use
   */
  @throws(classOf[IOException])
  def exportScores(outputStream: OutputStream, delimiter: String) {
    val sb: StringBuilder = new StringBuilder
    sb.append("Iteration").append(delimiter).append("Score")
    import scala.collection.JavaConversions._
    for (p <- scoreVsIter) {
      sb.append("\n").append(p.getFirst).append(delimiter).append(p.getSecond)
    }
    outputStream.write(sb.toString.getBytes("UTF-8"))
  }

  /**
   * Export the scores to the specified file in delimited (one per line) UTF-8 format, tab delimited
   *
   * @param file File to write to
   */
  @throws(classOf[IOException])
  def exportScores(file: File) {
    exportScores(file, "\t")
  }

  /**
   * Export the scores to the specified file in delimited (one per line) UTF-8 format, using the specified delimiter
   *
   * @param file      File to write to
   * @param delimiter Delimiter to use for writing scores
   */
  @throws(classOf[IOException])
  def exportScores(file: File, delimiter: String) {
    try {
      val fos: FileOutputStream = new FileOutputStream(file)
      try {
        exportScores(fos, delimiter)
      } finally {
        if (fos != null) fos.close()
      }
    }
  }
}