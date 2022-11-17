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
package esa.mo.nmf.groundmoadapter;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.commonmoadapter.CommonMOAdapterImpl;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.mal.structures.Blob;

/**
 * The implementation of the Ground MO Adapter.
 *
 * @author Cesar Coelho
 */
public class GroundMOAdapterImpl extends CommonMOAdapterImpl {
    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     */
    public GroundMOAdapterImpl(final ConnectionConsumer connection) {
        super(connection);
        // No need for super.init(); here and in other constructors because CommonMOAdapterImpl constructor already calls it
    }

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public GroundMOAdapterImpl(final ConnectionConsumer connection, final Blob authenticationId,
        final String localNamePrefix) {
        super(connection, authenticationId, localNamePrefix);
    }

    /**
     * The constructor of this class
     *
     * @param providerDetails The Provider details. This object can be obtained from the Directory
     *                        service
     */
    public GroundMOAdapterImpl(final ProviderSummary providerDetails) {
        super(providerDetails);
    }

    /**
     * The constructor of this class
     *
     * @param providerDetails The Provider details. This object can be obtained from the Directory
     *                        service
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public GroundMOAdapterImpl(final ProviderSummary providerDetails, final Blob authenticationId,
        final String localNamePrefix) {
        super(providerDetails, authenticationId, localNamePrefix);
    }
}
