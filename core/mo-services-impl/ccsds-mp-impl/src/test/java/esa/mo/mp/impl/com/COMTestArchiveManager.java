
package esa.mo.mp.impl.com;

import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityIdentityDetails;
import org.ccsds.moims.mo.mp.structures.ActivityIdentityDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;

/**
 * COMTestArchiveManager is used by tests to interact with COMArchiveManager
 */
public class COMTestArchiveManager extends
    COMArchiveManager<ActivityIdentityDetails, ActivityIdentityDetailsList, ActivityDefinitionDetails, ActivityDefinitionDetailsList, ActivityInstanceDetails, ActivityInstanceDetailsList, ActivityUpdateDetails, ActivityUpdateDetailsList> {

    public COMTestArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }
}
