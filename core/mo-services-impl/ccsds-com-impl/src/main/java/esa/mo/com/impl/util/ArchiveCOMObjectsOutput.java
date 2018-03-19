/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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
package esa.mo.com.impl.util;

import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

public class ArchiveCOMObjectsOutput {

    private final IdentifierList domain;
    private final ObjectType objType;
    private final ArchiveDetailsList archiveDetailsList;
    private final ElementList objects;

    public ArchiveCOMObjectsOutput(final IdentifierList domain, final ObjectType objType,
            final ArchiveDetailsList archiveDetailsList, final ElementList objects) {
        this.domain = domain;
        this.objType = objType;
        this.archiveDetailsList = archiveDetailsList;
        this.objects = objects;
    }

    public IdentifierList getDomain() {
        return domain;
    }

    public ObjectType getObjectType() {
        return objType;
    }

    public ArchiveDetailsList getArchiveDetailsList() {
        return archiveDetailsList;
    }

    public ElementList getObjectBodies() {
        return objects;
    }

}
