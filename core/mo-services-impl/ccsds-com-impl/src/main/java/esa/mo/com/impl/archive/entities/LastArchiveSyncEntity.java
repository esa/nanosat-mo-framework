/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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

import java.util.Objects;

/**
 * Entity holding last archive synchronization time for provider. The entities
 * in this implementation have been removed. Please create a proper COM Object
 * for your data and store it via the service interface instead of hacking it
 * out with this entity.
 *
 * @author Lukasz.Stochlak
 */
@Deprecated
public class LastArchiveSyncEntity {

    private Long id;

    private String providerUri;

    private String domain;

    private Long lastSync;

    /**
     * Default constructor.
     */
    public LastArchiveSyncEntity() {
    }

    /**
     * Constructor taking provider URI and domain as parameters. The entities in
     * this implementation have been removed. Please create a proper COM Object
     * for your data and store it via the service interface instead of hacking
     * it out with this entity.
     *
     * @param providerUri provider URI as String.
     * @param domain domain as String.
     */
    @Deprecated
    public LastArchiveSyncEntity(String providerUri, String domain) {
        this.providerUri = providerUri;
        this.domain = domain;
        this.lastSync = System.currentTimeMillis();
    }

    /**
     * Returns object hsh code.
     *
     * @return hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProviderUri(), getDomain(), getLastSync());
    }

    /**
     * Compares two objects (this and given as a parameter).
     *
     * @param o object to compare.
     * @return true if objects are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LastArchiveSyncEntity that = (LastArchiveSyncEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getProviderUri(), that.getProviderUri())
                && Objects.equals(getDomain(), that.getDomain())
                && Objects.equals(getLastSync(), that.getLastSync());
    }

    /**
     * Returns object id.
     *
     * @return object id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets object id.
     *
     * @param id object id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns provider URI.
     *
     * @return provider URI.
     */
    public String getProviderUri() {
        return providerUri;
    }

    /**
     * Sets provider URI.
     *
     * @param providerUri provider URI.
     */
    public void setProviderUri(String providerUri) {
        this.providerUri = providerUri;
    }

    /**
     * Returns domain.
     *
     * @return domian.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets domain.
     *
     * @param domain domain.
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Returns last synchronization time as Long.
     *
     * @return last synchronization time as Long.
     */
    public Long getLastSync() {
        return lastSync;
    }

    /**
     * Sets last synchronization time as Long.
     *
     * @param lastSync synchronization time as Long.
     */
    public void setLastSync(Long lastSync) {
        this.lastSync = lastSync;
    }

    /**
     * Returns object as String.
     *
     * @return object as String.
     */
    @Override
    public String toString() {
        return "LastArchiveSyncEntity{" + "id=" + id + ", providerUri='" + providerUri + '\'' + ", domain='" + domain + '\''
                + ", lastSync=" + lastSync + '}';
    }
}
