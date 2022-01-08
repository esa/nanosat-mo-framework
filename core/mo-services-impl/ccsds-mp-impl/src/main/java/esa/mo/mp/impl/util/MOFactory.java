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
package esa.mo.mp.impl.util;

import java.util.Random;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;

public class MOFactory {

    /**
     * Creates a Subscription with Entity Key (*, 0, 0, 0)
     */
    public static Subscription createSubscription() {
        return createSubscription(new Identifier("*"));
    }

    /**
     * Creates a Subscription with Entity Key (identity, 0, 0, 0)
     */
    public static Subscription createSubscription(final Identifier identity) {
        final Identifier subscriptionId = new Identifier("SubId" + new Random().nextInt());

        final IdentifierList subDomain = null;
        final Boolean allAreas = false;
        final Boolean allServices = false;
        final Boolean allOperations = false;
        final Boolean onlyOnChange = false;
        final EntityKeyList entityKeys = new EntityKeyList();

        final Identifier firstSubKey = identity == null ? new Identifier("*") : identity;
        final EntityKey entityKey = new EntityKey(firstSubKey, 0L, 0L, 0L);
        entityKeys.add(entityKey);

        final EntityRequestList entities = new EntityRequestList();
        final EntityRequest entityRequest = new EntityRequest(subDomain, allAreas, allServices, allOperations, onlyOnChange, entityKeys);
        entities.add(entityRequest);

        final Subscription subscription = new Subscription(subscriptionId, entities);
        return subscription;
    }
}
