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
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.consumer.PackageManagementAdapter;

/**
 * This class implements the functionality of the
 * NMF Package Management Service for the Constellation Manager.
 */
public class PackageManagerGround {
    private static PackageManagementConsumerServiceImpl serviceSMPackageManagement;

    /**
     * Get all NMF packages that are avaibalbe on the NanoSat Segment
     *
     * @param groundMOAdapter ground MO adapter implementation
     * @return FindPackageResponse: provides the package name and its installation status
     */
    public static FindPackageResponse getAllPackages(GroundMOAdapterImpl groundMOAdapter) {
        serviceSMPackageManagement = groundMOAdapter.getSMServices().getPackageManagementService();
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
     * @param packageName NMF package name
     */
    public static void installPackage(GroundMOAdapterImpl groundMOAdapter, String packageName) {
        serviceSMPackageManagement = groundMOAdapter.getSMServices().getPackageManagementService();
        IdentifierList ids = new IdentifierList();
        ids.add(new Identifier(packageName));

        try {
            serviceSMPackageManagement.getPackageManagementStub().install(ids, new PackageManagementAdapter() {

                @Override
                public void installAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.structures.BooleanList integrity, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Installing...");
                }

                @Override
                public void installResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Installed successfully!");
                }

                @Override
                public void installAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the install operation.";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.PLAIN_MESSAGE);
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, msg, error);
                }

                @Override
                public void installResponseErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the install operation.";
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, msg, error);
                }
            });
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Upgrade the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name
     */
    public static void upgradePackage(GroundMOAdapterImpl groundMOAdapter, String packageName) {
        serviceSMPackageManagement = groundMOAdapter.getSMServices().getPackageManagementService();
        IdentifierList ids = new IdentifierList();
        ids.add(new Identifier(packageName));

        try {
            serviceSMPackageManagement.getPackageManagementStub().upgrade(ids, new PackageManagementAdapter() {
                @Override
                public void upgradeAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Upgrading...");
                }

                @Override
                public void upgradeResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Upgraded!");
                }

                @Override
                public void upgradeAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MOErrorException error, java.util.Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the upgrade operation.", "Error", JOptionPane.PLAIN_MESSAGE);
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "There was an error during the upgrade operation." + "\nException:\n" + error + "\n" + error.toString(), error);
                }

                @Override
                public void upgradeResponseErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MOErrorException error, java.util.Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the upgrade operation.", "Error", JOptionPane.PLAIN_MESSAGE);
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "There was an error during the upgrade operation." + "\nException:\n" + error + "\n" + error.toString(), error);
                }
            });
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * update the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name
     */
    public static void uninstallPackage(GroundMOAdapterImpl groundMOAdapter, String packageName) {
        serviceSMPackageManagement = groundMOAdapter.getSMServices().getPackageManagementService();

        IdentifierList ids = new IdentifierList();
        ids.add(new Identifier(packageName));

        BooleanList keep = new BooleanList();
        keep.add(false);

        try {
            serviceSMPackageManagement.getPackageManagementStub().uninstall(ids, keep, new PackageManagementAdapter() {
                @Override
                public void uninstallAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Uninstalling...");
                }

                @Override
                public void uninstallResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Uninstalled successfully!");
                }

                @Override
                public void uninstallAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the uninstall operation.";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.PLAIN_MESSAGE);
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, msg, error);
                }

                @Override
                public void uninstallResponseErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the uninstall operation.";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.PLAIN_MESSAGE);
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, msg, error);
                }
            });
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
