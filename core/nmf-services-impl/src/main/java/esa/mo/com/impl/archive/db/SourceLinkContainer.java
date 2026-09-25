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
package esa.mo.com.impl.archive.db;

import org.ccsds.moims.mo.mal.structures.IntegerList;

/**
 * This class holds a source link and optionally, a set of domainIds.
 *
 * @author Cesar Coelho
 */
public class SourceLinkContainer {

    private final Integer objectTypeId;
    private final Integer domainId;
    private final Long objId;
    private IntegerList domainIds;
    private IntegerList objectTypeIds;

    /**
     * Creates a new {@code SourceLinkContainer}.
     *
     * @param objectTypeId the object type id
     * @param domainId the domain id
     * @param objId the object id
     */
    public SourceLinkContainer(final Integer objectTypeId, final Integer domainId, final Long objId) {
        this.objectTypeId = objectTypeId;
        this.domainId = domainId;
        this.objId = objId;
    }

    /**
     * Returns the object type id.
     *
     * @return the object type id
     */
    public Integer getObjectTypeId() {
        return objectTypeId;
    }

    /**
     * Returns the domain id.
     *
     * @return the domain id
     */
    public Integer getDomainId() {
        return domainId;
    }

    /**
     * Returns the object id.
     *
     * @return the object id
     */
    public Long getObjId() {
        return objId;
    }

    /**
     * Returns the domain ids.
     *
     * @return the domain ids
     */
    public IntegerList getDomainIds() {
        return domainIds;
    }

    /**
     * Sets the domain ids.
     *
     * @param domainIds the domain ids
     */
    public void setDomainIds(final IntegerList domainIds) {
        this.domainIds = domainIds;
    }

    /**
     * Returns the object type ids.
     *
     * @return the object type ids
     */
    public IntegerList getObjectTypeIds() {
        return objectTypeIds;
    }

    /**
     * Sets the object type ids.
     *
     * @param objectTypeIds the object type ids
     */
    public void setObjectTypeIds(final IntegerList objectTypeIds) {
        this.objectTypeIds = objectTypeIds;
    }

}
