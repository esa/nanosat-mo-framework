/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO COM Java API
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
package org.ccsds.moims.mo.com;

import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Identifier;

/**
 *
 */
public class COMObject
{
  private final ObjectType objectType;
  private final Identifier objectName;
  private final Object bodyShortForm;
  private final boolean hasRelated;
  private final ObjectType relatedType;
  private final boolean hasSource;
  private final ObjectType sourceType;
  private final boolean event;

  public COMObject(org.ccsds.moims.mo.mal.structures.UShort area,
          org.ccsds.moims.mo.mal.structures.UShort service,
          org.ccsds.moims.mo.mal.structures.UOctet version,
          org.ccsds.moims.mo.mal.structures.UShort number,
          Identifier name,
          Object bodyShortForm,
          boolean hasRelated,
          ObjectType relatedType,
          boolean hasSource,
          ObjectType sourceType,
          boolean isEvent)
  {
    this.objectType = new ObjectType(area, service, version, number);
    this.objectName = name;
    this.bodyShortForm = bodyShortForm;
    this.hasRelated = hasRelated;
    this.relatedType = relatedType;
    this.hasSource = hasSource;
    this.sourceType = sourceType;
    this.event = isEvent;
  }

  public COMObject(ObjectType objectType,
          Identifier name,
          Object bodyShortForm,
          boolean hasRelated,
          ObjectType relatedType,
          boolean hasSource,
          ObjectType sourceType,
          boolean isEvent)
  {
    this.objectType = objectType;
    this.objectName = name;
    this.bodyShortForm = bodyShortForm;
    this.hasRelated = hasRelated;
    this.relatedType = relatedType;
    this.hasSource = hasSource;
    this.sourceType = sourceType;
    this.event = isEvent;
  }
  
  public ObjectType getObjectType()
  {
    return objectType;
  }

  public Identifier getObjectName()
  {
    return objectName;
  }

  public Object getBodyShortForm()
  {
    return bodyShortForm;
  }

  public boolean hasRelated()
  {
    return hasRelated;
  }

  public ObjectType getRelatedType()
  {
    return relatedType;
  }

  public boolean hasSource()
  {
    return hasSource;
  }

  public ObjectType getSourceType()
  {
    return sourceType;
  }

  public boolean isEvent()
  {
    return event;
  }
}
