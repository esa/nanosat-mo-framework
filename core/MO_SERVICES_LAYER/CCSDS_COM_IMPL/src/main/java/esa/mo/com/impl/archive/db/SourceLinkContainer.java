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

/**
 *
 * @author Cesar Coelho
 */
public class SourceLinkContainer {

    private final Integer objectTypeId;
    private final Integer domainId;
    private final Long objId;

    public SourceLinkContainer(Integer objectTypeId, Integer domainId, Long objId) {
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
}
