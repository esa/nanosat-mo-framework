/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.accesscontrol;

import java.util.Collections;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.accesscontrol.MALCheckErrorException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Only the Supervisor itself publishes on its services: a broker of the
 * Supervisor refuses every publisher's message that the Supervisor did not mark
 * as its own, whatever credential it carries.
 */
public class PublishingToTheSupervisorTest {

    private static final String SUPERVISOR = "maltcp://10.0.0.1:1024";
    private static final String APP = "maltcp://10.0.0.1:1025";
    private static final UOctet[] PUBLISHER_STAGES = {
        MALPubSubOperation.PUBLISH_REGISTER_STAGE,
        MALPubSubOperation.PUBLISH_STAGE,
        MALPubSubOperation.PUBLISH_DEREGISTER_STAGE
    };

    @After
    public void clearMode() {
        System.clearProperty(AccessControlMode.PROPERTY);
    }

    @Test
    public void enforceRefusesAPublisherThatIsNotTheSupervisor() {
        System.setProperty(AccessControlMode.PROPERTY, "enforce");
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        byte[] appCredential = NMFAccessControl.newCredential();
        authority.registerApp("some-app", appCredential, Collections.singleton("Supervisor.SM.AppsLauncher"));

        for (UOctet stage : PUBLISHER_STAGES) {
            for (Blob credential : new Blob[]{new Blob(), new Blob(appCredential)}) {
                try {
                    authority.check(publisherMessage(stage, SUPERVISOR + "/AppsLauncherInternalBroker", credential));
                    Assert.fail("Stage " + stage + " from an App must be refused");
                } catch (MALCheckErrorException expected) {
                    // Refused
                }
            }
        }
    }

    @Test
    public void auditLetsAPublisherThatIsNotTheSupervisorThrough() throws Exception {
        System.setProperty(AccessControlMode.PROPERTY, "audit");
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        for (UOctet stage : PUBLISHER_STAGES) {
            authority.check(publisherMessage(stage, SUPERVISOR + "/AppsLauncherInternalBroker", new Blob()));
        }
    }

    @Test
    public void theSupervisorsOwnPublishersAreAccepted() throws Exception {
        System.setProperty(AccessControlMode.PROPERTY, "enforce");
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        for (UOctet stage : PUBLISHER_STAGES) {
            MALMessageHeader header = publisherMessage(stage, SUPERVISOR + "/AppsLauncherInternalBroker",
                    new Blob());
            authority.markOwnMessage(header);
            authority.check(header);
        }
    }

    @Test
    public void theMarkOfOneSupervisorIsNotThatOfAnother() {
        System.setProperty(AccessControlMode.PROPERTY, "enforce");
        AccessControlAuthority other = new AccessControlAuthority(SUPERVISOR + "/Directory");
        MALMessageHeader header = publisherMessage(MALPubSubOperation.PUBLISH_STAGE,
                SUPERVISOR + "/AppsLauncherInternalBroker", new Blob());
        other.markOwnMessage(header);
        try {
            new AccessControlAuthority(SUPERVISOR + "/Directory").check(header);
            Assert.fail("A mark made by another Supervisor must be refused");
        } catch (MALCheckErrorException expected) {
            // Refused
        }
    }

    @Test
    public void publishingThatTheSupervisorSendsToABrokerOfAnAppIsNotMarked() {
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        MALMessageHeader header = publisherMessage(MALPubSubOperation.PUBLISH_STAGE, APP + "/ParameterBroker",
                new Blob());
        authority.markOwnMessage(header);
        Assert.assertTrue("The mark must not be given to an App", NMFAccessControl.isEmpty(header.getAuthenticationId()));
    }

    private static MALMessageHeader publisherMessage(UOctet stage, String to, Blob credential) {
        return new MALMessageHeader(new Identifier(APP + "/publisher"), credential, new Identifier(to), Time.now(),
                InteractionType.PUBSUB, stage, 1L, new UShort(4), new UShort(1), new UShort(6), new UOctet((short) 1),
                Boolean.FALSE, new NamedValueList());
    }
}
