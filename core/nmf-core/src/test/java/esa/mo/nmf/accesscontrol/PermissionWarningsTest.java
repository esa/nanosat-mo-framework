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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.SMHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Before it sends a request to the Supervisor, an App warns in its own log
 * about a service that it did not declare in its permissions. Holds that an
 * App is warned, and that a ground tool, which may call everything, is not.
 */
public class PermissionWarningsTest {

    private static final String SUPERVISOR = "maltcp://10.0.0.1:1024";
    private static final String COMMAND_EXECUTOR = "/nanosat-mo-supervisor-CommandExecutor";
    private static final int COMMAND_EXECUTOR_SERVICE = 3;
    private static final int RUN_COMMAND = 1;

    private final Logger logger = Logger.getLogger(NMFAccessControl.class.getName());
    private final List<LogRecord> warnings = new ArrayList<>();
    private final Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                warnings.add(record);
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    };

    @BeforeClass
    public static void registerTheSMArea() throws MALException {
        MALContextFactory.registerArea(SMHelper.SM_AREA);
    }

    @Before
    public void captureWarnings() {
        logger.addHandler(handler);
    }

    @After
    public void stopCapturing() {
        logger.removeHandler(handler);
    }

    @Test
    public void anAppIsWarnedAboutAServiceItDidNotDeclare() throws Exception {
        NMFAccessControl process = newProcess();
        process.presentCredential(NMFAccessControl.newCredential(), SUPERVISOR + "/Directory");

        process.check(message(request(SUPERVISOR + COMMAND_EXECUTOR)));

        Assert.assertEquals("An App must be warned about a service it did not declare", 1, warnings.size());
        Assert.assertTrue("The warning must name the missing permission",
                warnings.get(0).getParameters()[1].toString().contains("Supervisor.SM.CommandExecutor"));
    }

    @Test
    public void aGroundToolIsNotWarned() throws Exception {
        NMFAccessControl process = newProcess();
        process.presentGroundCredential(NMFAccessControl.newCredential(), SUPERVISOR + "/Directory");

        process.check(message(request(SUPERVISOR + COMMAND_EXECUTOR)));

        Assert.assertTrue("A ground tool may call everything, so it must not be warned: " + warnings,
                warnings.isEmpty());
    }

    /**
     * Returns the access control of a new process, rather than the one of this
     * test JVM, so that the tests do not share a credential.
     */
    private static NMFAccessControl newProcess() throws Exception {
        Constructor<NMFAccessControl> constructor = NMFAccessControl.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static MALMessage message(MALMessageHeader header) {
        return new MALMessage() {
            @Override
            public MALMessageHeader getHeader() {
                return header;
            }

            @Override
            public MALMessageBody getBody() {
                return null;
            }

            @Override
            public Map getQoSProperties() {
                return null;
            }
        };
    }

    private static MALMessageHeader request(String to) {
        return new MALMessageHeader(new Identifier("maltcp://10.0.0.2:1025/consumer"), new Blob(),
                new Identifier(to), Time.now(), InteractionType.REQUEST, new UOctet((short) 1), 1L,
                SMHelper.SM_AREA_NUMBER, new UShort(COMMAND_EXECUTOR_SERVICE), new UShort(RUN_COMMAND),
                SMHelper.SM_AREA_VERSION, Boolean.FALSE, new NamedValueList());
    }
}
