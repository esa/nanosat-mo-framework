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
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.consumer.PlanInformationManagementStub;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.misc.ConsumerServiceImpl;

/**
 * Plan Information Management (PIM) Service consumer implementation
 */
public class PlanInformationManagementConsumerServiceImpl extends ConsumerServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(PlanInformationManagementConsumerServiceImpl.class.getName());

    private PlanInformationManagementStub pimService = null;
    private COMServicesConsumer comServices;

    public COMServicesConsumer getCOMServices() {
        return this.comServices;
    }

    @Override
    public Object getStub() {
        return this.getPlanInformationManagementStub();
    }

    public PlanInformationManagementStub getPlanInformationManagementStub() {
        return this.pimService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new PlanInformationManagementStub(tmConsumer);
    }

    public PlanInformationManagementConsumerServiceImpl(SingleConnectionDetails connectionDetails, COMServicesConsumer comServices) throws MALException, MalformedURLException {
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
            PlanInformationManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
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
            PlanInformationManagementHelper.PLANINFORMATIONMANAGEMENT_SERVICE);

        this.pimService = new PlanInformationManagementStub(tmConsumer);
    }
}
