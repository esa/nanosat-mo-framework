//------------------------------------------------------------------------------
//
// System : ccsds-common
//
// Sub-System : esa.mo.nmf.log_browser.adapters
//
// File Name : ArchiveToRolesAdapter.java
//
// Author : marcel.mikolajko
//
// Creation Date : 10.08.2021
//
//------------------------------------------------------------------------------
package esa.mo.nmf.log_browser.adapters;

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
 * @author marcel.mikolajko
 */
public class ArchiveToRolesAdapter extends ArchiveAdapter implements QueryStatusProvider{

    private static final Logger LOGGER = Logger.getLogger(ArchiveToRolesAdapter.class.getName());
    private boolean isQueryOver;
    private List<Long> rolesIds = new ArrayList<>();
    private List<String> rolesNames = new ArrayList<>();

    public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        for(int i = 0; i < objDetails.size(); ++i) {
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
//------------------------------------------------------------------------------
