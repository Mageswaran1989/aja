package org.dhira.core.util

import lombok.NonNull
import org.apache.commons.io.output.CloseShieldOutputStream
import org.deeplearning4j.nn.api.Layer
import org.deeplearning4j.nn.api.Model
import org.deeplearning4j.nn.api.Updater
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.layers.RBM
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.updater.graph.ComputationGraphUpdater
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.DataSetPreProcessor
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.heartbeat.reports.Task
import java.io._
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Utility class suited to save/restore neural net models
 *
 * @author raver119@gmail.com
 */
object ModelSerializer {
  val OLD_UPDATER_BIN: String = "updater.bin"
  val UPDATER_BIN: String = "updaterState.bin"

  /**
   * Write a model to a file
   * @param model the model to write
   * @param file the file to write to
   * @param saveUpdater whether to save the updater or not
   * @throws IOException
   */
  @throws(classOf[IOException])
  def writeModel(@NonNull model: Nothing, @NonNull file: File, saveUpdater: Boolean) {
    try {
      val stream: BufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))
      try {
        writeModel(model, stream, saveUpdater)
        stream.flush
        stream.close
      } finally {
        if (stream != null) stream.close()
      }
    }
  }

  /**
   * Write a model to a file path
   * @param model the model to write
   * @param path the path to write to
   * @param saveUpdater whether to save the updater
   *                    or not
   * @throws IOException
   */
  @throws(classOf[IOException])
  def writeModel(@NonNull model: Nothing, @NonNull path: String, saveUpdater: Boolean) {
    try {
      val stream: BufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path))
      try {
        writeModel(model, stream, saveUpdater)
        stream.flush
        stream.close
      } finally {
        if (stream != null) stream.close()
      }
    }
  }

  /**
   * Write a model to an output stream
   * @param model the model to save
   * @param stream the output stream to write to
   * @param saveUpdater whether to save the updater for the model or not
   * @throws IOException
   */
  @throws(classOf[IOException])
  def writeModel(@NonNull model: Nothing, @NonNull stream: OutputStream, saveUpdater: Boolean) {
    val zipfile: ZipOutputStream = new ZipOutputStream(new CloseShieldOutputStream(stream))
    var json: String = ""
    if (model.isInstanceOf[Nothing]) {
      json = (model.asInstanceOf[Nothing]).getLayerWiseConfigurations.toJson
    }
    else if (model.isInstanceOf[Nothing]) {
      json = (model.asInstanceOf[Nothing]).getConfiguration.toJson
    }
    val config: ZipEntry = new ZipEntry("configuration.json")
    zipfile.putNextEntry(config)
    writeEntry(new ByteArrayInputStream(json.getBytes), zipfile)
    val coefficients: ZipEntry = new ZipEntry("coefficients.bin")
    zipfile.putNextEntry(coefficients)
    var bos: ByteArrayOutputStream = new ByteArrayOutputStream
    var dos: DataOutputStream = new DataOutputStream(bos)
    Nd4j.write(model.params, dos)
    dos.flush
    dos.close
    var inputStream: InputStream = new ByteArrayInputStream(bos.toByteArray)
    writeEntry(inputStream, zipfile)
    if (saveUpdater) {
      var updaterState: INDArray = null
      if (model.isInstanceOf[Nothing]) {
        updaterState = (model.asInstanceOf[Nothing]).getUpdater.getStateViewArray
      }
      else if (model.isInstanceOf[Nothing]) {
        updaterState = (model.asInstanceOf[Nothing]).getUpdater.getStateViewArray
      }
      if (updaterState != null && updaterState.length > 0) {
        val updater: ZipEntry = new ZipEntry(UPDATER_BIN)
        zipfile.putNextEntry(updater)
        bos = new ByteArrayOutputStream
        dos = new DataOutputStream(bos)
        Nd4j.write(updaterState, dos)
        dos.flush
        dos.close
        inputStream = new ByteArrayInputStream(bos.toByteArray)
        writeEntry(inputStream, zipfile)
      }
    }
    zipfile.flush
    zipfile.close
  }

  @throws(classOf[IOException])
  private def writeEntry(inputStream: InputStream, zipStream: ZipOutputStream) {
    val bytes: Array[Byte] = new Array[Byte](1024)
    var bytesRead: Int = 0
    while ((({
      bytesRead = inputStream.read(bytes); bytesRead
    })) != -1) {
      zipStream.write(bytes, 0, bytesRead)
    }
  }

  /**
   * Load a multi layer network from a file
   *
   * @param file the file to load from
   * @return the loaded multi layer network
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreMultiLayerNetwork(@NonNull file: File): Nothing = {
    return restoreMultiLayerNetwork(file, true)
  }

  /**
   * Load a multi layer network from a file
   *
   * @param file the file to load from
   * @return the loaded multi layer network
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreMultiLayerNetwork(@NonNull file: File, loadUpdater: Boolean): Nothing = {
    val zipFile: ZipFile = new ZipFile(file)
    var gotConfig: Boolean = false
    var gotCoefficients: Boolean = false
    var gotOldUpdater: Boolean = false
    var gotUpdaterState: Boolean = false
    var gotPreProcessor: Boolean = false
    var json: String = ""
    var params: INDArray = null
    var updater: Nothing = null
    var updaterState: INDArray = null
    var preProcessor: DataSetPreProcessor = null
    val config: ZipEntry = zipFile.getEntry("configuration.json")
    if (config != null) {
      val stream: InputStream = zipFile.getInputStream(config)
      val reader: BufferedReader = new BufferedReader(new InputStreamReader(stream))
      var line: String = ""
      val js: StringBuilder = new StringBuilder
      while ((({
        line = reader.readLine; line
      })) != null) {
        js.append(line).append("\n")
      }
      json = js.toString
      reader.close
      stream.close
      gotConfig = true
    }
    val coefficients: ZipEntry = zipFile.getEntry("coefficients.bin")
    if (coefficients != null) {
      val stream: InputStream = zipFile.getInputStream(coefficients)
      val dis: DataInputStream = new DataInputStream(new BufferedInputStream(stream))
      params = Nd4j.read(dis)
      dis.close
      gotCoefficients = true
    }
    if (loadUpdater) {
      val oldUpdaters: ZipEntry = zipFile.getEntry(OLD_UPDATER_BIN)
      if (oldUpdaters != null) {
        val stream: InputStream = zipFile.getInputStream(oldUpdaters)
        val ois: ObjectInputStream = new ObjectInputStream(stream)
        try {
          updater = ois.readObject.asInstanceOf[Nothing]
        }
        catch {
          case e: ClassNotFoundException => {
            throw new RuntimeException(e)
          }
        }
        gotOldUpdater = true
      }
      val updaterStateEntry: ZipEntry = zipFile.getEntry(UPDATER_BIN)
      if (updaterStateEntry != null) {
        val stream: InputStream = zipFile.getInputStream(updaterStateEntry)
        val dis: DataInputStream = new DataInputStream(stream)
        updaterState = Nd4j.read(dis)
        dis.close
        gotUpdaterState = true
      }
    }
    val prep: ZipEntry = zipFile.getEntry("preprocessor.bin")
    if (prep != null) {
      val stream: InputStream = zipFile.getInputStream(prep)
      val ois: ObjectInputStream = new ObjectInputStream(stream)
      try {
        preProcessor = ois.readObject.asInstanceOf[DataSetPreProcessor]
      }
      catch {
        case e: ClassNotFoundException => {
          throw new RuntimeException(e)
        }
      }
      gotPreProcessor = true
    }
    zipFile.close
    if (gotConfig && gotCoefficients) {
      val confFromJson: Nothing = MultiLayerConfiguration.fromJson(json)
      val network: Nothing = new Nothing(confFromJson)
      network.init(params, false)
      if (gotUpdaterState && updaterState != null) {
        network.getUpdater.setStateViewArray(network, updaterState, false)
      }
      else if (gotOldUpdater && updater != null) {
        network.setUpdater(updater)
      }
      return network
    }
    else throw new IllegalStateException("Model wasnt found within file: gotConfig: [" + gotConfig + "], gotCoefficients: [" + gotCoefficients + "], gotUpdater: [" + gotUpdaterState + "]")
  }

  /**
   * Load a MultiLayerNetwork from InputStream from a file
   *
   * @param is the inputstream to load from
   * @return the loaded multi layer network
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreMultiLayerNetwork(@NonNull is: InputStream, loadUpdater: Boolean): Nothing = {
    val tmpFile: File = File.createTempFile("restore", "multiLayer")
    Files.copy(is, Paths.get(tmpFile.getAbsolutePath), StandardCopyOption.REPLACE_EXISTING)
    return restoreMultiLayerNetwork(tmpFile, loadUpdater)
  }

  @throws(classOf[IOException])
  def restoreMultiLayerNetwork(@NonNull is: InputStream): Nothing = {
    return restoreMultiLayerNetwork(is, true)
  }

  /**
   * Load a MultilayerNetwork model from a file
   *
   * @param path path to the model file, to get the computation graph from
   * @return the loaded computation graph
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreMultiLayerNetwork(@NonNull path: String): Nothing = {
    return restoreMultiLayerNetwork(new File(path), true)
  }

  /**
   * Load a MultilayerNetwork model from a file
   * @param path path to the model file, to get the computation graph from
   * @return the loaded computation graph
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreMultiLayerNetwork(@NonNull path: String, loadUpdater: Boolean): Nothing = {
    return restoreMultiLayerNetwork(new File(path), loadUpdater)
  }

  /**
   * Load a computation graph from a file
   * @param path path to the model file, to get the computation graph from
   * @return the loaded computation graph
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreComputationGraph(@NonNull path: String): Nothing = {
    return restoreComputationGraph(new File(path), true)
  }

  /**
   * Load a computation graph from a file
   * @param path path to the model file, to get the computation graph from
   * @return the loaded computation graph
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreComputationGraph(@NonNull path: String, loadUpdater: Boolean): Nothing = {
    return restoreComputationGraph(new File(path), loadUpdater)
  }

  /**
   * Load a computation graph from a InputStream
   * @param is the inputstream to get the computation graph from
   * @return the loaded computation graph
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreComputationGraph(@NonNull is: InputStream, loadUpdater: Boolean): Nothing = {
    val tmpFile: File = File.createTempFile("restore", "compGraph")
    Files.copy(is, Paths.get(tmpFile.getAbsolutePath), StandardCopyOption.REPLACE_EXISTING)
    return restoreComputationGraph(tmpFile, loadUpdater)
  }

  /**
   * Load a computation graph from a InputStream
   * @param is the inputstream to get the computation graph from
   * @return the loaded computation graph
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreComputationGraph(@NonNull is: InputStream): Nothing = {
    return restoreComputationGraph(is, true)
  }

  /**
   * Load a computation graph from a file
   * @param file the file to get the computation graph from
   * @return the loaded computation graph
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreComputationGraph(@NonNull file: File): Nothing = {
    return restoreComputationGraph(file, true)
  }

  /**
   * Load a computation graph from a file
   * @param file the file to get the computation graph from
   * @return the loaded computation graph
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def restoreComputationGraph(@NonNull file: File, loadUpdater: Boolean): Nothing = {
    val zipFile: ZipFile = new ZipFile(file)
    var gotConfig: Boolean = false
    var gotCoefficients: Boolean = false
    var gotOldUpdater: Boolean = false
    var gotUpdaterState: Boolean = false
    var gotPreProcessor: Boolean = false
    var json: String = ""
    var params: INDArray = null
    var updater: Nothing = null
    var updaterState: INDArray = null
    var preProcessor: DataSetPreProcessor = null
    val config: ZipEntry = zipFile.getEntry("configuration.json")
    if (config != null) {
      val stream: InputStream = zipFile.getInputStream(config)
      val reader: BufferedReader = new BufferedReader(new InputStreamReader(stream))
      var line: String = ""
      val js: StringBuilder = new StringBuilder
      while ((({
        line = reader.readLine; line
      })) != null) {
        js.append(line).append("\n")
      }
      json = js.toString
      reader.close
      stream.close
      gotConfig = true
    }
    val coefficients: ZipEntry = zipFile.getEntry("coefficients.bin")
    if (coefficients != null) {
      val stream: InputStream = zipFile.getInputStream(coefficients)
      val dis: DataInputStream = new DataInputStream(stream)
      params = Nd4j.read(dis)
      dis.close
      gotCoefficients = true
    }
    if (loadUpdater) {
      val oldUpdaters: ZipEntry = zipFile.getEntry(OLD_UPDATER_BIN)
      if (oldUpdaters != null) {
        val stream: InputStream = zipFile.getInputStream(oldUpdaters)
        val ois: ObjectInputStream = new ObjectInputStream(stream)
        try {
          updater = ois.readObject.asInstanceOf[Nothing]
        }
        catch {
          case e: ClassNotFoundException => {
            throw new RuntimeException(e)
          }
        }
        gotOldUpdater = true
      }
      val updaterStateEntry: ZipEntry = zipFile.getEntry(UPDATER_BIN)
      if (updaterStateEntry != null) {
        val stream: InputStream = zipFile.getInputStream(updaterStateEntry)
        val dis: DataInputStream = new DataInputStream(stream)
        updaterState = Nd4j.read(dis)
        dis.close
        gotUpdaterState = true
      }
    }
    val prep: ZipEntry = zipFile.getEntry("preprocessor.bin")
    if (prep != null) {
      val stream: InputStream = zipFile.getInputStream(prep)
      val ois: ObjectInputStream = new ObjectInputStream(stream)
      try {
        preProcessor = ois.readObject.asInstanceOf[DataSetPreProcessor]
      }
      catch {
        case e: ClassNotFoundException => {
          throw new RuntimeException(e)
        }
      }
      gotPreProcessor = true
    }
    zipFile.close
    if (gotConfig && gotCoefficients) {
      val confFromJson: Nothing = ComputationGraphConfiguration.fromJson(json)
      val cg: Nothing = new Nothing(confFromJson)
      cg.init(params, false)
      if (gotUpdaterState && updaterState != null) {
        cg.getUpdater.setStateViewArray(updaterState)
      }
      else if (gotOldUpdater && updater != null) {
        cg.setUpdater(updater)
      }
      return cg
    }
    else throw new IllegalStateException("Model wasnt found within file: gotConfig: [" + gotConfig + "], gotCoefficients: [" + gotCoefficients + "], gotUpdater: [" + gotUpdaterState + "]")
  }

  /**
   *
   * @param model
   * @return
   */
  def taskByModel(model: Nothing): Task = {
    val task: Task = new Task
    try {
      task.setArchitectureType(Task.ArchitectureType.RECURRENT)
      if (model.isInstanceOf[Nothing]) {
        task.setNetworkType(Task.NetworkType.ComputationalGraph)
        val network: Nothing = model.asInstanceOf[Nothing]
        try {
          if (network.getLayers != null && network.getLayers.length > 0) {
            import scala.collection.JavaConversions._
            for (layer <- network.getLayers) {
              if (layer.isInstanceOf[Nothing] || layer.isInstanceOf[Nothing]) {
                task.setArchitectureType(Task.ArchitectureType.RBM)
                break //todo: break is not supported
              }
              if (layer.`type` == Layer.Type.CONVOLUTIONAL) {
                task.setArchitectureType(Task.ArchitectureType.CONVOLUTION)
                break //todo: break is not supported
              }
              else if ((layer.`type` == Layer.Type.RECURRENT) || (layer.`type` == Layer.Type.RECURSIVE)) {
                task.setArchitectureType(Task.ArchitectureType.RECURRENT)
                break //todo: break is not supported
              }
            }
          }
          else task.setArchitectureType(Task.ArchitectureType.UNKNOWN)
        }
        catch {
          case e: Exception => {
          }
        }
      }
      else if (model.isInstanceOf[Nothing]) {
        task.setNetworkType(Task.NetworkType.MultilayerNetwork)
        val network: Nothing = model.asInstanceOf[Nothing]
        try {
          if (network.getLayers != null && network.getLayers.length > 0) {
            import scala.collection.JavaConversions._
            for (layer <- network.getLayers) {
              if (layer.isInstanceOf[Nothing] || layer.isInstanceOf[Nothing]) {
                task.setArchitectureType(Task.ArchitectureType.RBM)
                break //todo: break is not supported
              }
              if (layer.`type` == Layer.Type.CONVOLUTIONAL) {
                task.setArchitectureType(Task.ArchitectureType.CONVOLUTION)
                break //todo: break is not supported
              }
              else if ((layer.`type` == Layer.Type.RECURRENT) || (layer.`type` == Layer.Type.RECURSIVE)) {
                task.setArchitectureType(Task.ArchitectureType.RECURRENT)
                break //todo: break is not supported
              }
            }
          }
          else task.setArchitectureType(Task.ArchitectureType.UNKNOWN)
        }
        catch {
          case e: Exception => {
          }
        }
      }
      return task
    }
    catch {
      case e: Exception => {
        task.setArchitectureType(Task.ArchitectureType.UNKNOWN)
        task.setNetworkType(Task.NetworkType.DenseNetwork)
        return task
      }
    }
  }
}

class ModelSerializer {
  private def this() {
    this()
  }
}