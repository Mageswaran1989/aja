package org.aja.dhira.nnql

/**
  * Created by mdhandapani on 18/5/16.
  */
object NNQLCommands {

  class Command()
  case class CreateCommand() extends Command


  class RTActions()
  case class StartAction() extends RTActions
  case class StopAction() extends RTActions
  case class PauseAction() extends RTActions
}
