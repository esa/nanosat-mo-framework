/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.mission.orekit.simulator.tcp;

import esa.mo.nmf.mission.orekit.simulator.util.ArgumentDescriptor;
import esa.mo.nmf.mission.orekit.simulator.util.CommandDescriptor;
import esa.mo.nmf.mission.orekit.simulator.util.CommandResult;
import esa.mo.nmf.mission.orekit.simulator.util.PlatformMessage;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorData;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorDeviceData;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorHeader;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorSchedulerPiece;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * The messages of the simulator's socket, as JSON: each one comes back as it
 * was sent, the values of several possible types keep their type, and
 * anything that is not a message of the simulator is refused, Java
 * serialization included (NMF-2026-001).
 */
public class SimulatorMessagesTest {

    @Test
    public void textAndNumbersComeBackAsTheyWereSent() throws Exception {
        Assert.assertEquals("List", roundTrip("List"));
        Assert.assertEquals("Line one\nline two \"quoted\"", roundTrip("Line one\nline two \"quoted\""));
        Assert.assertEquals(0, roundTrip(0));
    }

    @Test
    public void thePlatformConfigurationComesBackAsItWasSent() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("camerasim.imagemode", "Fixed");
        properties.setProperty("camerasim.imagefile", "/a path/with spaces.jpg");
        Assert.assertEquals(properties, roundTrip(properties));
    }

    @Test
    public void aSettingComesBackAsItWasSent() throws Exception {
        PlatformMessage received = (PlatformMessage) roundTrip(new PlatformMessage("camerasim.imagemode", "Random"));
        Assert.assertEquals("camerasim.imagemode", received.getKey());
        Assert.assertEquals("Random", received.getValue());
    }

    @Test
    public void theHeaderComesBackAsItWasSent() throws Exception {
        SimulatorHeader sent = new SimulatorHeader();
        sent.setKeplerElements("7000.0;0.0;97.0;340.0;0.0;0.0");
        sent.setStartDate(new Date(1_700_000_000_000L));
        SimulatorHeader received = (SimulatorHeader) roundTrip(sent);
        Assert.assertEquals(sent.getKeplerElements(), received.getKeplerElements());
        Assert.assertEquals(sent.getStartDate(), received.getStartDate());
        Assert.assertEquals(sent.getTimeFactor(), received.getTimeFactor());
        Assert.assertEquals(sent.isUseCelestia(), received.isUseCelestia());
    }

    @Test
    public void theSimulatorDataComesBackAsItWasSent() throws Exception {
        SimulatorData sent = new SimulatorData(42, new Date(1_700_000_000_000L));
        sent.setTimeFactor(7);
        SimulatorData received = (SimulatorData) roundTrip(sent);
        Assert.assertEquals(42, received.getCounter());
        Assert.assertEquals(7, received.getTimeFactor());
        Assert.assertEquals(sent.getCurrentTime(), received.getCurrentTime());
    }

    @Test
    public void aCommandAndItsArgumentsComeBackWithTheirTypes() throws Exception {
        CommandDescriptor sent = command();
        CommandDescriptor received = (CommandDescriptor) roundTrip(sent);
        Assert.assertEquals(sent.getIntF(), received.getIntF());
        Assert.assertEquals(sent.getMethodBody(), received.getMethodBody());
        Assert.assertEquals(sent.getInternalID(), received.getInternalID());
        Assert.assertEquals(2, received.getInputArgList().size());
        Assert.assertArrayEquals(new float[]{1.5f, Float.NaN, -2f},
                (float[]) received.getInputArgList().get(0).getType(), 0f);
        Assert.assertEquals("values", received.getInputArgList().get(0).getName());
        Assert.assertEquals((byte) 3, received.getInputArgList().get(1).getType());
        Assert.assertEquals(sent.getTemplateList().size(), received.getTemplateList().size());
    }

    @Test
    public void theOutputOfACommandKeepsItsType() throws Exception {
        Object[] outputs = {"position", 7, 7L, 1.5f, 2.5d, (byte) 1, new byte[]{0, 1, (byte) 0xFF},
            new int[]{1, 2}, new long[]{3L}, new float[]{4f}, new double[]{5d}, null};
        for (Object output : outputs) {
            CommandResult sent = new CommandResult(command(), new Date(1_000L), new Date(2_000L));
            sent.setOutput(output);
            CommandResult received = (CommandResult) roundTrip(sent);
            Assert.assertEquals(sent.getExecutionTime(), received.getExecutionTime());
            Assert.assertEquals(sent.getSimulatorTime(), received.getSimulatorTime());
            Assert.assertEquals(sent.getCommandDescriptor().getMethodBody(),
                    received.getCommandDescriptor().getMethodBody());
            if (output == null) {
                Assert.assertNull(received.getOutput());
            } else {
                Assert.assertEquals("The output " + output + " must keep its type",
                        output.getClass(), received.getOutput().getClass());
                Assert.assertTrue("The output " + output + " must keep its value",
                        Objects.deepEquals(output, received.getOutput()));
            }
        }
    }

    @Test
    public void aListKeepsWhatItHoldsAndInWhichOrder() throws Exception {
        SimulatorDeviceData device = new SimulatorDeviceData("FineADCS");
        device.getDataList().add(new ArgumentDescriptor(new float[]{1f, 2f, 3f}, "PositionInertial"));
        LinkedList<Object> sent = new LinkedList<>();
        sent.add(command());
        sent.add(device);
        sent.add(new SimulatorSchedulerPiece(1_000L, 2002, "a template"));

        LinkedList<?> received = (LinkedList<?>) roundTrip(sent);
        Assert.assertEquals(3, received.size());
        Assert.assertTrue(received.get(0) instanceof CommandDescriptor);
        SimulatorDeviceData receivedDevice = (SimulatorDeviceData) received.get(1);
        Assert.assertEquals("FineADCS", receivedDevice.getName());
        Assert.assertArrayEquals(new float[]{1f, 2f, 3f},
                (float[]) receivedDevice.getDataList().get(0).getType(), 0f);
        SimulatorSchedulerPiece piece = (SimulatorSchedulerPiece) received.get(2);
        Assert.assertEquals(1_000L, piece.getTime());
        Assert.assertEquals(2002, piece.getInternalID());
        Assert.assertEquals("a template", piece.getArgumentTemplateDescription());
    }

    @Test
    public void aMessageOfAnUnknownTypeIsRefused() {
        assertRefused("{\"type\":\"java.io.File\",\"value\":{\"path\":\"/etc/passwd\"}}");
        assertRefused("{\"type\":\"Header\"}");
        assertRefused("{\"value\":\"List\"}");
        assertRefused("[\"List\"]");
        assertRefused("{\"type\":\"Command\",\"value\":{\"inputArgList\":[{\"name\":\"x\","
                + "\"type\":{\"kind\":\"java.io.File\",\"value\":\"/etc/passwd\"}}]}}");
    }

    @Test
    public void somethingThatIsNotJsonIsRefused() {
        assertRefused("this is not a message of the simulator");
        assertRefused("{\"type\":\"Text\",\"value\":");
    }

    @Test
    public void aJavaObjectIsRefused() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(new Date());
        }
        bytes.write('\n');
        try {
            SimulatorMessages.read(new ByteArrayInputStream(bytes.toByteArray()));
            Assert.fail("A serialized Java object must be refused");
        } catch (SimulatorMessages.NotAMessageException expected) {
            // Refused
        }
    }

    @Test
    public void somethingThatIsNotAMessageOfTheSimulatorIsNotSent() {
        try {
            SimulatorMessages.encode(new File("/etc/passwd"));
            Assert.fail("Only the simulator's own messages may be sent");
        } catch (SimulatorMessages.NotAMessageException expected) {
            // Not sent
        }
    }

    @Test
    public void theEndOfTheConnectionIsNotAMessage() throws Exception {
        Assert.assertNull(SimulatorMessages.read(new ByteArrayInputStream(new byte[0])));
    }

    private static Object roundTrip(Object message) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SimulatorMessages.write(message, out);
        String line = out.toString(StandardCharsets.UTF_8);
        Assert.assertEquals("A message is one line", line.length() - 1, line.indexOf('\n'));
        return SimulatorMessages.read(new ByteArrayInputStream(out.toByteArray()));
    }

    private static void assertRefused(String line) {
        try {
            SimulatorMessages.decode(line);
            Assert.fail("Not a message of the simulator, so it must be refused: " + line);
        } catch (SimulatorMessages.NotAMessageException expected) {
            // Refused
        }
    }

    private static CommandDescriptor command() {
        CommandDescriptor command = new CommandDescriptor("FineADCS", "void Gyro1SetRate(float[] values,byte mode)",
                "Sets the rate", 1096, Logger.getLogger(SimulatorMessagesTest.class.getName()));
        command.getInputArgList().clear();
        command.getInputArgList().add(new ArgumentDescriptor(new float[]{1.5f, Float.NaN, -2f}, "values"));
        command.getInputArgList().add(new ArgumentDescriptor((byte) 3, "mode"));
        return command;
    }
}
