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
package esa.mo.nmf.clitool;

import static esa.mo.nmf.clitool.BaseCommand.consumer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;

/**
 * The Helper class includes static methods that can be used by all other
 * clases.
 *
 * @author Cesar Coelho
 */
public class Helper {

    private static final Logger LOGGER = Logger.getLogger(Helper.class.getName());

    public static boolean checkProvider(LongList matchingApps) {
        if (matchingApps.size() != 1) {
            System.out.println("Could not find any apps matching provided name!");

            ArchiveStub archive = consumer.getCOMServices().getArchiveService().getArchiveStub();
            try {
                Map<String, ProviderAppDetails> providers = getProvidersDetails(archive);
                System.out.println("Available apps:");
                for (Map.Entry<String, ProviderAppDetails> entry : providers.entrySet()) {
                    System.out.println(entry.getKey() + " - Running: " + entry.getValue().appDetails.getRunning());
                }
            } catch (MALInteractionException | MALException | InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "Error while retrieving the available Apps!", ex);
            }

            return false;
        }

        return true;
    }

    public static Map<String, ProviderAppDetails> getProvidersDetails(ArchiveStub archive)
            throws MALInteractionException, MALException, InterruptedException {
        final Object lock = new Object();

        ArchiveQueryList queries = new ArchiveQueryList();
        queries.add(new ArchiveQuery(BaseCommand.domain, null, null, 0L, null, null, null, null, null));

        Map<String, ProviderAppDetails> result = new HashMap<>();
        ObjectType appType = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
                AppsLauncherHelper.APPSLAUNCHER_SERVICE_NUMBER, new UOctet((short) 0),
                AppsLauncherHelper.APP_OBJECT_NUMBER);
        archive.query(true, appType, queries, null, new ArchiveAdapter() {
            @Override
            public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                    ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
                for (int i = 0; i < objDetails.size(); ++i) {
                    AppDetails details = (AppDetails) objBodies.get(i);
                    result.put(details.getName().getValue(),
                            new ProviderAppDetails(objDetails.get(i).getInstId(), details));
                }
            }

            @Override
            public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                    ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
                if (objDetails != null) {
                    for (int i = 0; i < objDetails.size(); ++i) {
                        AppDetails details = (AppDetails) objBodies.get(i);
                        result.put(details.getName().getValue(),
                                new ProviderAppDetails(objDetails.get(i).getInstId(), details));
                    }
                }

                synchronized (lock) {
                    lock.notifyAll();
                }
            }

            @Override
            public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                    Map qosProperties) {
                LOGGER.log(Level.SEVERE, "Error during archive query!", error);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }

            @Override
            public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                    Map qosProperties) {
                LOGGER.log(Level.SEVERE, "Error during archive query!", error);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });

        synchronized (lock) {
            lock.wait();
        }

        return result;
    }

    private static class ProviderAppDetails {

        Long id;
        AppDetails appDetails;

        public ProviderAppDetails(Long id, AppDetails appDetails) {
            this.appDetails = appDetails;
            this.id = id;
        }
    }

}
