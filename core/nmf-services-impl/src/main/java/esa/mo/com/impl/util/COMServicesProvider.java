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
package esa.mo.com.impl.util;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.provider.ArchiveSyncProviderServiceImpl;
import esa.mo.com.impl.provider.DirectoryProviderServiceImpl;
import org.ccsds.moims.mo.mal.MALException;

/**
 * Class holding all the COM services providers. The services can all be initialized automatically
 * or can be set manually.
 */
public class COMServicesProvider {

    private ArchiveProviderServiceImpl archiveService;
    private ArchiveSyncProviderServiceImpl archiveSyncService;
    private final DirectoryProviderServiceImpl directoryService = new DirectoryProviderServiceImpl();

    /**
     * Initializes all the COM services automatically.
     *
     * @throws org.ccsds.moims.mo.mal.MALException if the services could not be initialized.
     */
    public void init() throws MALException {
        // Initialize the Archive service
        archiveService = new ArchiveProviderServiceImpl();
        archiveService.init();

        // Initialize the Directory service
        directoryService.init(this);
    }

    public ArchiveProviderServiceImpl getArchiveService() {
        return this.archiveService;
    }

    public ArchiveSyncProviderServiceImpl getArchiveSyncService() {
        return this.archiveSyncService;
    }

    public DirectoryProviderServiceImpl getDirectoryService() {
        return this.directoryService;
    }

    public void initArchiveSync() throws MALException {
        archiveSyncService = new ArchiveSyncProviderServiceImpl(archiveService.getConnection().getConnectionDetails());
        this.archiveSyncService.init(archiveService.getArchiveManager());
    }

    /**
     * Sets the Archive service provider
     *
     * @param archiveService Archive service provider
     */
    public void setArchiveService(ArchiveProviderServiceImpl archiveService) {
        this.archiveService = archiveService;
    }

    public void closeAll() {
        this.archiveService.close();
    }
}
