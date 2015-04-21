actor {
  var sum = 0
  loop {
    receive {
      case Data(bytes) => sum += hash(bytes)
      case GetSum(requester) => requester ! sum
    }
  }
}

