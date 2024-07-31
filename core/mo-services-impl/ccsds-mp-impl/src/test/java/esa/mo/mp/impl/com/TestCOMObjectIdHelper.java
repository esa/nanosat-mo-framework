
package esa.mo.mp.impl.com;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.junit.Test;

/**
 * TestCOMObjectIdHelper contains unit tests for COMObjectIdHelper static methods
 */
public class TestCOMObjectIdHelper {

    @Test
    public void testGetInstanceId() {
        ObjectId objectId = null;
        Long instanceId = COMObjectIdHelper.getInstanceId(objectId);
        assertNull(instanceId);

        objectId = new ObjectId();
        instanceId = COMObjectIdHelper.getInstanceId(objectId);
        assertNull(instanceId);

        objectId.setKey(new ObjectKey());
        instanceId = COMObjectIdHelper.getInstanceId(objectId);
        assertNull(instanceId);

        objectId.setKey(new ObjectKey(null, 1L));
        instanceId = COMObjectIdHelper.getInstanceId(objectId);
        assertEquals((Long) 1L, (Long) instanceId);
    }
}
