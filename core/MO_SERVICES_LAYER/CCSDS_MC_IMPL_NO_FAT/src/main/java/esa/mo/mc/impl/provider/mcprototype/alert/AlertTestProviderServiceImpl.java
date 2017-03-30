/* 
 * M&C Services for CCSDS Mission Operations Framework
 * Copyright (C) 2016 Deutsches Zentrum fuer Luft- und Raumfahrt e.V. (DLR).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package esa.mo.mc.impl.provider.mcprototype.alert;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.provider.AlertProviderServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest;
import org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequestList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mcprototype.MCPrototypeHelper;
import org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper;
import org.ccsds.moims.mo.mcprototype.alerttest.provider.AlertTestInheritanceSkeleton;

public class AlertTestProviderServiceImpl extends AlertTestInheritanceSkeleton {

	private MALProvider alertTestServiceProvider;
	private boolean initialiased = false;
	private final ConnectionProvider connection = new ConnectionProvider();
	private AlertProviderServiceImpl alertService;
	private COMServicesProvider comServices;
	private static final Logger LOGGER = Logger.getLogger(AlertTestProviderServiceImpl.class.getName());

	/**
	 * creates the MAL objects
	 *
	 * @param alertService
	 * @param comServices
	 * @throws MALException On initialisation error.
	 */
	public synchronized void init(AlertProviderServiceImpl alertService, COMServicesProvider comServices) throws MALException {
		this.alertService = alertService;
		this.comServices = comServices;
		if (!initialiased) {
			if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
				MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			}

			if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
				COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
			}

			if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
				MCHelper.init(MALContextFactory.getElementFactoryRegistry());
			}

			if (MALContextFactory.lookupArea(MCPrototypeHelper.MCPROTOTYPE_AREA_NAME, MCPrototypeHelper.MCPROTOTYPE_AREA_VERSION) == null) {
				MCPrototypeHelper.init(MALContextFactory.getElementFactoryRegistry());
			}

			try {
				AlertTestHelper.init(MALContextFactory.getElementFactoryRegistry());
			} catch (MALException ex) {
				// nothing to be done..
			}
		}

		// shut down old service transport
		if (null != alertTestServiceProvider) {
			connection.close();
		}

		alertTestServiceProvider = connection.startService(AlertTestHelper.ALERTTEST_SERVICE_NAME.toString(), AlertTestHelper.ALERTTEST_SERVICE, this);

		initialiased = true;
		Logger.getLogger(AlertTestProviderServiceImpl.class.getName()).info("AlertTest service READY");

	}

	/**
	 * Closes all running threads and releases the MAL resources.
	 */
	public void close() {
		try {
			if (null != alertTestServiceProvider) {
				alertTestServiceProvider.close();
			}

			connection.close();
		} catch (MALException ex) {
			Logger.getLogger(AlertTestProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
		}
	}

	@Override
	public void resetTest(String alertDomain, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.SEVERE, "AlertTestProvider: resetTest called.");
		// remove all alerts from provider
		LongList alertIdentityIds = new LongList();
		alertIdentityIds.add(0L);
		alertService.removeAlert(alertIdentityIds, interaction);
		
		// and remove all alert identities even from the COM archive- otherwise reuse of identities depends on former test runs
		comServices.getArchiveService().delete(AlertHelper.ALERTIDENTITY_OBJECT_TYPE, connection.getConnectionDetails().getDomain(), alertIdentityIds, interaction);
	}

	@Override
	public Long generateAlert(Long alertDefIdentifier, AttributeValueList alertArgumentValues, Boolean convertArguments, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "AlertTestProvider::generateAlert called.");
		ObjectId source = comServices.getActivityTrackingService().storeCOMOperationActivity(interaction, null);
		Long id = alertService.publishAlertEvent(interaction, new Identifier(alertDefIdentifier.toString()), alertArgumentValues, null, source);
		return null == id ? 0 : id;
	}

	@Override
	public ObjectInstancePair addAlertDefinition(AlertCreationRequest alertCreation, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "AlertTestProvider::addAlertDefinition called.");
		AlertCreationRequestList acrl = new AlertCreationRequestList();
		acrl.add(alertCreation);
		ObjectInstancePairList oipl = alertService.addAlert(acrl, interaction);
		return oipl.get(0);
	}

}
