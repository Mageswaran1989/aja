//package org.aja.dhira.nnql
//
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.LinkedList;
//import java.util.List;
//
//import jline.console._;
//import jline.console.completer._;
//
///**
// * Created by mageswaran on 4/5/16.
// */
//object Shell {
//
//}
//
//
///**
// * Sample application to show how jLine can be used.
// *
// * @author Mageswaran D
// *
// */
//class Shell {
//  var commandsList;
//
//  def init() {
//    commandsList = Array[String]("help", "action1", "action2", "exit" )
//  }
//
//  def run()  { //throws IOException
//    printWelcomeMessage();
//    ConsoleReader reader = new ConsoleReader();
//    reader.setBellEnabled(false);
//    List<Completer> completors = new LinkedList<Completer>();
//
//    completors.add(new StringsCompleter(commandsList));
//    reader.addCompleter(new ArgumentCompleter(completors));
//
//    String line;
//    PrintWriter out = new PrintWriter(System.out);
//
//    while ((line = readLine(reader, "")) != null) {
//      if ("help".equals(line)) {
//        printHelp();
//      } else if ("action1".equals(line)) {
//        System.out.println("You have selection action1");
//      } else if ("action2".equals(line)) {
//        System.out.println("You have selection action2");
//      } else if ("exit".equals(line)) {
//        System.out.println("Exiting application");
//        return;
//      } else {
//        System.out
//          .println("Invalid command, For assistance press TAB or type \"help\" then hit ENTER.");
//      }
//      out.flush();
//    }
//  }
//
//  def printWelcomeMessage() {
//		System.out
//				.println("Welcome to jLine Sample App. For assistance press TAB or type \"help\" then hit ENTER.")
//
//	}
//
//  def printHelp() {
//		System.out.println("help		- Show help");
//		System.out.println("action1		- Execute action1");
//		System.out.println("action2		- Execute action2");
//		System.out.println("exit        - Exit the app");
//
//	}
//
//  def readLine(reader: ConsoleReader, promtMessage: String)
//  throws IOException {
//    String line = reader.readLine(promtMessage + "\nshell> ");
//    return line.trim();
//  }
//
//  public static void main(String[] args) throws IOException {
//    Shell shell = new Shell();
//    shell.init();
//    shell.run();
//  }
//}
