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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.consumer.PackageManagementAdapter;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.consumer.PackageManagementStub;
import picocli.CommandLine;

/**
 * The PackageManagementCommands class contains the static classes for the
 * Package Management service. Launcher service.
 *
 * @author Cesar Coelho
 */
public class PackageManagementCommands {

    private static final Logger LOGGER = Logger.getLogger(PackageManagementCommands.class.getName());

    @CommandLine.Command(name = "findPackage",
            description = "The findPackage operation allows a consumer to find the available packages on the provider.")
    public static class FindPackage extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<packageName>",
                index = "0", description = "Name of the package to find.")
        String name;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getPackageManagementService() == null) {
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
                LOGGER.log(Level.SEVERE, "Error during the execution of the findPackage operation!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    @CommandLine.Command(name = "install",
            description = "The install operation allows a consumer to install the content of a package on the provider.")
    public static class Install extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<packageName>",
                index = "0", description = "Name of the package to be installed.")
        String name;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getPackageManagementService() == null) {
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
                    if (!isInstalled) {
                        packageManagement.install(names, new PackageManagementAdapter() {
                            @Override
                            public void installAckReceived(MALMessageHeader msgHeader, BooleanList integrity, Map qosProperties) {
                                LOGGER.log(Level.INFO, "Installing...");
                            }

                            @Override
                            public void installResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
                                LOGGER.log(Level.INFO, "Installed successfully");
                            }

                            @Override
                            public void installAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
                                LOGGER.log(Level.SEVERE, "There was an error during the install operation.", error);
                            }

                            @Override
                            public void installResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
                                LOGGER.log(Level.SEVERE, "There was an error during the install operation.", error);
                            }
                        });
                        System.out.println("Package name: " + packageName +  "  (installed)");
                    }
                }
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during the execution of the install operation!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    @CommandLine.Command(name = "uninstall",
            description = "The uninstall operation allows a consumer to uninstall the content of a package on the provider.")
    public static class Uninstall extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<packageName>",
                index = "0", description = "Name of the package to be uninstalled.")
        String name;

        @CommandLine.Option(names = {"-k", "--keepConfiguration"}, paramLabel = "<keepConfiguration>",
                description = "It specifies if the existing configuration is to be kept.")
        boolean keepConfiguration;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getPackageManagementService() == null) {
                System.out.println("Package Management service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                PackageManagementStub packageManagement = getPackageManagement();
                IdentifierList names = new IdentifierList();
                names.add(new Identifier(name));
                BooleanList keepConfigurations = new BooleanList();
                keepConfigurations.add(keepConfiguration);

                packageManagement.uninstall(names,
                        keepConfigurations,
                        new PackageManagementAdapter() {
                            @Override
                            public void uninstallAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
                                LOGGER.log(Level.INFO, "Uninstalling...");
                            }

                            @Override
                            public void uninstallResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
                                LOGGER.log(Level.INFO, "Uninstalled successfully");
                            }

                            @Override
                            public void uninstallAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
                                LOGGER.log(Level.SEVERE, "There was an error during the uninstall operation.", error);
                            }

                            @Override
                            public void uninstallResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
                                LOGGER.log(Level.SEVERE, "There was an error during the uninstall operation.", error);
                            }
                        }
                );
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during the execution of the uninstall operation!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    @CommandLine.Command(name = "upgrade",
            description = "The upgrade operation allows a consumer to upgrade the content of a package on the provider.")
    public static class Upgrade extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<packageName>",
                index = "0", description = "Name of the package to be upgraded.")
        String name;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getPackageManagementService() == null) {
                System.out.println("Package Management service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                PackageManagementStub packageManagement = getPackageManagement();
                IdentifierList names = new IdentifierList();
                names.add(new Identifier(name));
                packageManagement.upgrade(names,
                        new PackageManagementAdapter() {
                            @Override
                            public void upgradeAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
                                LOGGER.log(Level.INFO, "Upgrading...");
                            }

                            @Override
                            public void upgradeResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
                                LOGGER.log(Level.INFO, "Upgraded successfully");
                            }

                            @Override
                            public void upgradeAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
                                LOGGER.log(Level.SEVERE, "There was an error during the upgrade operation.", error);
                            }

                            @Override
                            public void upgradeResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
                                LOGGER.log(Level.SEVERE, "There was an error during the upgrade operation.", error);
                            }
                        }
                );
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during the execution of the upgrade operation!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    public static PackageManagementStub getPackageManagement() {
        return consumer.getSMServices().getPackageManagementService().getPackageManagementStub();
    }
}
