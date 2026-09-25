/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */
package esa.mo.nmf.cmt.services.sm;

import esa.mo.nmf.cmt.ConstellationManagementTool;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.sm.impl.consumer.PackageManagementConsumerServiceImpl;
import javax.swing.*;
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

/**
 * This class implements the functionality of the NMF Package Management Service
 * for the Constellation Manager.
 */
public class PackageManagerGround {

    private static final Logger LOGGER = Logger.getLogger(PackageManagerGround.class.getName());
    private final PackageManagementConsumerServiceImpl serviceSMPackageManagement;
    private final GroundMOAdapterImpl groundMOAdapter;

    public PackageManagerGround(GroundMOAdapterImpl groundMOAdapter) {
        this.groundMOAdapter = groundMOAdapter;
        serviceSMPackageManagement = groundMOAdapter.getSMServices().getPackageManagementService();
    }

    /**
     * Get all NMF packages that are avaibalbe on the NanoSat Segment
     *
     * @return FindPackageResponse: provides the package name and its
     * installation status
     */
    public FindPackageResponse getAllPackages() {
        IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            return serviceSMPackageManagement.getPackageManagementStub().findPackage(idList);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Install the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name.
     */
    public void installPackage(String packageName) {
        IdentifierList ids = new IdentifierList();
        ids.add(new Identifier(packageName));

        try {
            LOGGER.log(Level.INFO, "Calling install on: {0}",
                    serviceSMPackageManagement.getConnectionDetails().getProviderURI().getValue());

            serviceSMPackageManagement.getPackageManagementStub().asyncInstall(ids, new PackageManagementAdapter() {

                @Override
                public void installAckReceived(MALMessageHeader msgHeader,
                        BooleanList integrity, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Installing on satellite: {0}", msgHeader.getFrom());
                }

                @Override
                public void installResponseReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Installed successfully on satellite: {0}", msgHeader.getFrom());
                }

                @Override
                public void installAckErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the install operation.";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, msg, error);
                }

                @Override
                public void installResponseErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the install operation.";
                    LOGGER.log(Level.SEVERE, msg, error);
                }
            });
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Upgrade the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name
     */
    public void upgradePackage(String packageName) {
        IdentifierList ids = new IdentifierList();
        ids.add(new Identifier(packageName));

        try {
            serviceSMPackageManagement.getPackageManagementStub().upgrade(ids, new PackageManagementAdapter() {
                @Override
                public void upgradeAckReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Upgrading...");
                }

                @Override
                public void upgradeResponseReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Upgraded!");
                }

                @Override
                public void upgradeAckErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    JOptionPane.showMessageDialog(null,
                            "There was an error during the upgrade operation.",
                            "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, "There was an error during the upgrade operation."
                            + "\nException:\n" + error + "\n" + error.toString(), error);
                }

                @Override
                public void upgradeResponseErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    JOptionPane.showMessageDialog(null,
                            "There was an error during the upgrade operation.",
                            "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, "There was an error during the upgrade operation."
                            + "\nException:\n" + error + "\n" + error.toString(), error);
                }
            });
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * update the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name
     */
    public void uninstallPackage(String packageName) {
        IdentifierList ids = new IdentifierList();
        ids.add(new Identifier(packageName));

        BooleanList keep = new BooleanList();
        keep.add(false);

        try {
            serviceSMPackageManagement.getPackageManagementStub().uninstall(ids, keep, new PackageManagementAdapter() {
                @Override
                public void uninstallAckReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Uninstalling...");
                }

                @Override
                public void uninstallResponseReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Uninstalled successfully!");
                }

                @Override
                public void uninstallAckErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the uninstall operation.";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, msg, error);
                }

                @Override
                public void uninstallResponseErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the uninstall operation.";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, msg, error);
                }
            });
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
