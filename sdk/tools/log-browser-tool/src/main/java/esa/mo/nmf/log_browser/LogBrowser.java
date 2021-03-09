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

  public static final String APP_NAME = "log-browser-tool";

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    CommandsDefinitions cmdDef = new CommandsDefinitions();
    CommandLine cmd = new CommandLine(cmdDef);
    cmd.setUsageHelpAutoWidth(true);
    cmd.setUsageHelpLongOptionsMaxWidth(30);

    System.exit(cmd.execute(args));
  }
}
