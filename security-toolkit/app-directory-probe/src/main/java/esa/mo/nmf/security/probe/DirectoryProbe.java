/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.security.probe;

import esa.mo.com.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.SimpleMonitorAndControlAdapter;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.ccsds.moims.mo.com.structures.AddressDetails;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.com.structures.ServiceCapability;
import org.ccsds.moims.mo.com.structures.ServiceFilter;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.ServiceId;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A benign NMF App for the test of NMF-2026-011. It tries to take over the
 * Supervisor's entry in the Directory service, so that a look-up of the
 * Supervisor leads to this App instead:
 * <ul>
 * <li>packaged as {@code directory-probe}, it adds an entry named
 * {@code nanosat-mo-supervisor} that lists its own service addresses;</li>
 * <li>packaged as {@code nanosat-mo-supervisor}, it does nothing more than
 * every App does: it registers under its own name when it starts.</li>
 * </ul>
 * It then looks up the Supervisor and writes what it found to
 * {@code /tmp/<name>-report}, as {@code KEY=value} lines, and keeps running,
 * so that ground can look up the Supervisor while it runs. The addresses that
 * it registers are its own, and it provides only what every App provides.
 */
public class DirectoryProbe {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private final StringBuilder report = new StringBuilder();

    /**
     * Starts as a normal App, with no parameters or actions of its own.
     */
    public DirectoryProbe() {
        connector.init(new SimpleMonitorAndControlAdapter() {
            @Override
            public void initialRegistrations(MCRegistration registration) {
                // The probe exposes no parameters or actions.
            }

            @Override
            public Serializable onGetValueSimple(String name) {
                return null;
            }

            @Override
            public boolean onSetValueSimple(String name, Serializable value) {
                return false;
            }

            @Override
            public boolean actionArrivedSimple(String name, Serializable[] values, Long executionId) {
                return false;
            }
        });
    }

    /**
     * Runs the probe, writes its report and keeps the App running.
     *
     * @param args unused
     * @throws InterruptedException if the App is interrupted while it runs
     */
    public static void main(final String[] args) throws InterruptedException {
        DirectoryProbe probe = new DirectoryProbe();
        String name = System.getProperty(HelperMisc.PROP_MO_APP_NAME, "directory-probe");
        try {
            probe.probe(name);
        } catch (Throwable t) {
            probe.report.append("PROBE_ERROR=").append(t).append('\n');
        }
        probe.writeReport("/tmp/" + name + "-report");
        new CountDownLatch(1).await();
    }

    private void probe(String name) throws Exception {
        URI directoryURI = connector.readCentralDirectoryServiceURI();
        if (directoryURI == null) {
            report.append("PROBE_ERROR=no central Directory\n");
            return;
        }
        DirectoryConsumerServiceImpl directory = new DirectoryConsumerServiceImpl(directoryURI);

        ProviderList own = lookUp(directory, name);
        if (own.isEmpty()) {
            report.append("PROBE_ERROR=the App's own entry was not found in the Directory\n");
            return;
        }
        report.append("OWN_ADDRESSES=").append(addressesOf(own)).append('\n');

        if (!Const.NANOSAT_MO_SUPERVISOR_NAME.equals(name)) {
            Provider entry = own.get(0);
            Provider takeover = new Provider(null, new Identifier(Const.NANOSAT_MO_SUPERVISOR_NAME),
                    entry.getDomain(), entry.getServiceCapabilities(), entry.getProviderAddresses(),
                    entry.getProviderType());
            try {
                directory.getDirectoryStub().add(takeover);
                report.append("ADD=ACCEPTED\n");
            } catch (Exception refused) {
                report.append("ADD=REFUSED ").append(refused).append('\n');
            }
        }

        ProviderList supervisor = lookUp(directory, Const.NANOSAT_MO_SUPERVISOR_NAME);
        report.append("SUPERVISOR_ENTRIES=").append(supervisor.size()).append('\n');
        report.append("SUPERVISOR_ADDRESSES=").append(addressesOf(supervisor)).append('\n');
    }

    private static ProviderList lookUp(DirectoryConsumerServiceImpl directory, String name) throws Exception {
        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier("*"));
        ServiceId anyService = new ServiceId(new UShort(0), new UShort(0), new UOctet((short) 0));
        return directory.getDirectoryStub().lookup(new ServiceFilter(new Identifier(name), domain, anyService, null));
    }

    private static List<String> addressesOf(ProviderList providers) {
        List<String> addresses = new ArrayList<>();
        for (Provider provider : providers) {
            for (ServiceCapability capability : provider.getServiceCapabilities()) {
                for (AddressDetails address : capability.getServiceAddresses()) {
                    addresses.add(address.getServiceURI().getValue());
                }
            }
        }
        return addresses;
    }

    private void writeReport(String path) {
        try {
            Files.write(Paths.get(path), report.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("DIRECTORY-PROBE: could not write " + path + " (" + e + ")");
        }
        System.out.print("DIRECTORY-PROBE REPORT:\n" + report);
    }
}
