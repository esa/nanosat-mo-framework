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

import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.accesscontrol.MALCheckErrorException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.SMHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Supervisor consumes some of its own services, such as the Autonomous
 * ADCS service with which it monitors its attitude. Such a request leaves the
 * Supervisor and comes back to it, so it is checked like any other. Holds that,
 * in enforce mode, the Supervisor accepts the requests that it marked as its
 * own, that the same request without the mark is still refused, and that the
 * mark is given neither to a request to an App nor in place of a credential.
 */
public class RequestsTheSupervisorSendsToItselfTest {

    private static final String SUPERVISOR = "maltcp://10.0.0.1:1024";
    private static final String APP = "maltcp://10.0.0.1:1025";
    private static final String COMMAND_EXECUTOR = "/nanosat-mo-supervisor-CommandExecutor";
    private static final int COMMAND_EXECUTOR_SERVICE = 3;
    private static final int RUN_COMMAND = 1;

    @BeforeClass
    public static void registerTheSMArea() throws MALException {
        MALContextFactory.registerArea(SMHelper.SM_AREA);
    }

    @After
    public void clearMode() {
        System.clearProperty(AccessControlMode.PROPERTY);
    }

    @Test
    public void aRequestThatTheSupervisorSendsToItselfIsAccepted() throws Exception {
        System.setProperty(AccessControlMode.PROPERTY, "enforce");
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        MALMessageHeader header = request(SUPERVISOR + COMMAND_EXECUTOR, new Blob());

        authority.markOwnMessage(header);
        authority.check(header);
    }

    @Test
    public void theSameRequestWithoutTheMarkIsRefused() {
        System.setProperty(AccessControlMode.PROPERTY, "enforce");
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        try {
            authority.check(request(SUPERVISOR + COMMAND_EXECUTOR, new Blob()));
            Assert.fail("A request without the mark and without credentials must be refused");
        } catch (MALCheckErrorException expected) {
            // Refused
        }
    }

    @Test
    public void theMarkOfOneSupervisorIsNotThatOfAnother() {
        System.setProperty(AccessControlMode.PROPERTY, "enforce");
        MALMessageHeader header = request(SUPERVISOR + COMMAND_EXECUTOR, new Blob());
        new AccessControlAuthority(SUPERVISOR + "/Directory").markOwnMessage(header);
        try {
            new AccessControlAuthority(SUPERVISOR + "/Directory").check(header);
            Assert.fail("A request marked by another Supervisor must be refused");
        } catch (MALCheckErrorException expected) {
            // Refused
        }
    }

    @Test
    public void aRequestThatTheSupervisorSendsToAnAppIsNotMarked() {
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        MALMessageHeader header = request(APP + "/some-app-Parameter", new Blob());

        authority.markOwnMessage(header);
        Assert.assertTrue("The mark must not be given to an App",
                NMFAccessControl.isEmpty(header.getAuthenticationId()));
    }

    @Test
    public void aRequestThatCarriesACredentialKeepsIt() {
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        byte[] credential = NMFAccessControl.newCredential();
        MALMessageHeader header = request(SUPERVISOR + COMMAND_EXECUTOR, new Blob(credential.clone()));

        authority.markOwnMessage(header);
        Assert.assertArrayEquals("The mark must not replace a credential", credential,
                header.getAuthenticationId().getValue());
    }

    private static MALMessageHeader request(String to, Blob credential) {
        return new MALMessageHeader(new Identifier(SUPERVISOR + "/consumer"), credential, new Identifier(to),
                Time.now(), InteractionType.REQUEST, new UOctet((short) 1), 1L, SMHelper.SM_AREA_NUMBER,
                new UShort(COMMAND_EXECUTOR_SERVICE), new UShort(RUN_COMMAND), SMHelper.SM_AREA_VERSION,
                Boolean.FALSE, new NamedValueList());
    }
}
