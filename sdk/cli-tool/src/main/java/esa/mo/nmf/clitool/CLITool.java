/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.nmf.clitool;

/**
 * @author marcel.mikolajko
 */
public class CLITool {

    public static final String APP_NAME = "cli-consumer";

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("(1) The CLI Tool is starting...");
        Runtime.getRuntime().addShutdownHook(new Thread(BaseCommand::closeConsumer));
        System.out.println("(2) The CLI Tool is dispaching the command...");
        int exitCode = Dispatcher.dispatch(args);
        long startupTime = System.currentTimeMillis() - startTime;
        System.out.println("(3) The CLI Tool was executed in: " + startupTime + " ms");
        System.out.flush();
        System.exit(exitCode);
    }
}
