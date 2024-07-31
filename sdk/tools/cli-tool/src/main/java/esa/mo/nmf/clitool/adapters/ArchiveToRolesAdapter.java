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
package esa.mo.nmf.clitool.adapters;

import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Archive adapter that retrieves login roles
 *
 * @author Marcel Mikołajko
 */
public class ArchiveToRolesAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToRolesAdapter.class.getName());

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver;

    /**
     * Ids of the retrieved roles
     */
    private final List<Long> rolesIds = new ArrayList<>();

    /**
     * Names of the retrieved roles
     */
    private final List<String> rolesNames = new ArrayList<>();

    public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails,
        ElementList objBodies, Map qosProperties) {
        for (int i = 0; i < objDetails.size(); ++i) {
            rolesIds.add(objDetails.get(i).getInstId());
            rolesNames.add(objBodies.get(i).toString());
        }
        setIsQueryOver(true);
    }

    @Override
    public void retrieveAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "retrieveAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void retrieveResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "retrieveResponseErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public boolean isQueryOver() {
        return isQueryOver;
    }

    public void setIsQueryOver(boolean queryOver) {
        isQueryOver = queryOver;
    }

    public List<Long> getRolesIds() {
        return rolesIds;
    }

    public List<String> getRolesNames() {
        return rolesNames;
    }
}
