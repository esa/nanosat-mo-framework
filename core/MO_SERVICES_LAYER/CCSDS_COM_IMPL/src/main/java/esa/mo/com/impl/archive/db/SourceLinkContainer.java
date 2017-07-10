/* ----------------------------------------------------------------------------
 * Copyright (C) 2016      European Space Agency
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

    public SourceLinkContainer(final Integer objectTypeId, final Integer domainId, final Long objId) {
        this.objectTypeId = objectTypeId;
        this.domainId = domainId;
        this.objId = objId;
    }

    public Integer getObjectTypeId() {
        return objectTypeId;
    }

    public Integer getDomainId() {
        return domainId;
    }

    public Long getObjId() {
        return objId;
    }

    public IntegerList getDomainIds() {
        return domainIds;
    }

    public void setDomainIds(final IntegerList domainIds) {
        this.domainIds = domainIds;
    }

    public IntegerList getObjectTypeIds() {
        return objectTypeIds;
    }

    public void setObjectTypeIds(final IntegerList objectTypeIds) {
        this.objectTypeIds = objectTypeIds;
    }
    
}
