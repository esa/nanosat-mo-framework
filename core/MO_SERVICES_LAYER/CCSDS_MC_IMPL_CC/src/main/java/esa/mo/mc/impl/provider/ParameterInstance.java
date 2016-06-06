/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.mc.impl.provider;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterInstance {

    private final Identifier name;
    private final ParameterValue parameterValue;
    private final ObjectId source;
    private final Time timestamp;

    public ParameterInstance(final Identifier name, final Attribute value,
            final ObjectId source, final Time timestamp) {

        final ParameterValue pValue = new ParameterValue();
        pValue.setRawValue(value);
        pValue.setConvertedValue(null);
        pValue.setValid(true);
        pValue.setInvalidSubState(new UOctet((short) 0));

        this.name = name;
        this.parameterValue = pValue;
        this.source = source;
        this.timestamp = timestamp;
    }

    public ParameterInstance(final Identifier name, final ParameterValue pValue,
            final ObjectId source, final Time timestamp) {
        this.name = name;
        this.parameterValue = pValue;
        this.source = source;
        this.timestamp = timestamp;
    }
    
    public Identifier getName() {
        return this.name;
    }

    public ParameterValue getParameterValue() {
        return this.parameterValue;
    }

    public ObjectId getSource() {
        return this.source;
    }

    public Time getTimestamp() {
        return this.timestamp;
    }

}
