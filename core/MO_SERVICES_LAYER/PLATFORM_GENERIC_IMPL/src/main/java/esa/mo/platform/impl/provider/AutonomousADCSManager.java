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
package esa.mo.platform.impl.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinition;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionBDot;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSingleSpinning;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionTargetTracking;

/**
 *
 * @author Cesar Coelho
 */
public final class AutonomousADCSManager {

    private static final transient int SAVING_PERIOD = 20;  // Used to store the uniqueAValObjId only once every "SAVINGPERIOD" times
    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)

    private final HashMap<Long, AttitudeDefinition> attitudeDefs;
    private final COMServicesProvider comServices;
    private long availableTime = 0;
    private final Object MUTEX = new Object();

    public AutonomousADCSManager(COMServicesProvider comServices) {

        this.comServices = comServices;
        this.attitudeDefs = new HashMap<Long, AttitudeDefinition>();

        if (comServices != null) {  // Do we have COM services?
            if (comServices.getArchiveService() == null) {  // No Archive?
                this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
            } else {

            }
        }

    }

    public Long add(AttitudeDefinition definition, ObjectId source, SingleConnectionDetails connectionDetails) {

        if (comServices.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.attitudeDefs.put(uniqueObjIdDef, definition);

//            this.save();
            return uniqueObjIdDef;
        } else {
            try {
                AttitudeDefinitionList defs = (AttitudeDefinitionList) HelperMisc.element2elementList(definition);
                defs.add(definition);
                ObjectType objType = AutonomousADCSManager.generateDefinitionObjectType(definition);

                // Store the actual Definition
                LongList objIds = comServices.getArchiveService().store(
                        true,
                        objType,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
                        defs,
                        null);

                this.attitudeDefs.put(objIds.get(0), definition);
                return objIds.get(0);
            } catch (MALException ex) {
                Logger.getLogger(AutonomousADCSManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(AutonomousADCSManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(AutonomousADCSManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    protected AttitudeDefinition get(Long objId) {
        return attitudeDefs.get(objId);
    }

    public boolean delete(Long objId) {
        return (this.attitudeDefs.remove(objId) != null);
    }
    
    protected Long list(Identifier input) {
        final LongList objIds = this.listAll();
        for (Long objId : objIds) {
            if (compareName(objId, input)) {
                return objId;
            }
        }
        return null; // Not found!
    }

    protected LongList listAll() {
        LongList list = new LongList();
        list.addAll(attitudeDefs.keySet());
        return list;
    }

    protected boolean exists(Long input) {
        return attitudeDefs.containsKey(input);
    }

    protected Boolean compareName(Long objId, Identifier name) {
        return attitudeDefs.get(objId).getName().equals(name);
    }

    protected ObjectId storeCOMOperationActivity(final MALInteraction interaction) {
        if (comServices.getActivityTrackingService() != null) {
            return comServices.getActivityTrackingService().storeCOMOperationActivity(interaction, null);
        } else {
            return null;
        }
    }

    private static ObjectType generateDefinitionObjectType(AttitudeDefinition definition) {
        if (definition instanceof AttitudeDefinitionBDot) {
            return AutonomousADCSHelper.ATTITUDEDEFINITIONBDOT_OBJECT_TYPE;
        }

        if (definition instanceof AttitudeDefinitionSingleSpinning) {
            return AutonomousADCSHelper.ATTITUDEDEFINITIONSINGLESPINNING_OBJECT_TYPE;
        }

        if (definition instanceof AttitudeDefinitionSunPointing) {
            return AutonomousADCSHelper.ATTITUDEDEFINITIONSUNPOINTING_OBJECT_TYPE;
        }

        if (definition instanceof AttitudeDefinitionTargetTracking) {
            return AutonomousADCSHelper.ATTITUDEDEFINITIONTARGETTRACKING_OBJECT_TYPE;
        }

        if (definition instanceof AttitudeDefinitionNadirPointing) {
            return AutonomousADCSHelper.ATTITUDEDEFINITIONNADIRPOINTING_OBJECT_TYPE;
        }

        return null;
    }

    public String invalidField(AttitudeDefinition attitude) {

        // Add the validation conditions below
        
        // Example: longitude or latitude out of boundaries
        
        
        return null;
        
    }

    public void markAvailableTime(final Duration time) {
        if(time == null){
            this.availableTime = 0;
            return;
        }
        
        if(time.getValue() == 0){
            this.availableTime = 0;
            return;
        }

        this.availableTime = System.currentTimeMillis() + (long) (time.getValue()*1000);
    }

    public Duration getTimeLeft() {
        if (this.availableTime == 0){
            return null; // Return null if the time left is unknown...
        }
        
        return ( (this.availableTime == 0) ? null : new Duration((this.availableTime - System.currentTimeMillis()) * 1000));
    }

}
