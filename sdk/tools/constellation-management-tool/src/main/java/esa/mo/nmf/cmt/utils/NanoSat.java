/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */
package esa.mo.nmf.cmt.utils;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nmf.cmt.services.mc.ParameterGround;
import esa.mo.nmf.cmt.services.sm.AppManagerGround;
import esa.mo.nmf.cmt.services.sm.PackageManagerGround;
import esa.mo.nmf.commonmoadapter.CompleteDataReceivedListener;
import esa.mo.nmf.commonmoadapter.SimpleDataReceivedListener;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.FindPackageResponse;

public class NanoSat {

    protected String name;
    protected String ipAddress = null;
    protected ProviderSummaryList providers;
    protected GroundMOAdapterImpl groundAdapter;
    protected HashMap<String, Long> installedApps = new HashMap<>();

    // set active = false when segment is removed from constellation.
    // needed to avoid naming issues when adding new segments to the
    // constellation after others have been removed.
    protected Boolean active;

    /**
     * Initializer Constructor. This class manages the connection to a NanoSat
     * segment.
     *
     * @param name NanoSat segment name
     */
    public NanoSat(String name) {
        this.name = name;
        this.active = true;
    }

    /**
     * Initializer Constructor. This class manages the connection to a NanoSat
     * segment.
     */
    public NanoSat() {
        this.active = true;
    }

    /**
     * Creates an NMF App geofence
     *
     * @param coordinates List with GPS coordinates
     * @param appName name of the app to apply the geofence
     * @param startOnEnter true: start App when entering geofence, false: start
     * App when leaving geofence
     */
    public void createGeofence(ArrayList<String[]> coordinates, String appName, boolean startOnEnter) {
        UInteger rawValue = new UInteger(4711);
        ParameterRawValue value = new ParameterRawValue(10L, rawValue);
        ParameterGround.addParameter(this.groundAdapter, value);
    }

    /**
     * @return Container name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return message - no container logs available
     */
    public String getLogs() throws IOException {
        return "Logs are available for simulated NanoSat Segments only.";
    }

    /**
     * @return NanoSat segment IP address
     * @throws IOException
     */
    public String getIPAddress() throws IOException {
        if (this.ipAddress == null) {
            throw new IOException("IP Address is undefined");
        }
        return this.ipAddress;
    }

    /**
     * @param ipAddress NanoSat segment IP address
     */
    public void setIPAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @return Supervisor Directory Service URI
     * @throws IOException
     */
    public URI getDirectoryServiceURI() throws IOException {
        return new URI(this.getDirectoryServiceURIString());
    }

    /**
     * @return Supervisor Directory Service URI String
     * @throws IOException
     */
    public String getDirectoryServiceURIString() throws IOException {
        return ("maltcp://" + this.getIPAddress() + ":1024/nanosat-mo-supervisor-Directory");
    }

    /**
     * Overwritten by NanoSatSimulator.run()
     *
     * @throws IOException
     */
    public void run() throws IOException {
    }

    /**
     * Connect to the service providers on the NanoSat Segment
     */
    public void connectToProviders() {
        try {
            this.providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(this.getDirectoryServiceURI());

            if (!providers.isEmpty()) {
                // Connect to provider on index 0
                this.groundAdapter = new GroundMOAdapterImpl(this.providers.get(0));
                this.groundAdapter.addDataReceivedListener(new NanoSat.CompleteDataReceivedAdapter(this.getName()));
            } else {
                Logger.getLogger(NanoSat.class.getName()).log(Level.SEVERE,
                        "{0}: the returned list of providers is empty!", new Object[]{this.name});
            }
        } catch (MALException | MALInteractionException | IOException ex) {
            Logger.getLogger(NanoSat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Install the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name
     */
    public void installPackage(String packageName) {
        PackageManagerGround.installPackage(this.groundAdapter, packageName);
    }

    /**
     * Uninstall the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name
     */
    public void uninstallPackage(String packageName) {
        PackageManagerGround.uninstallPackage(this.groundAdapter, packageName);
    }

    /**
     * Update the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name
     */
    public void upgradePackage(String packageName) {
        PackageManagerGround.upgradePackage(this.groundAdapter, packageName);
    }

    /**
     * Runs an App by name. Ignores Apps that are not installed.
     *
     * @param appName Name of the App that shall be installed
     */
    public void runAppByName(String appName) {
        if (installedApps.get(appName) == null) {
            Logger.getLogger(NanoSat.class.getName()).log(Level.INFO,
                    "Cannot run App {0} on {1}: App not installed!",
                    new Object[]{appName, this.name});
            return;
        }
        AppManagerGround.runAppById(this.groundAdapter, installedApps.get(appName));
    }

    /**
     * Stops an App by name. Ignores Apps that are not installed.
     *
     * @param appName Name of the App that shall be stopped
     */
    public void stopAppByName(String appName) {
        if (installedApps.get(appName) == null) {
            Logger.getLogger(NanoSat.class.getName()).log(Level.INFO,
                    "Cannot stop App {0} on {1}: App not installed!",
                    new Object[]{appName, this.name});
            return;
        }
        AppManagerGround.stopAppById(this.groundAdapter, installedApps.get(appName));
    }

    /**
     * Retrieves the installed Apps from the NanoSat. Saves the IDs of the
     * installed Apps to installedApps and returns the list of installed Apps
     * for further processing
     *
     * @return list with installed Apps
     */
    public List<ArchivePersistenceObject> getAllApps() {

        List<ArchivePersistenceObject> apps = AppManagerGround.getApps(this.groundAdapter);

        if (apps != null) {
            for (ArchivePersistenceObject app : apps) {
                AppDetails appDetails = (AppDetails) app.getObject();

                if (installedApps.get(appDetails.getName().toString()) == null) {
                    installedApps.put(appDetails.getName().toString(), app.getArchiveDetails().getInstId());
                }
            }
        }

        return apps;
    }

    /**
     * Get all NMF packages that are available on the NanoSat Segment
     *
     * @return FindPackageResponse: provides the package name and its
     * installation status
     */
    public FindPackageResponse getAllPackages() {
        return PackageManagerGround.getAllPackages(this.groundAdapter);
    }

    /**
     * Remove the NanoSat segment from the constellation.
     */
    public void delete() {
        this.active = false;
        this.providers = null;
        this.groundAdapter = null;
    }

    /**
     * Remove the Simulation from the constellation.
     */
    public void deleteIfSimulation() {
    }

    /**
     * @return NanoSat segment active status
     */
    public Boolean isActive() {
        return this.active;
    }

    protected class CompleteDataReceivedAdapter extends CompleteDataReceivedListener {

        private String name;

        protected CompleteDataReceivedAdapter(String name) {
            this.name = name;
        }

        @Override
        public void onDataReceived(ParameterInstance parameterInstance) {
            Logger.getLogger(NanoSat.class.getName()).log(Level.INFO,
                    "\nNode: {0}\nParameter name: {1}\nParameter Value: {2}",
                    new Object[]{this.name, parameterInstance.getName(), parameterInstance.getParameterValue().toString(),});
        }
    }

    protected class SimpleDataReceivedAdapter extends SimpleDataReceivedListener {

        private String name;

        @Override
        public void onDataReceived(String parameterName, Serializable data) {
            Logger.getLogger(NanoSat.class.getName()).log(Level.INFO,
                    "\nNode: {0}\nParameter name: {1}\nParameter Value: {2}",
                    new Object[]{this.name, parameterName, data.toString()});
        }
    }
}
