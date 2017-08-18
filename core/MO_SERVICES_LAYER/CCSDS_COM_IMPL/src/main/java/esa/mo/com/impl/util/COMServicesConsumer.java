/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.com.impl.util;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.consumer.ArchiveSyncConsumerServiceImpl;
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archivesync.ArchiveSyncHelper;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;

/**
 * Class holding all the COM services consumers. The services can all be
 * initialized automatically or can be set manually.
 */
public class COMServicesConsumer {

    private EventConsumerServiceImpl eventService;
    private ArchiveConsumerServiceImpl archiveService;
    private ArchiveSyncConsumerServiceImpl archiveSyncService;

    /**
     * Initializes all the COM services consumer side automatically from the
     * connectionConsumer holding the details of the connections.
     *
     * @param connectionConsumer Connection details
     */
    public void init(final ConnectionConsumer connectionConsumer) {
        SingleConnectionDetails details;

        try {
            // Initialize the Archive service
            details = connectionConsumer.getServicesDetails().get(ArchiveHelper.ARCHIVE_SERVICE_NAME);
            if (details != null) {
                archiveService = new ArchiveConsumerServiceImpl(details);
            }

            // Initialize the Event service (without an Archive)
            details = connectionConsumer.getServicesDetails().get(EventHelper.EVENT_SERVICE_NAME);
            if (details != null) {
                eventService = new EventConsumerServiceImpl(details);
            }

            // Initialize the Event service (without an Archive)
            details = connectionConsumer.getServicesDetails().get(ArchiveSyncHelper.ARCHIVESYNC_SERVICE_NAME);
            if (details != null) {
                archiveSyncService = new ArchiveSyncConsumerServiceImpl(details);
            }
        } catch (MALException ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EventConsumerServiceImpl getEventService() {
        return this.eventService;
    }

    public ArchiveConsumerServiceImpl getArchiveService() {
        return this.archiveService;
    }

    public ArchiveSyncConsumerServiceImpl getArchiveSyncService() {
        return this.archiveSyncService;
    }

    /**
     * Sets manually all the COM consumer services
     *
     * @param eventService Event service consumer
     * @param archiveService Archive service consumer
     */
    public void setServices(EventConsumerServiceImpl eventService, ArchiveConsumerServiceImpl archiveService) {
        this.eventService = eventService;
        this.archiveService = archiveService;
    }

    /**
     * Sets the Event service consumer
     *
     * @param eventService Event service consumer
     */
    public void setEventService(EventConsumerServiceImpl eventService) {
        this.eventService = eventService;
    }

    /**
     * Sets the Archive service consumer
     *
     * @param archiveService Archive service consumer
     */
    public void setArchiveService(ArchiveConsumerServiceImpl archiveService) {
        this.archiveService = archiveService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if (this.eventService != null) {
            this.eventService.closeConnection();
        }

        if (this.archiveService != null) {
            this.archiveService.closeConnection();
        }

        if (this.archiveSyncService != null) {
            this.archiveSyncService.closeConnection();
        }
    }

}
