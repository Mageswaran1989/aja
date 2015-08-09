package simulator {

abstract class Simulation {
  type Action = () => Unit
  case class WorkItem(time: Int, action: Action)

  private var curTime = 0
  def currentTime: Int = curTime

  private var agenta: List[WorkItem] = List()
  private def insert(ag: List[WorkItem], item: WorkItem): List[WorkItem] = {
    if (ag.isEmpty || item.time < ag.head.time)
      item :: ag
    else
      ag.head :: insert(ag.tail, item)
  }

  //by-name parameter block: => Unit
  def afterDelay(delay: Int)(block: => Unit)
  {
    val item = WorkItem(currentTime + delay, () => block)
    agenta = insert(agenta, item)
  }

  private def next() 
  {
    //annotations is used to avoid the warning on Nil combination, as it was already chaeked in next()
    (agenta: @unchecked) match 
    {
      case item :: rest => agenta = rest; 
      curTime = item.time
      item.action()
    }
  }

  def run() 
  {
    afterDelay(0)
    {
      println("*** Simulation started, time = " + currentTime + " ***")
    } 
    while (!agenta.isEmpty) 
    {
      next()
    }
  }

}

abstract class BasicCircuitSimulation extends Simulation {
  def InverterDelay: Int
  def AndGateDelay: Int
  def OrGateDelay: Int

  class Wire {
    private var sigVal = false
    private var actions: List[Action] = List()
    def getSignal = sigVal
    def setSignal(s: Boolean) = {
      if (s != sigVal) {
        sigVal = s
        actions foreach(_ ()) // actions.foreach(f => f()) takes function and applies to empty parameter
      }
    }
    def addAction(a: Action) = {
      actions = a :: actions
      a()
    }
  }

  def inverter(input: Wire, output: Wire) = {
    def invertAction() {
      val inputSig = input.getSignal
      afterDelay(InverterDelay) {
        output setSignal !inputSig
      }
    }
    input addAction invertAction
  }

  def andGate(a1: Wire, a2: Wire, output: Wire) = {
    def andAction() = {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(AndGateDelay) {
        output setSignal (a1Sig & a2Sig)
      }
    }
    a1 addAction andAction
    a2 addAction andAction
  }

  def orGate(a1: Wire, a2: Wire, output: Wire)= {
    def orAction() = {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(OrGateDelay) {
        output setSignal (a1Sig | a2Sig)
      }
    }
    a1 addAction orAction
    a2 addAction orAction
  }

  def probe(name: String, wire: Wire) {
    def probeAction() {
      println(name + " " + currentTime + " newvalue = " + wire.getSignal)
    }
    wire addAction probeAction
  }
}

abstract class CircuitSimulation extends BasicCircuitSimulation {

  /*
  Half Adder
  A -------------)
       |         )-----D-----------|
    ---|---------)                 |----------S-----
    |  |              ---|~---E----|
    |  |              |
    |  |              |
    |  --------|      |
    |          |-------------------------------C----
  B -----------|
  */

  def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire)
  {
    val d, e = new Wire
    orGate(a,b,d)
    andGate(a,b,c)
    inverter(c,e)
    andGate(d,e,s)
  }

  /*

  Full Adder
                 ------------
  B -------------|          |----------------Sum
      --------   |   H.A    |
  A --| H.A  |-S-|          |----C2---)
      |      |   ------------         )-------Cout
  Cin-|      |-------------------C1---)
      --------

  */

  def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire)
  {
    val s, c1, c2 = new Wire
    halfAdder(a, cin, s, c1)
    halfAdder(b, s, sum, c2)
    orGate(c2, c1, cout)
  }

}

}//end of package


import simulator._

object MySimulation extends CircuitSimulation {
  def InverterDelay = 1
  def AndGateDelay = 3
  def OrGateDelay = 5

  def main(args: Array[String]) = {
    val input1, input2, sum, carry = new Wire
    probe("sum", sum)
    probe("carry", carry)

    halfAdder(input1, input2, sum, carry)
    input1 setSignal true
    run()
    input2 setSignal true
    run()
  }
}

