package esa.mo.com.impl.sync;

import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 *
 * @author Yannick Lavan
 */
public enum ToDelete {
    //ACTIVITY_TRACKING(new UShort(2), new UShort(3), new UOctet((short)1), new UShort(6)),
    ARCHIVE_OBJECT_STORED_EVENT(new UShort(2), new UShort(2), new UOctet((short) 1), new UShort(1)),
    ARCHIVE_OBJECT_UPDATED_EVENT(new UShort(2), new UShort(2), new UOctet((short) 1), new UShort(2)),
    ARCHIVE_OBJECT_DELETED_EVENT(new UShort(2), new UShort(2), new UOctet((short) 1), new UShort(3)),
    PARAMETER_VALUE_INSTANCE(new UShort(4), new UShort(2), new UOctet((short) 1), new UShort(3)), AGGREGATION_VALUE(
        new UShort(4), new UShort(6), new UOctet((short) 1), new UShort(3)), STDOUT_VALUE(new UShort(7), new UShort(3),
            new UOctet((short) 1), new UShort(2)), STDERR_VALUE(new UShort(7), new UShort(3), new UOctet((short) 1),
                new UShort(3));

    private ObjectType type;

    ToDelete(UShort area, UShort service, UOctet version, UShort number) {
        this.type = new ObjectType(area, service, version, number);
    }

    public ObjectType getType() {
        return this.type;
    }
}
