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
package esa.mo.ground.constellation.services.sm;

import esa.mo.ground.constellation.ConstellationManager;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.sm.impl.consumer.PackageManagementConsumerServiceImpl;
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
   * TODO: define return type
   */
  public static void getAllPackages(GroundMOAdapterImpl services) {
    serviceSMPackageManagement =
      services.getSMServices().getPackageManagementService();
    IdentifierList idList = new IdentifierList();
    idList.add(new Identifier("*"));

    try {
      serviceSMPackageManagement
        .getPackageManagementStub()
        .asyncFindPackage(
          idList,
          new PackageManagementAdapter() {

            @Override
            public void findPackageResponseReceived(
              MALMessageHeader msgHeader,
              IdentifierList names,
              BooleanList installed,
              Map qosProperties
            ) {
              for (int i = 0; i < names.size(); i++) {
                //packagesTable.addEntry(names.get(i), installed.get(i));
                Logger
                  .getLogger(ConstellationManager.class.getName())
                  .log(
                    Level.INFO,
                    "Package {0}: {1} - {2}",
                    new Object[] { i, names.get(i), installed.get(i) }
                  );
              }
            }

            @Override
            public void findPackageErrorReceived(
              MALMessageHeader msgHeader,
              MALStandardError error,
              Map qosProperties
            ) {
              Logger
                .getLogger(ConstellationManager.class.getName())
                .log(
                  Level.SEVERE,
                  "There was an error during the findPackage operation.",
                  error
                );
            }
          }
        );
    } catch (MALInteractionException | MALException ex) {
      Logger
        .getLogger(ConstellationManager.class.getName())
        .log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Install the given NMF package on the NanoSat Segment
   *
   * @param packageName NMF package name
   */
  public static void installPackage(
    GroundMOAdapterImpl services,
    String packageName
  ) {
    serviceSMPackageManagement =
      services.getSMServices().getPackageManagementService();
    IdentifierList ids = new IdentifierList();
    ids.add(new Identifier(packageName));

    try {
      serviceSMPackageManagement
        .getPackageManagementStub()
        .install(
          ids,
          new PackageManagementAdapter() {

            @Override
            public void installAckReceived(
              org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
              org.ccsds.moims.mo.mal.structures.BooleanList integrity,
              java.util.Map qosProperties
            ) {
              Logger
                .getLogger(ConstellationManager.class.getName())
                .log(Level.INFO, "Installing...");
            }

            @Override
            public void installResponseReceived(
              org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
              java.util.Map qosProperties
            ) {
              Logger
                .getLogger(ConstellationManager.class.getName())
                .log(Level.INFO, "Installed successfully!");
            }

            @Override
            public void installAckErrorReceived(
              org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
              org.ccsds.moims.mo.mal.MALStandardError error,
              java.util.Map qosProperties
            ) {
              String msg = "There was an error during the install operation.";
              Logger
                .getLogger(ConstellationManager.class.getName())
                .log(Level.SEVERE, msg, error);
            }

            @Override
            public void installResponseErrorReceived(
              org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
              org.ccsds.moims.mo.mal.MALStandardError error,
              java.util.Map qosProperties
            ) {
              String msg = "There was an error during the install operation.";
              Logger
                .getLogger(ConstellationManager.class.getName())
                .log(Level.SEVERE, msg, error);
            }
          }
        );
    } catch (MALInteractionException | MALException ex) {
      Logger
        .getLogger(ConstellationManager.class.getName())
        .log(Level.SEVERE, null, ex);
    }
  }
}
