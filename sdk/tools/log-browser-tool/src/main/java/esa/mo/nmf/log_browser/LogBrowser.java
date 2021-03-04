// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser;

import picocli.CommandLine;

/**
 * Main entry point to start the LogBrowser tool.
 *
 * @author Tanguy Soto
 */
public class LogBrowser {

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    CommandsImplementations cmdImpl = new CommandsImplementations();
    CommandsDefinitions cmdDef = new CommandsDefinitions(cmdImpl);

    int exitCode = new CommandLine(cmdDef).execute(args);
    System.exit(exitCode);
  }
}
