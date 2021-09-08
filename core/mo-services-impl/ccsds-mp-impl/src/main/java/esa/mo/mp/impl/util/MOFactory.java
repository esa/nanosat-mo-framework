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
    public static Subscription createSubscription(Identifier identity) {
        Identifier subscriptionId = new Identifier("SubId" + new Random().nextInt());

        IdentifierList subDomain = null;
        Boolean allAreas = false;
        Boolean allServices = false;
        Boolean allOperations = false;
        Boolean onlyOnChange = false;
        EntityKeyList entityKeys = new EntityKeyList();

        Identifier firstSubKey = identity == null ? new Identifier("*") : identity;
        EntityKey entityKey = new EntityKey(firstSubKey, 0L, 0L, 0L);
        entityKeys.add(entityKey);

        EntityRequestList entities = new EntityRequestList();
        EntityRequest entityRequest = new EntityRequest(subDomain, allAreas, allServices, allOperations, onlyOnChange, entityKeys);
        entities.add(entityRequest);

        Subscription subscription = new Subscription(subscriptionId, entities);
        return subscription;
    }
}
