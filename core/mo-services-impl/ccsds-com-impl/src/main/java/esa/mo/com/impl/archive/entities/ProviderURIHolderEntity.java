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
package esa.mo.com.impl.archive.entities;

import java.io.Serializable;

/**
 * The entity that holds a providerURI and its respective id.
 */
@Deprecated
public class ProviderURIHolderEntity implements Serializable {

    private int id;
    private String providerURI;

    protected ProviderURIHolderEntity() {
    }

    public ProviderURIHolderEntity(final int id, final String providerURI) {
        this.id = id;
        this.providerURI = providerURI;
    }

    public int getId() {
        return this.id;
    }

    public String getProviderURIString() {
        return this.providerURI;
    }

}
