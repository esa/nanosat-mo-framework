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

package esa.mo.nmf.log_browser;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;

/**
 * Bunch of helper functions to query the central directory service.
 *
 * @author Tanguy Soto
 */
public class CentralDirectoryHelper {

  private static final Logger LOGGER = Logger.getLogger(CentralDirectoryHelper.class.getName());

  /**
   * Look up the central directory to find provider details of the given provider.
   * 
   * @param centralDirectoryServiceURI URI of the central directory to use
   * @param providerName Name of the provider to look for
   * @return ProviderSummary of the provider or null if not found
   */
  public static ProviderSummary getProviderSummary(URI centralDirectoryServiceURI,
      String providerName) {
    // Create provider filter
    IdentifierList domain = new IdentifierList();
    domain.add(new Identifier("*"));
    ServiceKey sk = new ServiceKey(new UShort(0), new UShort(0), new UOctet((short) 0));
    ServiceFilter sf2 = new ServiceFilter(new Identifier(providerName), domain, new Identifier("*"),
        null, new Identifier("*"), sk, new UIntegerList());

    // Query directory service with filter
    try (DirectoryConsumerServiceImpl centralDirectory = new DirectoryConsumerServiceImpl(centralDirectoryServiceURI)) {
      ProviderSummaryList providersSummaries =
          centralDirectory.getDirectoryStub().lookupProvider(sf2);
      if (providersSummaries.size() == 1) {
        LOGGER.log(Level.INFO, String.format("Found provider %s", providerName));
        return providersSummaries.get(0);
      } else if (providersSummaries.size() > 1) {
        LOGGER.log(Level.SEVERE,
            String.format("Found multiple providers with name %s", providerName));
      } else {
        LOGGER.log(Level.SEVERE,
            String.format("Couldn't find provider with name %s", providerName));
      }
    } catch (MALInteractionException | MALException | MalformedURLException e) {
      LOGGER.log(Level.SEVERE, "Error while looking up the central directory", e);
    }

    return null;
  }

  /**
   * Look up the central directory to find the list of providers that provides a COM archive
   * service.
   *
   * @param centralDirectoryServiceURI URI of the central directory to use
   * @return The list of providers
   */
  public static ArrayList<String> listCOMArchiveProviders(URI centralDirectoryServiceURI) {
    ArrayList<String> archiveProviders = new ArrayList<>();

    // Create archive provider filter
    IdentifierList domain = new IdentifierList();
    domain.add(new Identifier("*"));
    ServiceKey sk = new ServiceKey(COMHelper.COM_AREA_NUMBER, ArchiveHelper.ARCHIVE_SERVICE_NUMBER,
        new UOctet((short) 0));
    ServiceFilter sf2 = new ServiceFilter(new Identifier("*"), domain, new Identifier("*"), null,
        new Identifier("*"), sk, new UIntegerList());

    // Query directory service with filter
    try (DirectoryConsumerServiceImpl centralDirectory = new DirectoryConsumerServiceImpl(centralDirectoryServiceURI)) {
      ProviderSummaryList providersSummaries =
          centralDirectory.getDirectoryStub().lookupProvider(sf2);
      for (ProviderSummary providerSummary : providersSummaries) {
        final StringBuilder provider = new StringBuilder(providerSummary.getProviderName().getValue());

        ProviderDetails providerDetails = providerSummary.getProviderDetails();

        // dump provider addresses
        for (AddressDetails addressDetails : providerDetails.getProviderAddresses()) {
          provider.append("\n\t - ").append(addressDetails.getServiceURI().getValue());
        }

        // dump services capabilities addresses
        for (ServiceCapability serviceCapability : providerDetails.getServiceCapabilities()) {
          for (AddressDetails serviceAddressDetails : serviceCapability.getServiceAddresses()) {
            provider.append("\n\t - ").append(serviceAddressDetails.getServiceURI().getValue());
          }
        }
        archiveProviders.add(provider.toString());
      }
    } catch (MALInteractionException | MALException | MalformedURLException e) {
      LOGGER.log(Level.SEVERE, "Error while looking up the central directory", e);
    }

    return archiveProviders;
  }
}
