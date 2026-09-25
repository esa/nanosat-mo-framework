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

import static esa.mo.nmf.clitool.BaseCommand.consumer;
import esa.mo.nmf.clitool.Args;
import esa.mo.nmf.clitool.BaseCommand;
import esa.mo.nmf.clitool.ExitCodes;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.sm.packagemanagement.consumer.PackageManagementAdapter;
import org.ccsds.moims.mo.sm.packagemanagement.consumer.PackageManagementStub;

/**
 * The PackageManagementCommands class contains the static classes for the
 * Package Management service.
 *
 * @author Cesar Coelho
 */
public class PackageManagementCommands {

    private static final Logger LOGGER
            = Logger.getLogger(PackageManagementCommands.class.getName());

    private PackageManagementCommands() {
    }

    /**
     * Implements the {@code software-management findPackage} CLI command.
     */
    public static class FindPackage extends BaseCommand {
        /**
         * Default constructor.
         */
        public FindPackage() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <packageName>");
                return;
            }
            String name = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getPackageManagementService() == null) {
                System.out.println(
                        "Package Management service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                PackageManagementStub packageManagement = getPackageManagement();
                IdentifierList names = new IdentifierList();
                names.add(new Identifier(name));
                FindPackageResponse response = packageManagement.findPackage(names);
                for (int i = 0; i < response.getNames().size(); i++) {
                    String packageName = response.getNames().get(i).getValue();
                    Boolean isInstalled = response.getInstalled().get(i);
                    String installedStr = isInstalled ? "  (installed)" : "";
                    System.out.println("Package name: " + packageName + installedStr);
                }
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE,
                        "Error during the execution of the findPackage operation!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    /**
     * Implements the {@code software-management install} CLI command.
     */
    public static class Install extends BaseCommand {
        /**
         * Default constructor.
         */
        public Install() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <packageName>");
                return;
            }
            String name = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getPackageManagementService() == null) {
                System.out.println(
                        "Package Management service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                PackageManagementStub packageManagement = getPackageManagement();
                IdentifierList names = new IdentifierList();
                names.add(new Identifier(name));
                FindPackageResponse response = packageManagement.findPackage(names);
                for (int i = 0; i < response.getNames().size(); i++) {
                    String packageName = response.getNames().get(i).getValue();
                    Boolean isInstalled = response.getInstalled().get(i);
                    if (!isInstalled) {
                        packageManagement.install(names,
                                new PackageManagementAdapter() {
                            @Override
                            public void installAckReceived(
                                    MALMessageHeader msgHeader,
                                    BooleanList integrity, Map qosProperties) {
                                LOGGER.log(Level.INFO, "Installing...");
                            }

                            @Override
                            public void installResponseReceived(
                                    MALMessageHeader msgHeader, Map qosProperties) {
                                LOGGER.log(Level.INFO, "Installed successfully");
                            }

                            @Override
                            public void installAckErrorReceived(
                                    MALMessageHeader msgHeader,
                                    MOErrorException error, Map qosProperties) {
                                LOGGER.log(Level.SEVERE,
                                        "There was an error during the install operation.",
                                        error);
                            }

                            @Override
                            public void installResponseErrorReceived(
                                    MALMessageHeader msgHeader,
                                    MOErrorException error, Map qosProperties) {
                                LOGGER.log(Level.SEVERE,
                                        "There was an error during the install operation.",
                                        error);
                            }
                        });
                        System.out.println("Package name: " + packageName + "  (installed)");
                    }
                }
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE,
                        "Error during the execution of the install operation!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    /**
     * Implements the {@code software-management uninstall} CLI command.
     */
    public static class Uninstall extends BaseCommand {
        /**
         * Default constructor.
         */
        public Uninstall() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            boolean keepConfiguration = args.flag("-k", "--keepConfiguration");
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <packageName>");
                return;
            }
            String name = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getPackageManagementService() == null) {
                System.out.println(
                        "Package Management service is not available for this provider!");
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
                    public void uninstallAckErrorReceived(MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE,
                                "There was an error during the uninstall operation.",
                                error);
                    }

                    @Override
                    public void uninstallResponseErrorReceived(MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE,
                                "There was an error during the uninstall operation.",
                                error);
                    }
                }
                );
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE,
                        "Error during the execution of the uninstall operation!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    /**
     * Implements the {@code software-management upgrade} CLI command.
     */
    public static class Upgrade extends BaseCommand {
        /**
         * Default constructor.
         */
        public Upgrade() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <packageName>");
                return;
            }
            String name = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getPackageManagementService() == null) {
                System.out.println(
                        "Package Management service is not available for this provider!");
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
                    public void upgradeAckErrorReceived(MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE,
                                "There was an error during the upgrade operation.",
                                error);
                    }

                    @Override
                    public void upgradeResponseErrorReceived(MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE,
                                "There was an error during the upgrade operation.",
                                error);
                    }
                }
                );
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE,
                        "Error during the execution of the upgrade operation!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    /**
     * Returns the PackageManagement service stub of the connected consumer.
     *
     * @return the PackageManagement service stub
     */
    public static PackageManagementStub getPackageManagement() {
        return consumer.getSMServices()
                .getPackageManagementService().getPackageManagementStub();
    }
}
