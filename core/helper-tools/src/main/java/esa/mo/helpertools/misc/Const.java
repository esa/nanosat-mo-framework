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
package esa.mo.helpertools.misc;

/**
 * A Helper class dedicated to the NanoSat MO Framework specific properties and constants
 */
public class Const
{

  public final static String CENTRAL_DIRECTORY_URI_PROPERTY = "esa.mo.nmf.centralDirectoryURI";
  public static final String FILENAME_CENTRAL_DIRECTORY_SERVICE = "centralDirectoryService.uri";
  public static final String DYNAMIC_CHANGES_PROPERTY
      = "esa.mo.nanosatmoframework.provider.dynamicchanges";
  public static final String APPSLAUNCHER_STD_LIMIT_PROPERTY
      = "esa.mo.nanosatmoframework.appslauncher.stdlimit";
  public static final String APPSLAUNCHER_STD_LIMIT_DEFAULT = "2048";
  public static final String APPSLAUNCHER_STD_STORE_PROPERTY
      = "esa.mo.nanosatmoframework.appslauncher.stdstore";
  public static final String APPSLAUNCHER_STD_STORE_DEFAULT = "true";
  public static final String ARCHIVESYNC_PURGE_ARCHIVE_PROPERTY
      = "esa.mo.nanosatmoframework.archivesync.purgearchive";
  public static final String ARCHIVESYNC_PURGE_ARCHIVE_DEFAULT = "true";
  public static final String NANOSAT_MO_SUPERVISOR_NAME = "nanosat-mo-supervisor";
  public final static String NANOSAT_MO_GROUND_PROXY_NAME = "ground-mo-proxy";
  public final static String NMF_PACKAGE_SUFFIX = "nmfpack";
}
