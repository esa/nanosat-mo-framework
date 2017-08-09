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

import esa.mo.com.impl.provider.ActivityTrackingProviderServiceImpl;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.provider.EventProviderServiceImpl;
import org.ccsds.moims.mo.mal.MALException;

/**
 * Class holding all the COM services providers. The services can all be
 * initialized automatically or can be set manually.
 */
public class COMServicesProvider {

    private ArchiveProviderServiceImpl archiveService;
    private EventProviderServiceImpl eventService;
    private ActivityTrackingProviderServiceImpl activityTrackingService;

    /**
     * Initializes all the COM services automatically.
     *
     * @throws org.ccsds.moims.mo.mal.MALException
     */
    public void init() throws MALException {
        // Initialize the Archive service
        archiveService = new ArchiveProviderServiceImpl();
        archiveService.init(null);

        eventService = new EventProviderServiceImpl();
        activityTrackingService = new ActivityTrackingProviderServiceImpl();

        // Initialize the Event service (without an Archive)
        eventService.init(archiveService);        
        
        // Set the Archive service in the Event service
        eventService.setArchiveService(archiveService);

        // Start Activity Tracking Service
        activityTrackingService.init(archiveService, eventService);
    }

    public EventProviderServiceImpl getEventService() {
        return this.eventService;
    }

    public ArchiveProviderServiceImpl getArchiveService() {
        return this.archiveService;
    }

    public ActivityTrackingProviderServiceImpl getActivityTrackingService() {
        return this.activityTrackingService;
    }

    /**
     * Sets the Event service provider
     *
     * @param eventService Event service provider
     */
    public void setEventService(EventProviderServiceImpl eventService) {
        this.eventService = eventService;
    }

    /**
     * Sets the Archive service provider
     *
     * @param archiveService Archive service provider
     */
    public void setArchiveService(ArchiveProviderServiceImpl archiveService) {
        this.archiveService = archiveService;
    }

    /**
     * Sets the Activity Tracking service provider
     *
     * @param activityTrackingService Activity Tracking service provider
     */
    public void setActivityTrackingService(ActivityTrackingProviderServiceImpl activityTrackingService) {
        this.activityTrackingService = activityTrackingService;
    }
    
    public void closeAll(){
        this.archiveService.close();
        this.eventService.close();
    }

}
