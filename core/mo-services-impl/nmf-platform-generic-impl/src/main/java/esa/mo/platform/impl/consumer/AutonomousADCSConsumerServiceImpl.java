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
package esa.mo.platform.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;

/**
 *
 * @author Cesar Coelho
 */
public class AutonomousADCSConsumerServiceImpl extends ConsumerServiceImpl
{

  private AutonomousADCSStub autonomousADCSService = null;
  private COMServicesConsumer comServices;

  public COMServicesConsumer getCOMServices()
  {
    return comServices;
  }

  @Override
  public Object getStub()
  {
    return this.getAutonomousADCSStub();
  }

  public AutonomousADCSStub getAutonomousADCSStub()
  {
    return this.autonomousADCSService;
  }

  @Override
  public Object generateServiceStub(MALConsumer tmConsumer)
  {
    return new AutonomousADCSStub(tmConsumer);
  }

  public AutonomousADCSConsumerServiceImpl(SingleConnectionDetails connectionDetails,
      COMServicesConsumer comServices) throws MALException, MalformedURLException,
      MALInteractionException
  {
    this(connectionDetails, comServices, null, null);
  }

  public AutonomousADCSConsumerServiceImpl(SingleConnectionDetails connectionDetails,
                                           COMServicesConsumer comServices,
                                           Blob authenticationID,
                                           String localNamePrefix) throws MALException, MalformedURLException,
                                                                         MALInteractionException
  {
    this.connectionDetails = connectionDetails;
    this.comServices = comServices;

    // Close old connection
    if (tmConsumer != null) {
      try {
        tmConsumer.close();
      } catch (MALException ex) {
        Logger.getLogger(AutonomousADCSConsumerServiceImpl.class.getName()).log(Level.SEVERE, null,
                                                                                ex);
      }
    }

    tmConsumer = connection.startService(
            this.connectionDetails.getProviderURI(),
            this.connectionDetails.getBrokerURI(),
            this.connectionDetails.getDomain(),
            AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE,
            authenticationID, localNamePrefix);

    this.autonomousADCSService = new AutonomousADCSStub(tmConsumer);
  }

}
