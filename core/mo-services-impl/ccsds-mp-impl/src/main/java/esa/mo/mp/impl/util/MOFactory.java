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
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Subscription;

public class MOFactory {

    private static Random RANDOM = new Random();

    /**
     * Creates a Subscription with Entity Key (*, 0, 0, 0)
     */
    public static Subscription createSubscription() {
        return createSubscription(new Identifier("*"));
    }

    /**
     * Creates a Subscription with Entity Key (identity, 0, 0, 0)
     */
    public static Subscription createSubscription(Identifier identity) {
        Identifier subscriptionId = new Identifier("SubId" + RANDOM.nextInt());
        return new Subscription(subscriptionId);
    }
}
