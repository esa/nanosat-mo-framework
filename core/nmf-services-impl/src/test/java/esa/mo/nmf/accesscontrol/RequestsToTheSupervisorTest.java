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
 * Holds that, in enforce mode, the Supervisor refuses a request without
 * credentials to its CommandExecutor whatever the header of the request says:
 * <ul>
 * <li>whatever host and port its destination names, since the transport
 * delivers a request by the service name at the end of the destination
 * alone;</li>
 * <li>whatever area version, service or operation number it names, including
 * ones the Supervisor does not know.</li>
 * </ul>
 */
public class RequestsToTheSupervisorTest {

    private static final String SUPERVISOR = "maltcp://10.0.0.1:1024";
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
    public void enforceRefusesARequestWithoutCredentials() {
        assertRefused("A request without credentials to the Supervisor's own address",
                request(SUPERVISOR + COMMAND_EXECUTOR, SMHelper.SM_AREA_VERSION, COMMAND_EXECUTOR_SERVICE, RUN_COMMAND));
    }

    @Test
    public void enforceRefusesARequestWhoseDestinationNamesAnotherHost() {
        for (String host : new String[]{"maltcp://10.9.9.9:1024", "maltcp://obc:1024", "maltcp://10.0.0.1:9"}) {
            assertRefused("A request without credentials whose destination names " + host,
                    request(host + COMMAND_EXECUTOR, SMHelper.SM_AREA_VERSION, COMMAND_EXECUTOR_SERVICE, RUN_COMMAND));
        }
    }

    @Test
    public void enforceRefusesARequestOfAnUnknownAreaVersion() {
        assertRefused("A request without credentials that names an unknown version of the SM area",
                request(SUPERVISOR + COMMAND_EXECUTOR, new UOctet((short) 99), COMMAND_EXECUTOR_SERVICE, RUN_COMMAND));
    }

    @Test
    public void enforceRefusesARequestOfAnUnknownServiceOrOperation() {
        assertRefused("A request without credentials that names an unknown service",
                request(SUPERVISOR + COMMAND_EXECUTOR, SMHelper.SM_AREA_VERSION, 99, RUN_COMMAND));
        assertRefused("A request without credentials that names an unknown operation",
                request(SUPERVISOR + COMMAND_EXECUTOR, SMHelper.SM_AREA_VERSION, COMMAND_EXECUTOR_SERVICE, 99));
    }

    private static void assertRefused(String what, MALMessageHeader header) {
        System.setProperty(AccessControlMode.PROPERTY, "enforce");
        AccessControlAuthority authority = new AccessControlAuthority(SUPERVISOR + "/Directory");
        try {
            authority.check(header);
            Assert.fail(what + " must be refused");
        } catch (MALCheckErrorException expected) {
            // Refused
        }
    }

    private static MALMessageHeader request(String to, UOctet areaVersion, int service, int operation) {
        return new MALMessageHeader(new Identifier("maltcp://10.0.0.2:1025/caller"), new Blob(), new Identifier(to),
                Time.now(), InteractionType.REQUEST, new UOctet((short) 1), 1L, SMHelper.SM_AREA_NUMBER,
                new UShort(service), new UShort(operation), areaVersion, Boolean.FALSE, new NamedValueList());
    }
}
