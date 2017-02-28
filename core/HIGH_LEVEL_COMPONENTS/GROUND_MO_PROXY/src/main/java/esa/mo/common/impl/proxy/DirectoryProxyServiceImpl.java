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
package esa.mo.common.impl.proxy;

import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 */
public class DirectoryProxyServiceImpl extends DirectoryProviderServiceImpl {



    public PublishDetails fetchAndLoadFromRemoteDirectoryService(final URI centralDirectoryServiceURI) {
        // To be done...
        
        // Generate the PublishDetails object from the Central Directory URI
        
        // Connect there, and retrieve the list
        
        
        return null;
    }

}
