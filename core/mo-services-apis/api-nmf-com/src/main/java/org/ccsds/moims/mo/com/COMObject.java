/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO COM Java API
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
package org.ccsds.moims.mo.com;

import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * Describes a COM object type: its {@link ObjectType}, name and body short form, together
 * with whether it has a related object and a source object, and whether it is an event.
 */
public class COMObject {

    private final ObjectType objectType;
    private final Identifier objectName;
    private final Object bodyShortForm;
    private final boolean hasRelated;
    private final ObjectType relatedType;
    private final boolean hasSource;
    private final ObjectType sourceType;
    private final boolean event;

    /**
     * Creates a COM object descriptor, building its {@link ObjectType} from the area, service,
     * version and object number.
     *
     * @param area the area number of the object type
     * @param service the service number of the object type
     * @param version the area version of the object type
     * @param number the object number of the object type
     * @param name the object name
     * @param bodyShortForm the short form of the object body
     * @param hasRelated whether the object has a related object
     * @param relatedType the related object type, or {@code null} if none
     * @param hasSource whether the object has a source object
     * @param sourceType the source object type, or {@code null} if none
     * @param isEvent whether the object is an event
     */
    public COMObject(UShort area, UShort service, UOctet version, UShort number,
            Identifier name, Object bodyShortForm, boolean hasRelated, ObjectType relatedType,
            boolean hasSource, ObjectType sourceType, boolean isEvent) {
        this.objectType = new ObjectType(area, service, version, number);
        this.objectName = name;
        this.bodyShortForm = bodyShortForm;
        this.hasRelated = hasRelated;
        this.relatedType = relatedType;
        this.hasSource = hasSource;
        this.sourceType = sourceType;
        this.event = isEvent;
    }

    /**
     * Creates a COM object descriptor from an existing {@link ObjectType}.
     *
     * @param objectType the object type
     * @param name the object name
     * @param bodyShortForm the short form of the object body
     * @param hasRelated whether the object has a related object
     * @param relatedType the related object type, or {@code null} if none
     * @param hasSource whether the object has a source object
     * @param sourceType the source object type, or {@code null} if none
     * @param isEvent whether the object is an event
     */
    public COMObject(ObjectType objectType, Identifier name, Object bodyShortForm, boolean hasRelated,
            ObjectType relatedType, boolean hasSource, ObjectType sourceType, boolean isEvent) {
        this.objectType = objectType;
        this.objectName = name;
        this.bodyShortForm = bodyShortForm;
        this.hasRelated = hasRelated;
        this.relatedType = relatedType;
        this.hasSource = hasSource;
        this.sourceType = sourceType;
        this.event = isEvent;
    }

    /**
     * Returns the object type.
     *
     * @return the object type
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Returns the object name.
     *
     * @return the object name
     */
    public Identifier getObjectName() {
        return objectName;
    }

    /**
     * Returns the short form of the object body.
     *
     * @return the body short form
     */
    public Object getBodyShortForm() {
        return bodyShortForm;
    }

    /**
     * Returns whether the object has a related object.
     *
     * @return {@code true} if the object has a related object
     */
    public boolean hasRelated() {
        return hasRelated;
    }

    /**
     * Returns the related object type.
     *
     * @return the related object type, or {@code null} if none
     */
    public ObjectType getRelatedType() {
        return relatedType;
    }

    /**
     * Returns whether the object has a source object.
     *
     * @return {@code true} if the object has a source object
     */
    public boolean hasSource() {
        return hasSource;
    }

    /**
     * Returns the source object type.
     *
     * @return the source object type, or {@code null} if none
     */
    public ObjectType getSourceType() {
        return sourceType;
    }

    /**
     * Returns whether the object is an event.
     *
     * @return {@code true} if the object is an event
     */
    public boolean isEvent() {
        return event;
    }
}
