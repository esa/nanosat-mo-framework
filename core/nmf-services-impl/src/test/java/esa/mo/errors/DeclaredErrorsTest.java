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
package esa.mo.errors;

import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.ArtificialIntelligenceProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.CameraProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.GPSProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.PowerControlProviderServiceImpl;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import esa.mo.sm.impl.provider.CommandExecutorProviderServiceImpl;
import java.io.File;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.UnsupportedOperationException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * A provider answers a request that it cannot serve with the error that its
 * specification declares, never with an exception that the MAL does not turn
 * into an answer: a NULL in a field that cannot be NULL, a bad path, an unknown
 * model and an operation that is not implemented.
 */
public class DeclaredErrorsTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void aNullFieldIsAnInvalidArgumentThatNamesTheField() {
        assertError(InvalidArgumentException.class, "The ids field must not be NULL",
                () -> new ParameterProviderServiceImpl().getValue(null, null));
        assertError(InvalidArgumentException.class, "The appIds field must not be NULL",
                () -> new AppsLauncherProviderServiceImpl().runApp(null, null));
        assertError(InvalidArgumentException.class, "The names field must not be NULL",
                () -> new PowerControlProviderServiceImpl().listDevices(null, null));
        assertError(InvalidArgumentException.class, "The command field must not be NULL",
                () -> new CommandExecutorProviderServiceImpl().runCommand(null, null));
    }

    @Test
    public void aBadPathOfTheArtificialIntelligenceServiceIsAnInvalidArgument() throws Exception {
        ArtificialIntelligenceProviderServiceImpl ai = new ArtificialIntelligenceProviderServiceImpl();
        assertError(InvalidArgumentException.class, "The modelPath field must not be NULL", () -> ai.setModel(null, null));
        assertError(InvalidArgumentException.class, "The modelPath field must end with .xml",
                () -> ai.setModel("model.bin", null));
        assertError(InvalidArgumentException.class, "The modelPath field must refer to an existing file: /no/model.xml",
                () -> ai.setModel("/no/model.xml", null));
        assertError(InvalidArgumentException.class, "The jsonPath field must not be NULL",
                () -> ai.doComputerVision(null, null));
    }

    @Test
    public void anUnknownModelIsUnknown() throws Exception {
        File tiles = tmp.newFolder("tiles");
        assertError(UnknownException.class, null,
                () -> new ArtificialIntelligenceProviderServiceImpl().doInference(42L, tiles.getPath(), null));
    }

    @Test
    public void anOperationThatIsNotImplementedIsAnUnsupportedOperation() {
        assertError(UnsupportedOperationException.class, null,
                () -> new CameraProviderServiceImpl().preprocessPicture(null, null, null));
        assertError(UnsupportedOperationException.class, null,
                () -> new GPSProviderServiceImpl().getTLE(null));
    }

    private interface Call {

        void run() throws Exception;
    }

    private static void assertError(Class<? extends MOErrorException> expected, String extraInformation, Call call) {
        try {
            call.run();
            Assert.fail("Expected " + expected.getSimpleName());
        } catch (Exception error) {
            Assert.assertEquals(expected, error.getClass());
            Assert.assertEquals(extraInformation, ((MOErrorException) error).getExtraInformation());
        }
    }
}
