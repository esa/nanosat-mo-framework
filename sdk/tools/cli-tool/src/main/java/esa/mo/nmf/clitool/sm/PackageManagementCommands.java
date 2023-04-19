/* ----------------------------------------------------------------------------
 * Copyright (C) 2023      European Space Agency
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
package esa.mo.nmf.clitool.sm;

import esa.mo.nmf.clitool.BaseCommand;
import static esa.mo.nmf.clitool.BaseCommand.consumer;
import esa.mo.nmf.clitool.ExitCodes;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.consumer.PackageManagementStub;
import picocli.CommandLine;

/**
 * The AppsLauncherCommands class contains the static classes for the Apps
 * Launcher service.
 *
 * @author Cesar Coelho
 */
public class PackageManagementCommands {

    private static final Logger LOGGER = Logger.getLogger(PackageManagementCommands.class.getName());

    @CommandLine.Command(name = "findPackage", description = "The findPackage operation allows a consumer to find the available packages on the provider.")
    public static class FindPackage extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<appName>", index = "0", description = "Name of the package to find.")
        String name;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Package Management service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                PackageManagementStub packageManagement = getPackageManagement();
                IdentifierList names = new IdentifierList();
                names.add(new Identifier(name));
                FindPackageResponse response = packageManagement.findPackage(names);
                for (int i = 0; i < response.getBodyElement0().size(); i++) {
                    String packageName = response.getBodyElement0().get(i).getValue();
                    Boolean isInstalled = response.getBodyElement1().get(i);
                    String installedStr = isInstalled ? "  (installed)" : "";
                    System.out.println("Package name: " + packageName + installedStr);
                }
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during runApp!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    public static PackageManagementStub getPackageManagement() {
        return consumer.getSMServices().getPackageManagementService().getPackageManagementStub();
    }
}
