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
package esa.mo.platform.impl.provider.gen;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSet;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSetList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinition;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionBDot;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSingleSpinning;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionTargetTracking;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;

/**
 *
 * @author Cesar Coelho
 */
public final class AutonomousADCSManager {

    // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdDef = System.currentTimeMillis();
    private final HashMap<Long, AttitudeDefinition> attitudeDefs;
    private final COMServicesProvider comServices;
    private long availableTime = 0;

    public AutonomousADCSManager(final COMServicesProvider comServices) {
        this.comServices = comServices;
        this.attitudeDefs = new HashMap<Long, AttitudeDefinition>();
    }

    public ArchiveProviderServiceImpl getArchiveService() {
        return this.comServices.getArchiveService();
    }

    public synchronized Long add(AttitudeDefinition definition, ObjectId source, URI uri) {
        uniqueObjIdDef++;

        if (comServices.getArchiveService() != null) {
            try {
                AttitudeDefinitionList defs = (AttitudeDefinitionList) HelperMisc.element2elementList(definition);
                defs.add(definition);
                ObjectType objType = AutonomousADCSManager.generateDefinitionObjectType(definition);
                ArchiveDetailsList adl = HelperArchive.generateArchiveDetailsList(null, source, uri);
                adl.get(0).setInstId(uniqueObjIdDef);

                // Store the actual Definition
                comServices.getArchiveService().store(
                        false,
                        objType,
                        ConfigurationProviderSingleton.getDomain(),
                        adl,
                        defs,
                        null);

                this.attitudeDefs.put(uniqueObjIdDef, definition);
                return uniqueObjIdDef;
            } catch (MALException ex) {
                Logger.getLogger(AutonomousADCSManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(AutonomousADCSManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(AutonomousADCSManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return uniqueObjIdDef;
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

    public String invalidField(AttitudeDefinition attitude) {

        // Add the validation conditions below
        // Example: longitude or latitude out of boundaries
        return null;
    }

    public synchronized void markAvailableTime(final Duration time) {
        if (time == null) {
            this.availableTime = 0;
            return;
        }

        if (time.getValue() == 0) {
            this.availableTime = 0;
            return;
        }

        this.availableTime = System.currentTimeMillis() + (long) (time.getValue() * 1000);
    }

    public synchronized Duration getTimeLeft() {
        if (this.availableTime == 0) {
            return null; // Return null if the time left is unknown...
        }

        return ((this.availableTime == 0) ? null : new Duration((this.availableTime - System.currentTimeMillis()) * 1000));
    }

    protected ConfigurationObjectDetails getCurrentConfiguration(final IdentifierList domain) {
        final HashMap<Long, AttitudeDefinition> defObjs = new HashMap(attitudeDefs);
        LongList keys = new LongList();
        keys.addAll(defObjs.keySet());

        ConfigurationObjectSet objsSetBDot = newConfigurationObjectSet(domain,
                AutonomousADCSHelper.ATTITUDEDEFINITIONBDOT_OBJECT_TYPE);
        ConfigurationObjectSet objsSetSunPointing = newConfigurationObjectSet(domain,
                AutonomousADCSHelper.ATTITUDEDEFINITIONSUNPOINTING_OBJECT_TYPE);
        ConfigurationObjectSet objsSetSingleSpinning = newConfigurationObjectSet(domain,
                AutonomousADCSHelper.ATTITUDEDEFINITIONSUNPOINTING_OBJECT_TYPE);
        ConfigurationObjectSet objsSetTargetTracking = newConfigurationObjectSet(domain,
                AutonomousADCSHelper.ATTITUDEDEFINITIONTARGETTRACKING_OBJECT_TYPE);
        ConfigurationObjectSet objsSetNadirPointing = newConfigurationObjectSet(domain,
                AutonomousADCSHelper.ATTITUDEDEFINITIONNADIRPOINTING_OBJECT_TYPE);

        for (int i = 0; i < defObjs.size(); i++) {
            final Long objId = keys.get(i);
            final AttitudeDefinition definition = defObjs.get(objId);

            if (definition instanceof AttitudeDefinitionBDot) {
                objsSetBDot.getObjInstIds().add(objId);
            }

            if (definition instanceof AttitudeDefinitionSunPointing) {
                objsSetSunPointing.getObjInstIds().add(objId);
            }

            if (definition instanceof AttitudeDefinitionSingleSpinning) {
                objsSetSingleSpinning.getObjInstIds().add(objId);
            }

            if (definition instanceof AttitudeDefinitionTargetTracking) {
                objsSetTargetTracking.getObjInstIds().add(objId);
            }

            if (definition instanceof AttitudeDefinitionNadirPointing) {
                objsSetNadirPointing.getObjInstIds().add(objId);
            }
        }

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSetBDot);
        list.add(objsSetSunPointing);
        list.add(objsSetSingleSpinning);
        list.add(objsSetTargetTracking);
        list.add(objsSetNadirPointing);

        // Needs the Common API here!
        return new ConfigurationObjectDetails(list);
    }

    private static ConfigurationObjectSet newConfigurationObjectSet(final IdentifierList domain, final ObjectType objType) {
        ConfigurationObjectSet objsSet = new ConfigurationObjectSet(objType, domain, new LongList());
        return objsSet;
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

    public static AttitudeMode getAttitudeMode(AttitudeDefinition definition) {
        if (definition instanceof AttitudeDefinitionBDot) {
            return AttitudeMode.BDOT;
        }

        if (definition instanceof AttitudeDefinitionSunPointing) {
            return AttitudeMode.SUNPOINTING;
        }

        if (definition instanceof AttitudeDefinitionSingleSpinning) {
            return AttitudeMode.SINGLESPINNING;
        }

        if (definition instanceof AttitudeDefinitionTargetTracking) {
            return AttitudeMode.TARGETTRACKING;
        }

        if (definition instanceof AttitudeDefinitionNadirPointing) {
            return AttitudeMode.NADIRPOINTING;
        }

        return null;
    }

    protected synchronized boolean reloadConfiguration(final IdentifierList domain, ConfigurationObjectDetails configurationObjectDetails) {
        final HashMap<Long, AttitudeDefinition> defs = new HashMap<Long, AttitudeDefinition>();

        for (int i = 0; i < configurationObjectDetails.getConfigObjects().size(); i++) {
            ConfigurationObjectSet confSet = configurationObjectDetails.getConfigObjects().get(i);

            // Confirm the domain
            if (!confSet.getDomain().equals(domain)) {
                return false;
            }

            // Load them from the Archive
            if (confSet.getObjType().equals(AutonomousADCSHelper.ATTITUDEDEFINITIONBDOT_OBJECT_TYPE)) {
                defs.putAll(this.getDefsFromArchive(confSet));
                continue;
            }

            if (confSet.getObjType().equals(AutonomousADCSHelper.ATTITUDEDEFINITIONSINGLESPINNING_OBJECT_TYPE)) {
                defs.putAll(this.getDefsFromArchive(confSet));
                continue;
            }

            if (confSet.getObjType().equals(AutonomousADCSHelper.ATTITUDEDEFINITIONSUNPOINTING_OBJECT_TYPE)) {
                defs.putAll(this.getDefsFromArchive(confSet));
                continue;
            }

            if (confSet.getObjType().equals(AutonomousADCSHelper.ATTITUDEDEFINITIONTARGETTRACKING_OBJECT_TYPE)) {
                defs.putAll(this.getDefsFromArchive(confSet));
                continue;
            }

            if (confSet.getObjType().equals(AutonomousADCSHelper.ATTITUDEDEFINITIONNADIRPOINTING_OBJECT_TYPE)) {
                defs.putAll(this.getDefsFromArchive(confSet));
                continue;
            }

            return false; // One of the sets is Unknown!
        }

        // ok, we're good to go...
        this.attitudeDefs.clear();
        this.attitudeDefs.putAll(defs);

        return true;
    }

    private HashMap<Long, AttitudeDefinition> getDefsFromArchive(final ConfigurationObjectSet confSet) {
        final HashMap<Long, AttitudeDefinition> defs = new HashMap<Long, AttitudeDefinition>();
        AttitudeDefinitionList pDefs = (AttitudeDefinitionList) HelperArchive.getObjectBodyListFromArchive(
                this.getArchiveService(),
                confSet.getObjType(),
                confSet.getDomain(),
                confSet.getObjInstIds());

        for (int j = 0; j < confSet.getObjInstIds().size(); j++) {
            defs.put(confSet.getObjInstIds().get(j), (AttitudeDefinition) pDefs.get(j));
        }

        return defs;
    }

}
