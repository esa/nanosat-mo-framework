package esa.mo.mp.impl.com;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mp.MPHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.junit.BeforeClass;
import org.junit.Test;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.mp.impl.api.MPConfiguration;
import esa.mo.mp.impl.util.MPFactory;

/**
 * TestCOMArchiveManager contains tests for COMArchiveManager worker methods
 */
public class TestCOMArchiveManager {

    private static final Logger LOGGER = Logger.getLogger(TestCOMArchiveManager.class.getName());

    private static COMTestArchiveManager archiveManager = null;
    private static AppTestInteraction interaction = new AppTestInteraction();

    @BeforeClass
    public static void setup() throws MALException {
        System.setProperty("provider.properties", "src/test/resources/testProvider.properties");
        System.setProperty("esa.mo.nanosatmoframework.provider.settings", "src/test/resources/testProvider.properties");
        System.setProperty("transport.properties", "src/test/resources/testTransport.properties");
        HelperMisc.loadPropertiesFile();

        if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
            COMHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(MPHelper.MP_AREA_NAME, MPHelper.MP_AREA_VERSION) == null) {
            MPHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        COMServicesProvider comServices = new COMServicesProvider();
        comServices.init();
        ArchiveProviderServiceImpl archiveService = comServices.getArchiveService();
        archiveService.wipe();

        MPConfiguration configuration = new MPConfiguration();
        archiveManager = new COMTestArchiveManager(comServices, configuration);
    }

    @Test
    public void testAddCOMObjectByIdentity() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("Activity Identity");
        assertNotNull(pair);
    }

    @Test
    public void testAddCOMObjectByInvalidIdentity() throws MALException, MALInteractionException {
        try {
            this.addTestActivity("*");
            fail("No exception thrown");
        } catch (MALInteractionException e) {
            assertEquals(COMHelper.INVALID_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("[0]", e.getStandardError().getExtraInformation().toString());
        }
    }

    @Test
    public void testAddCOMObjectByDuplicateIdentity() throws MALException, MALInteractionException {
        try {
            this.addTestActivity("Duplicate Identity");
            this.addTestActivity("Duplicate Identity");
            fail("No exception thrown");
        } catch (MALInteractionException e) {
            assertEquals(COMHelper.DUPLICATE_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("[0]", e.getStandardError().getExtraInformation().toString());
        }
    }

    @Test
    public void testAddCOMObjectByRelatedId() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("Related Identity");
        ActivityDefinitionDetails definition = MPFactory.createActivityDefinition();
        ObjectId definitionId = archiveManager.addCOMObject(pair.getIdentityId(), definition, null, interaction);
        assertNotNull(definitionId);
    }

    @Test
    public void testAddCOMObjectByInvalidRelatedId() throws MALException, MALInteractionException {
        try {
            ObjectId invalidObjectId = new ObjectId();
            ActivityDefinitionDetails definition = MPFactory.createActivityDefinition();
            archiveManager.addCOMObject(invalidObjectId, definition, null, interaction);
            fail("No exception thrown");
        } catch (MALInteractionException e) {
            assertEquals(COMHelper.INVALID_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("[0]", e.getStandardError().getExtraInformation().toString());
        }
    }

    @Test
    public void testListAllIdentityIds() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("List Identity");
        ObjectIdList ids = archiveManager.listAllIdentityIds(
            PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE);
        assertNotNull(ids);
        assertTrue(ids.size() >= 1);
        assertTrue(ids.contains(pair.getIdentityId()));
    }

    @Test
    public void testListAllObjectIds() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("List Object");
        ObjectIdList ids = archiveManager.listAllObjectIds(
            PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE);
        assertNotNull(ids);
        assertTrue(ids.size() >= 1);
        assertTrue(ids.contains(pair.getObjectId()));
    }

    @Test
    public void testGetIdentityId() throws MALException, MALInteractionException {
        String identityName = "Get Identity Id";
        ObjectIdPair pair = this.addTestActivity(identityName);
        ObjectId id = archiveManager.getIdentityId(new Identifier(identityName),
            PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE);
        assertNotNull(id);
        assertEquals(pair.getIdentityId(), id);
    }

    @Test
    public void testGetObjectByIdentity() throws MALException, MALInteractionException {
        String identityName = "Get Object By Identity";
        this.addTestActivity(identityName);
        Element object = archiveManager.getObject(new Identifier(identityName),
            PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE);
        assertNotNull(object);
        assertTrue(object instanceof ActivityDefinitionDetails);
    }

    @Test
    public void testGetObjectIdByRelatedId() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("Get Object Id By Related Id");
        ObjectId objectId = archiveManager.getObjectIdByRelatedId(pair.getIdentityId());
        assertNotNull(objectId);
        assertEquals(pair.getObjectId(), objectId);
    }

    @Test
    public void testGetObjectIdByInverseRelatedId() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("Get Object Id By Inverse Related Id");
        ObjectId identityId = archiveManager.getObjectIdByInverseRelatedId(pair.getObjectId());
        assertNotNull(identityId);
        assertEquals(pair.getIdentityId(), identityId);
    }

    @Test
    public void testGetObjectByRelatedId() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("Get Object By Related Id");
        Element object = archiveManager.getObjectByRelatedId(pair.getIdentityId());
        assertNotNull(object);
        assertTrue(object instanceof ActivityDefinitionDetails);
    }

    @Test
    public void testGetObject() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("Get Object");
        Element object = archiveManager.getObject(pair.getObjectId());
        assertNotNull(object);
        assertTrue(object instanceof ActivityDefinitionDetails);
    }

    @Test
    public void testGetSourceId() throws MALException, MALInteractionException {
        ObjectIdPair sourcePair = this.addTestActivity("Source");
        ObjectIdPair pair = this.addTestActivity("Get Source Id", sourcePair.getObjectId());
        ObjectId sourceId = archiveManager.getSourceId(pair.getObjectId());
        assertNotNull(sourceId);
        assertEquals(sourcePair.getObjectId(), sourceId);
    }

    @Test
    public void testUpdateObject() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("Update Object");
        ActivityDefinitionDetails updatedDefinition = MPFactory.createActivityDefinition();
        updatedDefinition.setDescription("Updated definition");

        ObjectId objectId = archiveManager.updateCOMObject(pair.getIdentityId(), updatedDefinition, null, interaction);
        assertNotNull(objectId);
        Element object = archiveManager.getObjectByRelatedId(pair.getIdentityId());
        assertNotNull(object);
        assertTrue(object instanceof ActivityDefinitionDetails);
        assertEquals(updatedDefinition, object);
    }

    @Test
    public void testUpdateObjectWithMultiple() throws MALException, MALInteractionException {
        this.addTestActivity("Update Object 1");
        ObjectIdPair pair2 = this.addTestActivity("Update Object 2");

        ActivityInstanceDetails activityInstance2 = MPFactory.createActivityInstance();
        archiveManager.addCOMObject(pair2.getObjectId(), activityInstance2, null, interaction);

        Element object2 = archiveManager.getObjectByRelatedId(pair2.getObjectId());
        assertEquals(activityInstance2, object2);

        ActivityInstanceDetails updatedActivityInstance2 = MPFactory.createActivityInstance();
        updatedActivityInstance2.setComments("Updated activity instance 2");
        archiveManager.updateCOMObject(pair2.getObjectId(), updatedActivityInstance2, null, interaction);

        object2 = archiveManager.getObjectByRelatedId(pair2.getObjectId());
        assertEquals(updatedActivityInstance2, object2);
    }

    @Test
    public void testRemoveObject() throws MALException, MALInteractionException {
        ObjectIdPair pair = this.addTestActivity("Remove Object");
        archiveManager.removeObject(pair.getIdentityId(), interaction);
        Element object = archiveManager.getObjectByRelatedId(pair.getIdentityId());
        assertNull(object);
    }

    private ObjectIdPair addTestActivity(String identityName) throws MALException, MALInteractionException {
        return addTestActivity(identityName, null);
    }

    private ObjectIdPair addTestActivity(String identityName, ObjectId source) throws MALException,
        MALInteractionException {
        Identifier identity = new Identifier(identityName);
        ActivityDefinitionDetails definition = MPFactory.createActivityDefinition();
        return archiveManager.addCOMObject(identity, definition, source, interaction);
    }
}
