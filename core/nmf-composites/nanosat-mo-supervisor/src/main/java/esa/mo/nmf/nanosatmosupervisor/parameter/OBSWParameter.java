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
package esa.mo.nmf.nanosatmosupervisor.parameter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Holds the information for an OBSW parameter defined in the datapool XML file.
 *
 * @author Tanguy Soto
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OBSWParameter {
    /**
     * Parameter ID (object instance id of the ParameterIdentity).
     */
    @XmlAttribute
    private final Long id;

    /**
     * Parameter name (Identifier, body of the ParameterIdentity)
     */
    @XmlAttribute
    private final String name;

    /**
     * Parameter description (description field of the ParameterDefinitionDetails)
     */
    @XmlAttribute
    private final String description;

    /**
     * Parameter type (rawType field of the ParameterDefinitionDetails)
     */
    @XmlAttribute(name = "attributeType")
    private final String type;

    /**
     * The OBSW aggregation that includes this parameter.
     */
    private OBSWAggregation aggregation;

    public OBSWParameter() {
        super();
        this.id = null;
        this.name = null;
        this.description = null;
        this.type = null;
    }

    /**
     * Creates a new instance of OBSWParameter.
     *
     * @param id
     * @param name
     * @param description
     * @param type
     */
    public OBSWParameter(Long id, String name, String description, String type) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    /**
     * Returns the type.
     *
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the description.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the OBSW aggregation that includes this parameter.
     * 
     * @return The aggregation
     */
    public OBSWAggregation getAggregation() {
        return aggregation;
    }

    /**
     * Sets the OBSW aggregation that includes this parameter.
     * 
     * @param aggregation The aggregation to add
     */
    public void setAggregation(OBSWAggregation aggregation) {
        this.aggregation = aggregation;
    }

    /**
     * Returns the ID.
     *
     * @return The ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("OBSWParameter[id=%s, name=%s, description=%s, type=%s]", id, name, description, type);
    }
}
