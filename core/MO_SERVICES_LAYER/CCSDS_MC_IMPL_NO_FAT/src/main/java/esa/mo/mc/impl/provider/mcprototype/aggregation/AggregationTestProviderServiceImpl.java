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
package esa.mo.mc.impl.provider.mcprototype.aggregation;

import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.interfaces.AggregationTriggerListener;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mcprototype.MCPrototypeHelper;
import org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper;
import org.ccsds.moims.mo.mcprototype.aggregationtest.provider.AggregationTestInheritanceSkeleton;

/**
 *
 * @author Vorwerg
 */
public class AggregationTestProviderServiceImpl extends AggregationTestInheritanceSkeleton{

    private MALProvider aggregationTestServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private transient AggregationTriggerListener aggregationApplication; // transient: marks members that won't be serialized.

    /**
     * creates the MAL objects
     *
     * @param aggregationApplication
     * @throws MALException On initialisation error.
     */
    public synchronized void init(AggregationTriggerListener aggregationApplication) throws MALException {
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
                AggregationTestHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
            
            this.aggregationApplication = aggregationApplication;

        }

        // shut down old service transport
        if (null != aggregationTestServiceProvider) {
            connection.close();
        }

        aggregationTestServiceProvider = connection.startService(AggregationTestHelper.AGGREGATIONTEST_SERVICE_NAME.toString(), AggregationTestHelper.AGGREGATIONTEST_SERVICE, this);

        running = true;

        initialiased = true;
        Logger.getLogger(AggregationTestProviderServiceImpl.class.getName()).info("AggregationTest service READY");

    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != aggregationTestServiceProvider) {
                aggregationTestServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }
    
    @Override
    public void triggerAggregationUpdate(Identifier name, MALInteraction interaction) throws MALException, MALInteractionException {
        aggregationApplication.triggerAggregationUpdate(name);
    }
    
}
