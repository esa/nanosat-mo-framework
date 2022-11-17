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
package esa.mo.ground.cameraacquisotorground;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author Kevin Otto
 */
@SpringBootApplication
public class SpringInitiator {

    /**
     * Main command line entry point.
     * This Demo should be used with Camera Acquisition System provider.
     * @param args the command line arguments - directoryURI and port for tomcat
     */
    public static void main(final String[] args) {
        if (args.length != 2) {
            System.err.println(
                "Please give directoryURI as first argument and an open port number for Tomcat as second argument!");
            System.err.println("e.g. maltcp://123.123.123.123:1024/nanosat-mo-supervisor-Directory 1050");
            System.exit(1);
        }
        SpringApplication app = new SpringApplication(CameraAcquisitorGround.class);

        app.setDefaultProperties(Collections.singletonMap("server.port", args[1]));
        app.run(args);
    }

}
