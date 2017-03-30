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
package esa.mo.mc.impl.provider.mcprototype.action;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mc.impl.util.MCServicesHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequestList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mcprototype.MCPrototypeHelper;
import org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper;
import org.ccsds.moims.mo.mcprototype.actiontest.provider.ActionTestInheritanceSkeleton;

public class ActionTestProviderServiceImpl extends ActionTestInheritanceSkeleton {

	private MALProvider actionTestServiceProvider;
	private boolean initialiased = false;
	private final ConnectionProvider connection = new ConnectionProvider();
	private ActionProviderServiceImpl actionService;
	private COMServicesProvider comServices;
	private ActionInvocationListener actions;
	private static final Logger LOGGER = Logger.getLogger(ActionTestProviderServiceImpl.class.getName());

	/**
	 * creates the MAL objects
	 *
	 * @param actionService
	 * @param comServices
	 * @param actions
	 * @throws MALException On initialisation error.
	 */
	public synchronized void init(ActionProviderServiceImpl actionService, COMServicesProvider comServices, ActionInvocationListener actions) throws MALException {
		this.actionService = actionService;
		this.comServices = comServices;
		this.actions = actions;
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
				ActionTestHelper.init(MALContextFactory.getElementFactoryRegistry());
			} catch (MALException ex) {
				// nothing to be done..
			}
		}

		// shut down old service transport
		if (null != actionTestServiceProvider) {
			connection.close();
		}

		actionTestServiceProvider = connection.startService(ActionTestHelper.ACTIONTEST_SERVICE_NAME.toString(), ActionTestHelper.ACTIONTEST_SERVICE, this);

		initialiased = true;
		Logger.getLogger(ActionTestProviderServiceImpl.class.getName()).info("ActionTest service READY");

	}

	/**
	 * Closes all running threads and releases the MAL resources.
	 */
	public void close() {
		try {
			if (null != actionTestServiceProvider) {
				actionTestServiceProvider.close();
			}

			connection.close();
		} catch (MALException ex) {
			Logger.getLogger(ActionTestProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
		}
	}

	@Override
	public void resetTest(String domain, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.SEVERE, "ActionTestProvider: resetTest called.");
		// remove all actions from provider
		LongList actionIdentityIds = new LongList();
		actionIdentityIds.add(0L);
		actionService.removeAction(actionIdentityIds, interaction);

		// and remove all action identities even from the COM archive- otherwise reuse of identities depends on former test runs
		comServices.getArchiveService().delete(ActionHelper.ACTIONIDENTITY_OBJECT_TYPE, ConfigurationProviderSingleton.getDomain(), actionIdentityIds, interaction);
	}

	@Override
	public ObjectInstancePair addActionDefinition(ActionCreationRequest actionDefinition, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "ActionTestProvider::addActionDefinition called.");
		ActionCreationRequestList acrl = new ActionCreationRequestList();
		acrl.add(actionDefinition);
		ObjectInstancePairList oipl = actionService.addAction(acrl, interaction);
		return oipl.get(0);
	}

	@Override
	public void forcePreCheckInvalidException(Boolean force, UIntegerList extra, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "ActionTestProvider::forcePreCheckInvalidException called.");
//		actions.setForcePreCheckInvalidException(force);
//		actions.setForcePreCheckInvalidExceptionExtra(extra);
	}

	@Override
	public void forcePreCheckFailure(Boolean force, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "ActionTestProvider::forcePreCheckFailure called.");
//		actions.setForcePreCheckFailure(force);
	}

	@Override
	public void setFailureStage(UInteger stage, UInteger failureCode, MALInteraction interaction) throws MALInteractionException, MALException {
//		actions.setFailureStage(stage, failureCode);
	}

	@Override
	public void setMinimumActionExecutionTime(UInteger time, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "ActionTestProvider::setMinimumActionExecutionTime called.");
	}

}
