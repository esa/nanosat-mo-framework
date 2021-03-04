// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
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
 * Bunch of help functions.
 *
 * @author Tanguy Soto
 */
public class Helpers {

  private static final Logger LOGGER = Logger.getLogger(Helpers.class.getName());

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
    DirectoryConsumerServiceImpl centralDirectory = null;
    try {
      centralDirectory = new DirectoryConsumerServiceImpl(centralDirectoryServiceURI);
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

    } finally {
      if (centralDirectory != null) {
        centralDirectory.closeConnection();
      }
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
    ArrayList<String> archiveProviders = new ArrayList<String>();

    // Create archive provider filter
    IdentifierList domain = new IdentifierList();
    domain.add(new Identifier("*"));
    ServiceKey sk = new ServiceKey(COMHelper.COM_AREA_NUMBER, ArchiveHelper.ARCHIVE_SERVICE_NUMBER,
        new UOctet((short) 0));
    ServiceFilter sf2 = new ServiceFilter(new Identifier("*"), domain, new Identifier("*"), null,
        new Identifier("*"), sk, new UIntegerList());

    // Query directory service with filter
    DirectoryConsumerServiceImpl centralDirectory = null;
    try {
      centralDirectory = new DirectoryConsumerServiceImpl(centralDirectoryServiceURI);
      ProviderSummaryList providersSummaries =
          centralDirectory.getDirectoryStub().lookupProvider(sf2);
      for (ProviderSummary providerSummary : providersSummaries) {
        archiveProviders.add(providerSummary.getProviderName().getValue());
      }
    } catch (MALInteractionException | MALException | MalformedURLException e) {
      LOGGER.log(Level.SEVERE, "Error while looking up the central directory", e);

    } finally {
      if (centralDirectory != null) {
        centralDirectory.closeConnection();
      }
    }

    return archiveProviders;
  }
}
