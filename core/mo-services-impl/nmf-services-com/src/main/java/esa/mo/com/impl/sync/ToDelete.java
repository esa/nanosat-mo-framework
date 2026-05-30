package esa.mo.com.impl.sync;

import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * Object types whose instances are purged from the archive during
 * synchronisation. The numeric values are hard-coded because this module
 * (nmf-services-com) cannot depend on the MC or SM API jars.
 *
 * @author Yannick Lavan
 */
public enum ToDelete {
    /** MC::Parameter::ParameterValue (area=4, service=2, version=1, number=3) */
    PARAMETER_VALUE_INSTANCE(4, 2, 1, 3),
    /** MC::Aggregation::AggregationValue (area=4, service=6, version=1, number=3) */
    AGGREGATION_VALUE(4, 6, 1, 3),
    /** SM::CommandExecutor::StandardOutput (area=7, service=3, version=1, number=2) */
    STDOUT_VALUE(7, 3, 1, 2),
    /** SM::CommandExecutor::StandardError (area=7, service=3, version=1, number=3) */
    STDERR_VALUE(7, 3, 1, 3);

    private final ObjectType type;

    ToDelete(int area, int service, int version, int number) {
        this.type = new ObjectType(
                new UShort(area), new UShort(service),
                new UOctet((short) version), new UShort(number));
    }

    public ObjectType getType() {
        return this.type;
    }
}
