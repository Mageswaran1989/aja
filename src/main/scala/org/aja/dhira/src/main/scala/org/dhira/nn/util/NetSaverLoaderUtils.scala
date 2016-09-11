package org.deeplearning4j.util

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.deeplearning4j.nn.api.Layer
import org.deeplearning4j.nn.api.Updater
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io._
import java.util.HashMap
import java.util.Map

/**
 * Utility to save and load network configuration and parameters.
 */
object NetSaverLoaderUtils {
  private val log: Logger = LoggerFactory.getLogger(classOf[NetSaverLoaderUtils])

  /**
   * Save model configuration and parameters
   * @param net trained network | model
   * @param basePath path to store configuration
   */
  def saveNetworkAndParameters(net: Nothing, basePath: String) {
    val confPath: String = FilenameUtils.concat(basePath, net.toString + "-conf.json")
    val paramPath: String = FilenameUtils.concat(basePath, net.toString + ".bin")
    log.info("Saving model and parameters to {} and {} ...", confPath, paramPath)
    try {
      val dos: DataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(paramPath)))
      try {
        Nd4j.write(net.params, dos)
        dos.flush
        FileUtils.write(new File(confPath), net.getLayerWiseConfigurations.toJson)
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      } finally {
        if (dos != null) dos.close()
      }
    }
  }

  /**
   * Load existing model configuration and parameters
   * @param confPath string path where model configuration is stored
   * @param paramPath string path where parameters are stored
   */
  def loadNetworkAndParameters(confPath: String, paramPath: String): Nothing = {
    log.info("Loading saved model and parameters...")
    var savedNetwork: Nothing = null
    try {
      val confFromJson: Nothing = MultiLayerConfiguration.fromJson(confPath)
      val dis: DataInputStream = new DataInputStream(new FileInputStream(paramPath))
      val newParams: INDArray = Nd4j.read(dis)
      dis.close
      savedNetwork = new Nothing(confFromJson)
      savedNetwork.init
      savedNetwork.setParams(newParams)
    }
    catch {
      case e: IOException => {
        e.printStackTrace
      }
    }
    return savedNetwork
  }

  /**
   * Save model updators
   * @param net trained network | model
   * @param basePath path to store configuration
   */
  def saveUpdators(net: Nothing, basePath: String) {
    val paramPath: String = FilenameUtils.concat(basePath, net.toString + "updators.bin")
    try {
      val oos: ObjectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(paramPath)))
      try {
        oos.writeObject(net.getUpdater)
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      } finally {
        if (oos != null) oos.close()
      }
    }
  }

  /**
   * Load model updators
   * @param updatorPath path of the updators
   *                    Returns saved updaters
   */
  def loadUpdators(updatorPath: String): Nothing = {
    var updater: Nothing = null
    try {
      val oos: ObjectInputStream = new ObjectInputStream(new FileInputStream(new File(updatorPath)))
      try {
        updater = oos.readObject.asInstanceOf[Nothing]
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
        case e: ClassNotFoundException => {
          e.printStackTrace
        }
      } finally {
        if (oos != null) oos.close()
      }
    }
    return updater
  }

  /**
   * Save existing parameters for the layer
   * @param param layer parameters in INDArray format
   * @param paramPath string path where parameters are stored
   */
  def saveLayerParameters(param: INDArray, paramPath: String) {
    log.info("Saving parameters to {} ...", paramPath)
    try {
      val dos: DataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(paramPath)))
      try {
        Nd4j.write(param, dos)
        dos.flush
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      } finally {
        if (dos != null) dos.close()
      }
    }
  }

  /**
   * Load existing parameters to the layer
   * @param layer to load the parameters into
   * @param paramPath string path where parameters are stored
   */
  def loadLayerParameters(layer: Nothing, paramPath: String): Nothing = {
    val name: String = layer.conf.getLayer.getLayerName
    log.info("Loading saved parameters for layer {} ...", name)
    try {
      val dis: DataInputStream = new DataInputStream(new FileInputStream(paramPath))
      val param: INDArray = Nd4j.read(dis)
      dis.close
      layer.setParams(param)
    }
    catch {
      case e: IOException => {
        e.printStackTrace
      }
    }
    return layer
  }

  /**
   * Save existing parameters for the network
   * @param net trained network | model
   * @param layerIds list of *int* layer ids
   * @param paramPaths map of layer ids and string paths to store parameters
   */
  def saveParameters(net: Nothing, layerIds: Array[Int], paramPaths: Map[Integer, String]) {
    var layer: Nothing = null
    for (layerId <- layerIds) {
      layer = net.getLayer(layerId)
      if (!layer.paramTable.isEmpty) {
        NetSaverLoaderUtils.saveLayerParameters(layer.params, paramPaths.get(layerId))
      }
    }
  }

  /**
   * Save existing parameters for the network
   * @param net trained network | model
   * @param layerIds list of *string* layer ids
   * @param paramPaths map of layer ids and string paths to store parameters
   */
  def saveParameters(net: Nothing, layerIds: Array[String], paramPaths: Map[String, String]) {
    var layer: Nothing = null
    for (layerId <- layerIds) {
      layer = net.getLayer(layerId)
      if (!layer.paramTable.isEmpty) {
        NetSaverLoaderUtils.saveLayerParameters(layer.params, paramPaths.get(layerId))
      }
    }
  }

  /**
   * Load existing parameters for the network
   * @param net trained network | model
   * @param layerIds list of *int* layer ids
   * @param paramPaths map of layer ids and string paths to find parameters
   */
  def loadParameters(net: Nothing, layerIds: Array[Int], paramPaths: Map[Integer, String]): Nothing = {
    var layer: Nothing = null
    for (layerId <- layerIds) {
      layer = net.getLayer(layerId)
      loadLayerParameters(layer, paramPaths.get(layerId))
    }
    return net
  }

  /**
   * Load existing parameters for the network
   * @param net trained network | model
   * @param layerIds list of *string* layer ids
   * @param paramPaths map of layer ids and string paths to find parameters
   */
  def loadParameters(net: Nothing, layerIds: Array[String], paramPaths: Map[String, String]): Nothing = {
    var layer: Nothing = null
    for (layerId <- layerIds) {
      layer = net.getLayer(layerId)
      loadLayerParameters(layer, paramPaths.get(layerId))
    }
    return net
  }

  /**
   * Create map of *int* layerIds to path
   * @param layerIds list of *string* layer ids
   * @param basePath string path to find parameters
   */
  def getIdParamPaths(basePath: String, layerIds: Array[Int]): Map[Integer, String] = {
    val paramPaths: Map[Integer, String] = new HashMap[Integer, String]
    for (id <- layerIds) {
      paramPaths.put(id, FilenameUtils.concat(basePath, id + ".bin"))
    }
    return paramPaths
  }

  /**
   * Create map of *string* layerIds to path
   * @param layerIds list of *string* layer ids
   * @param basePath string path to find parameters
   */
  def getStringParamPaths(basePath: String, layerIds: Array[String]): Map[String, String] = {
    val paramPaths: Map[String, String] = new HashMap[String, String]
    for (name <- layerIds) {
      paramPaths.put(name, FilenameUtils.concat(basePath, name + ".bin"))
    }
    return paramPaths
  }

  /**
   * Define output directory based on network type
   * @param networkType
   */
  def defineOutputDir(networkType: String): String = {
    val tmpDir: String = System.getProperty("java.io.tmpdir")
    val outputPath: String = File.separator + networkType + File.separator + "output"
    val dataDir: File = new File(tmpDir, outputPath)
    if (!dataDir.getParentFile.exists) dataDir.mkdirs
    return dataDir.toString
  }
}

class NetSaverLoaderUtils {
  private def this() {
    this()
  }
}