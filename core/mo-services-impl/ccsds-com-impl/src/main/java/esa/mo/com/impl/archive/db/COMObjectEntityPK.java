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

import java.io.Serializable;

/**
 * Archive Persistence Object Primary Key
 *
 * @author Cesar Coelho
 */
public class COMObjectEntityPK implements Serializable {

    private Integer objectTypeId;
    private Integer domainId;
    private Long objId;

    public COMObjectEntityPK(final Integer objectTypeId, final Integer domain, final Long objId) {
        this.objectTypeId = objectTypeId;
        this.domainId = domain;
        this.objId = objId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof COMObjectEntityPK)) {
            return false;
        }
        COMObjectEntityPK input = (COMObjectEntityPK) other;

        return (input.objectTypeId.equals(objectTypeId) &&
                input.domainId.equals(domainId) &&
                input.objId.equals(objId));
    }

    @Override
    public int hashCode() {
        return objectTypeId.hashCode() ^ domainId.hashCode() ^ objId.hashCode();
    }

    public Integer getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(Integer objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }
}
