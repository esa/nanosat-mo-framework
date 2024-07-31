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
package esa.mo.mp.impl.com;

import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;

/**
 * COMConfiguration tracks COM model relationships.
 * <p>
 * COM model is the Identity - Definition - Instance - Status relationship, where Identity, Definition, Instance, Status are COM Objects.
 * <p>
 * The Related links are normally Status -> Instance, Instance -> Definition, and Definition -> Identity.
 * The Inverse Related links are in the opposite direction (e.g. Identity -> Definition).
 * They are represented by "configuration" COM objects, since there are no Inverse Related links in COM Model.
 * <p>
 * Example: ..IdentityTo..Definition configuration COM Object has Related link pointing to Definition and body is the Identity,
 * so the meaning is Identity -> Definition.
 */
public interface COMConfiguration {

    /**
     * Method to identify ObjectType based on given Element
     * @param element which type is to be identified
     * @return the identified ObjectType
     */
    public ObjectType getObjectType(Element element);

    /**
     * Method to get configuration COM ObjectType based on object's related ObjectType
     * @param relatedType object's related ObjectType
     * @return the configuration ObjectType
     */
    public ObjectType getConfigurationType(ObjectType relatedType);

    /**
     * Method to get object's related ObjectType
     * @param objectType object's ObjectType
     * @return the related ObjectType
     */
    public ObjectType getRelatedType(ObjectType objectType);

    /**
     * Method to get object's inverse related ObjectType
     * @param objectType object's ObjectType
     * @return the inverse related ObjectType
     */
    public ObjectType getInverseRelatedType(ObjectType objectType);
}
