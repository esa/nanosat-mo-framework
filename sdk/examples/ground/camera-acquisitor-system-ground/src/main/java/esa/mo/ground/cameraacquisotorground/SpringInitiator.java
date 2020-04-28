/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
@SpringBootApplication
public class SpringInitiator
{

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   */
  public static void main(final String args[])
  {
    SpringApplication app = new SpringApplication(CameraAcquisitorGround.class);

    app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
    app.run(args);
  }

}
