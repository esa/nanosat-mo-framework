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
package esa.mo.com.impl.archive.fast;

import esa.mo.com.impl.archive.db.DatabaseBackend;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Holds the set of providerURIs that the database contains in its dedicated
 * table and avoids constant checking on it which makes things go much faster.
 */
public class FastProviderURI extends FastIndex<String> {

    private final static String TABLE_NAME = "FastProviderURI";

    public FastProviderURI(final DatabaseBackend dbBackend) {
        super(dbBackend, TABLE_NAME);
    }

    public synchronized Integer getProviderURIId(final URI providerURI) {
        final Integer id = this.fastID.get(providerURI.getValue());
        return (id == null) ? this.addNewEntry(providerURI.getValue()) : id;
    }

    public synchronized URI getProviderURI(final Integer id) throws Exception {
        final URI providerURI = new URI(this.fastIDreverse.get(id));

        if (providerURI == null) {
            throw new Exception();
        }

        return providerURI;
    }
}
