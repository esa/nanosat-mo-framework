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
import org.ccsds.moims.mo.mal.structures.Identifier;

/**
 * Holds the set of networks that the database contains in its dedicated table
 * and avoids constant checking on it which makes things go much faster.
 */
public class FastNetwork extends FastIndex<String> {

    private final static String TABLE_NAME = "FastNetwork";

    public FastNetwork(final DatabaseBackend dbBackend) {
        super(dbBackend, TABLE_NAME);
    }

    public synchronized Integer getNetworkId(final Identifier network) {
        final Integer id = this.fastID.get(network.getValue());
        return (id == null) ? this.addNewEntry(network.getValue()) : id;
    }

    public synchronized Identifier getNetwork(final Integer id) throws Exception {
        final String network = this.fastIDreverse.get(id);

        if (network == null) {
            throw new Exception();
        }

        return new Identifier(network);
    }
}
