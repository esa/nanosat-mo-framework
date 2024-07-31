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
package esa.mo.ground.mp;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mp.planinformationmanagement.consumer.PlanInformationManagementStub;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.NumericResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetailsList;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetailsList;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.mp.impl.util.MPFactory;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;

/**
 * Ground consumer: Mission Planning Services
 * Connects to MP provider and adds a single definition for Request, Activity, Event and Resource
 */
public class MPGroundDemo {

    private final GroundMOAdapterImpl gma;

    private static final Logger LOGGER = Logger.getLogger(MPGroundDemo.class.getName());

    private static PlanInformationManagementStub pimService;

    public MPGroundDemo() {
        ConnectionConsumer connection = new ConnectionConsumer();

        try {
            connection.getServicesDetails().loadURIFromFiles();
        } catch (MalformedURLException | FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "The URIs could not be loaded from a file.", ex);
        }

        this.gma = new GroundMOAdapterImpl(connection);
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) {
        MPGroundDemo test = new MPGroundDemo();

        pimService = test.gma.getMPServices().getPlanInformationManagementService().getPlanInformationManagementStub();

        // Add single Activity definition
        ActivityDefinitionDetails activityDefinition = MPFactory.createActivityDefinition();
        activityDefinition.setDescription("NMF Activity");
        activityDefinition.setExecDef(new Identifier("TAKE_PICTURE"));

        ActivityDefinitionDetailsList activityDefinitions = new ActivityDefinitionDetailsList();
        activityDefinitions.add(activityDefinition);
        IdentifierList activityIdentities = new IdentifierList();
        activityIdentities.add(new Identifier("Activity Definition"));
        try {
            pimService.addActivityDef(activityIdentities, activityDefinitions);
        } catch (MALInteractionException | MALException e) {
            LOGGER.log(Level.WARNING, null, e);
        }

        // Add single Event definition
        EventDefinitionDetails eventDefinition = MPFactory.createEventDefinition();
        eventDefinition.setDescription("NMF Event");

        EventDefinitionDetailsList eventDefinitions = new EventDefinitionDetailsList();
        eventDefinitions.add(eventDefinition);
        IdentifierList eventIdentities = new IdentifierList();
        eventIdentities.add(new Identifier("Event Definition"));
        try {
            pimService.addEventDef(eventIdentities, eventDefinitions);
        } catch (MALInteractionException | MALException e) {
            LOGGER.log(Level.WARNING, null, e);
        }

        // Add single Resource definition
        NumericResourceDefinitionDetails numericResourceDefinition = MPFactory.createNumericResourceDefinition();
        numericResourceDefinition.setDescription("NMF Resource");

        c_ResourceDefinitionDetails c_ResourceDefinition = new c_ResourceDefinitionDetails();
        c_ResourceDefinition.setNumericResourceDef(numericResourceDefinition);
        c_ResourceDefinitionDetailsList c_ResourceDefinitions = new c_ResourceDefinitionDetailsList();
        c_ResourceDefinitions.add(c_ResourceDefinition);
        IdentifierList resourceIdentities = new IdentifierList();
        resourceIdentities.add(new Identifier("Resource Definition"));
        try {
            pimService.addResourceDef(resourceIdentities, c_ResourceDefinitions);
        } catch (MALInteractionException | MALException e) {
            LOGGER.log(Level.WARNING, null, e);
        }

        // Add single Request template
        RequestTemplateDetails requestTemplate = MPFactory.createRequestTemplate();
        requestTemplate.setDescription("NMF Request");

        RequestTemplateDetailsList requestTemplates = new RequestTemplateDetailsList();
        requestTemplates.add(requestTemplate);
        IdentifierList requestTemplateIdentities = new IdentifierList();
        requestTemplateIdentities.add(new Identifier("Request Template"));
        try {
            pimService.addRequestDef(requestTemplateIdentities, requestTemplates);
        } catch (MALInteractionException | MALException e) {
            LOGGER.log(Level.WARNING, null, e);
        }

        LOGGER.info("Added static definitions..");
    }
}
