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
package esa.mo.nmf;

import esa.mo.com.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.com.impl.util.HelperCommon;
import esa.mo.mc.impl.util.MCServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.sm.impl.util.SMServicesConsumer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.sm.SMHelper;

/**
 * NMFConsumer connects to an NMF Provider and exposes the available services in
 * the provider: COM, MC, Common, Platform and Software Management services.
 *
 * @author Cesar Coelho
 */
public class NMFConsumer {

    /** Consumer of the COM services (Archive, Directory, ...) offered by the provider. */
    protected final COMServicesConsumer comServices = new COMServicesConsumer();
    /** Consumer of the Monitor and Control services offered by the provider. */
    protected final MCServicesConsumer mcServices = new MCServicesConsumer();
    /** Consumer of the Platform services offered by the provider. */
    protected final PlatformServicesConsumer platformServices = new PlatformServicesConsumer();
    /** Consumer of the Software Management services offered by the provider. */
    protected final SMServicesConsumer smServices = new SMServicesConsumer();
    private final ConnectionConsumer connection;
    private final Blob authenticationId;
    private final String localNamePrefix;

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public NMFConsumer(final ConnectionConsumer connection, final Blob authenticationId, final String localNamePrefix) {
        this.connection = connection;
        this.authenticationId = authenticationId;
        this.localNamePrefix = localNamePrefix;
        NMFConsumer.initHelpers();
    }

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     */
    public NMFConsumer(final ConnectionConsumer connection) {
        this(connection, null, null);
    }

    /**
     * The constructor of this class
     *
     * @param provider The Provider details. This object can be obtained from
     * the Directory service
     */
    public NMFConsumer(final Provider provider) {
        this(provider, null, null);
    }

    /**
     * The constructor of this class
     *
     * @param provider The Provider details. This object can be obtained from
     * the Directory service
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public NMFConsumer(final Provider provider, final Blob authenticationId, final String localNamePrefix) {
        NMFConsumer.initHelpers(); // The Helpers need to be initialized before conversion
        // Grab the provider variable and put it into a ConnectionConsumer
        this.connection = HelperCommon.providerToConnectionConsumer(provider);
        this.authenticationId = authenticationId;
        this.localNamePrefix = localNamePrefix;
    }

    /**
     * Loads the consumer properties and initializes the consumers for the COM, Monitor and
     * Control, Software Management and Platform services.
     */
    public void init() {
        try {
            HelperMisc.loadConsumerProperties();
        } catch (MalformedURLException ex) {
            Logger.getLogger(NMFConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            // Ignore the exception if it does not exist - the file is becoming deprecated
        }

        initCOMServices();
        initMCServices();
        initSMServices();
        initPlatformServices();
    }

    private void initCOMServices() {
        comServices.init(connection, authenticationId, localNamePrefix);
    }

    private void initMCServices() {
        mcServices.init(connection, comServices, authenticationId, localNamePrefix);
    }

    private void initPlatformServices() {
        platformServices.init(connection, comServices, authenticationId, localNamePrefix);
    }

    private void initSMServices() {
        smServices.init(connection, comServices, authenticationId, localNamePrefix);
    }

    /**
     * Requests the COM services.
     *
     * @return The COM services.
     */
    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    /**
     * Requests the MC services.
     *
     * @return The MC services.
     */
    public MCServicesConsumer getMCServices() {
        return mcServices;
    }

    /**
     * Requests the Platform services.
     *
     * @return The Platform services.
     */
    public PlatformServicesConsumer getPlatformServices() {
        return platformServices;
    }

    /**
     * Requests the Software Management services.
     *
     * @return The Software Management services
     */
    public SMServicesConsumer getSMServices() {
        return smServices;
    }

    /**
     * Requests the Connection Consumer object.
     *
     * @return The Connection Consumer object.
     */
    public ConnectionConsumer getConnectionConsumer() {
        return connection;
    }

    /**
     * Retrieves the complete list of Providers available on the Directory
     * service.
     *
     * @param directoryURI The Directory service URI.
     * @return The list of providers.
     * @throws org.ccsds.moims.mo.mal.MALException if there is a MAL exception.
     * @throws java.net.MalformedURLException if the URI is incorrect.
     * @throws org.ccsds.moims.mo.mal.MALInteractionException if it could not
     * reach the Directory service.
     */
    public static final ProviderList retrieveProvidersFromDirectory(final URI directoryURI)
            throws MALException, MalformedURLException, MALInteractionException {
        return NMFConsumer.retrieveProvidersFromDirectory(directoryURI, null, null);
    }

    /**
     * Retrieves the complete list of Providers available on the Directory
     * service.
     *
     * @param directoryURI The Directory service URI.
     * @param authenticationId The authenticationId.
     * @param localNamePrefix The local name prefix.
     * @return The list of providers.
     * @throws org.ccsds.moims.mo.mal.MALException if there is a MAL exception.
     * @throws java.net.MalformedURLException if the URI is incorrect.
     * @throws org.ccsds.moims.mo.mal.MALInteractionException if it could not
     * reach the Directory service.
     */
    public static final ProviderList retrieveProvidersFromDirectory(final URI directoryURI,
            final Blob authenticationId, final String localNamePrefix)
            throws MALException, MalformedURLException, MALInteractionException {
        return NMFConsumer.retrieveProvidersFromDirectory(directoryURI, authenticationId, localNamePrefix, null);
    }

    /**
     * Retrieves Providers from the Directory service, filtering service
     * addresses to only those whose URI starts with one of the given schemes.
     *
     * @param directoryURI The Directory service URI.
     * @param addressSchemeFilter URI scheme prefixes to match (e.g.
     * "malspp"). NULL returns all addresses.
     * @return The list of providers.
     * @throws org.ccsds.moims.mo.mal.MALException if there is a MAL exception.
     * @throws java.net.MalformedURLException if the URI is incorrect.
     * @throws org.ccsds.moims.mo.mal.MALInteractionException if it could not
     * reach the Directory service.
     */
    public static final ProviderList retrieveProvidersFromDirectory(final URI directoryURI,
            final IdentifierList addressSchemeFilter)
            throws MALException, MalformedURLException, MALInteractionException {
        return NMFConsumer.retrieveProvidersFromDirectory(directoryURI, null, null, addressSchemeFilter);
    }

    /**
     * Retrieves the complete list of Providers available on the Directory
     * service.
     *
     * @param directoryURI The Directory service URI.
     * @param localNamePrefix The local name prefix.
     * @return The list of providers.
     * @throws org.ccsds.moims.mo.mal.MALException if there is a MAL exception.
     * @throws java.net.MalformedURLException if the URI is incorrect.
     * @throws org.ccsds.moims.mo.mal.MALInteractionException if it could not
     * reach the Directory service.
     */
    public static final ProviderList retrieveProvidersFromDirectory(final URI directoryURI,
            final String localNamePrefix) throws MALException, MalformedURLException, MALInteractionException {
        return NMFConsumer.retrieveProvidersFromDirectory(directoryURI, null, localNamePrefix, null);
    }

    private static ProviderList retrieveProvidersFromDirectory(final URI directoryURI,
            final Blob authenticationId, final String localNamePrefix,
            final IdentifierList addressSchemeFilter)
            throws MALException, MalformedURLException, MALInteractionException {
        NMFConsumer.initHelpers();

        try {
            HelperMisc.loadConsumerProperties();
        } catch (MalformedURLException ex) {
            Logger.getLogger(NMFConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            // Ignore the exception if it does not exist - the file is becoming deprecated
            Logger.getLogger(NMFConsumer.class.getName()).log(Level.FINE, null, ex);
        }

        DirectoryConsumerServiceImpl directoryService = new DirectoryConsumerServiceImpl(
                directoryURI, authenticationId, localNamePrefix);

        IdentifierList wildcardList = new IdentifierList();
        wildcardList.add(new Identifier("*"));

        ServiceFilter filter = new ServiceFilter(new Identifier("*"),
                wildcardList,
                new ServiceId(new UShort((short) 0), new UShort((short) 0), new UOctet((short) 0)),
                addressSchemeFilter
        );

        ProviderList providers;
        try {
            providers = directoryService.getDirectoryStub().lookup(filter);
        } catch (MALException | MALInteractionException e) {
            throw e;
        } finally {
            directoryService.closeConnection();
        }

        return providers;
    }

    /**
     * Initializes the MAL Helpers for all the sets of services.
     */
    public static void initHelpers() {
        // Load the MAL factories for the supported services
        MALContextFactory.getElementsRegistry().loadFullArea(MALHelper.MAL_AREA);
        MALContextFactory.getElementsRegistry().loadFullArea(COMHelper.COM_AREA);
        MALContextFactory.getElementsRegistry().loadFullArea(MCHelper.MC_AREA);
        MALContextFactory.getElementsRegistry().loadFullArea(SMHelper.SM_AREA);
        MALContextFactory.getElementsRegistry().loadFullArea(PlatformHelper.PLATFORM_AREA);
    }

    /**
     * Sets the authentication id used for all the underlying service consumers.
     *
     * @param authenticationId the authentication id of the logged in user
     */
    public void setAuthenticationId(Blob authenticationId) {
        this.comServices.setAuthenticationId(authenticationId);
        this.connection.setAuthenticationId(authenticationId);
        this.mcServices.setAuthenticationId(authenticationId);
        this.platformServices.setAuthenticationId(authenticationId);
        this.smServices.setAuthenticationId(authenticationId);
    }

    /**
     * Returns the authentication id used by this consumer.
     *
     * @return the authentication id, or {@code null} if none was set
     */
    public Blob getAuthenticationId() {
        return authenticationId;
    }
}
