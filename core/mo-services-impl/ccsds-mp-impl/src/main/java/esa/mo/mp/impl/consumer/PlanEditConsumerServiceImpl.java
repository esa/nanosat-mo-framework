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
package esa.mo.mp.impl.consumer;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mp.MPHelper;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planedit.consumer.PlanEditStub;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.misc.ConsumerServiceImpl;

/**
 * Plan Edit (PED) Service consumer implementation
 */
public class PlanEditConsumerServiceImpl extends ConsumerServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(PlanEditConsumerServiceImpl.class.getName());

    private PlanEditStub planEditService = null;
    private COMServicesConsumer comServices;

    public COMServicesConsumer getCOMServices() {
        return this.comServices;
    }

    @Override
    public Object getStub() {
        return this.getPlanEditStub();
    }

    public PlanEditStub getPlanEditStub() {
        return this.planEditService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new PlanEditStub(tmConsumer);
    }

    public PlanEditConsumerServiceImpl(SingleConnectionDetails connectionDetails, COMServicesConsumer comServices) throws MALException, MalformedURLException {
        if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
            COMHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(MPHelper.MP_AREA_NAME, MPHelper.MP_AREA_VERSION) == null) {
            MPHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        try {
            PlanEditHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // nothing to be done..
        }

        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(
            this.connectionDetails.getProviderURI(),
            this.connectionDetails.getBrokerURI(),
            this.connectionDetails.getDomain(),
            PlanEditHelper.PLANEDIT_SERVICE);

        this.planEditService = new PlanEditStub(tmConsumer);
    }
}
