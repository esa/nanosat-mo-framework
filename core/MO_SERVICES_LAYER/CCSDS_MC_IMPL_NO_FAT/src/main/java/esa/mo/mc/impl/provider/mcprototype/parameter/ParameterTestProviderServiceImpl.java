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
package esa.mo.mc.impl.provider.mcprototype.parameter;

import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.mc.impl.interfaces.ParameterTestStatusListener;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.DurationList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.mcprototype.MCPrototypeHelper;
import org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper;
import org.ccsds.moims.mo.mcprototype.parametertest.provider.ParameterTestInheritanceSkeleton;

/**
 *
 * @author Vorwerg
 */
public class ParameterTestProviderServiceImpl extends ParameterTestInheritanceSkeleton {

    private MALProvider parameterTestServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private transient ParameterTestStatusListener parameterApplication;   // transient: marks members that won't be serialized.

    /**
     * creates the MAL objects
     *
     * @param parameterApplication
     * @throws MALException On initialisation error.
     */
    public synchronized void init(ParameterTestStatusListener parameterApplication) throws MALException {
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
                ParameterTestHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
            
            this.parameterApplication = parameterApplication;

        }

        // shut down old service transport
        if (null != parameterTestServiceProvider) {
            connection.close();
        }

        parameterTestServiceProvider = connection.startService(ParameterTestHelper.PARAMETERTEST_SERVICE_NAME.toString(), ParameterTestHelper.PARAMETERTEST_SERVICE, this);

        running = true;

        initialiased = true;
        Logger.getLogger(ParameterTestProviderServiceImpl.class.getName()).info("ParameterTest service READY");

    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != parameterTestServiceProvider) {
                parameterTestServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public void pushParameterValue(Identifier name, ParameterRawValue value, MALInteraction interaction) throws MALException, MALInteractionException {
        parameterApplication.pushParameterValue(name, value);
    }

    @Override
    public void setValidityStateOptions(Boolean useCustomMechanisms, Boolean useCustomValue, MALInteraction interaction) throws MALException, MALInteractionException {
        parameterApplication.setValidityStateOptions(useCustomMechanisms, useCustomValue);
    }

    @Override
    public void setReadOnlyParameter(Identifier name, Boolean value, MALInteraction interaction) throws MALException, MALInteractionException {
        parameterApplication.setReadOnlyParameter(name, value);
    }

    @Override
    public void setProvidedIntervals(DurationList providedIntervals, MALInteraction interaction) throws MALException, MALInteractionException {
        parameterApplication.setProvidedIntervals(providedIntervals);
    }

    @Override
    public void deleteParameterValues(MALInteraction interaction) {
        //removes all parametervalues
        IdentifierList names = new IdentifierList();
        names.add(new Identifier("*"));
//        parameterApplication.removeParameters(names);
    }

}
