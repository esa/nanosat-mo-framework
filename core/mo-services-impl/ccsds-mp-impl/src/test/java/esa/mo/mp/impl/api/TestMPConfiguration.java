package esa.mo.mp.impl.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.junit.Test;

/**
 * TestMPConfiguration contains unit tests for MPConfiguration
 */
public class TestMPConfiguration {

    MPConfiguration configuration = new MPConfiguration();

    @Test
    public void testGetObjectType() {
        ObjectType requestVersionType = configuration.getObjectType(new RequestVersionDetails());
        assertEquals(PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE, requestVersionType);
    }

    @Test
    public void testGetUnknownObjectType() {
        ObjectType unknownType = configuration.getObjectType(new Identifier());
        assertNull(unknownType);
    }

    @Test
    public void testGetRelatedType() {
        ObjectType relatedType = configuration.getRelatedType(PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE);
        assertEquals(PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE, relatedType);
    }

    @Test
    public void testGetUnknownRelatedType() {
        ObjectType unknownRelatedType = configuration.getRelatedType(PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
        assertNull(unknownRelatedType);
    }

    @Test
    public void testGetInverseRelatedType() {
        ObjectType inverseRelatedType = configuration.getInverseRelatedType(
            PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
        assertEquals(PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE, inverseRelatedType);
    }

    @Test
    public void testGetUnknownInverseRelatedType() {
        ObjectType unknownInverseRelatedType = configuration.getInverseRelatedType(
            PlanningRequestHelper.REQUESTSTATUSUPDATE_OBJECT_TYPE);
        assertNull(unknownInverseRelatedType);
    }

    @Test
    public void testGetConfigurationType() {
        ObjectType configurationType = configuration.getConfigurationType(
            PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
        assertEquals(PlanningRequestHelper.REQUESTIDENTITYTOREQUESTVERSION_OBJECT_TYPE, configurationType);
    }

    @Test
    public void testGetUnknownConfigurationType() {
        ObjectType unknownConfigurationType = configuration.getConfigurationType(
            PlanningRequestHelper.REQUESTSTATUSUPDATE_OBJECT_TYPE);
        assertNull(unknownConfigurationType);
    }
}
