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
 * A Helper class dedicated to the NanoSat MO Framework specific properties and
 * constants
 */
public class Const {
    private Const() {
    }


    /** The central directory uri property. */
    public static final String CENTRAL_DIRECTORY_URI_PROPERTY = "esa.mo.nmf.centralDirectoryURI";
    /** The filename central directory service. */
    public static final String FILENAME_CENTRAL_DIRECTORY_SERVICE = "centralDirectoryService.uri";
    /** The dynamic changes property. */
    public static final String DYNAMIC_CHANGES_PROPERTY = "esa.mo.nanosatmoframework.provider.dynamicchanges";
    /** The appslauncher std limit property. */
    public static final String APPSLAUNCHER_STD_LIMIT_PROPERTY = "esa.mo.nanosatmoframework.appslauncher.stdlimit";
    /** The appslauncher std limit default. */
    public static final String APPSLAUNCHER_STD_LIMIT_DEFAULT = "2048";
    /** The appslauncher std store property. */
    public static final String APPSLAUNCHER_STD_STORE_PROPERTY = "esa.mo.nanosatmoframework.appslauncher.stdstore";
    /** The appslauncher std store default. */
    public static final String APPSLAUNCHER_STD_STORE_DEFAULT = "true";
    /** The archivesync purge archive property. */
    public static final String ARCHIVESYNC_PURGE_ARCHIVE_PROPERTY = "esa.mo.nanosatmoframework.archivesync.purgearchive";
    /** The archivesync purge archive default. */
    public static final String ARCHIVESYNC_PURGE_ARCHIVE_DEFAULT = "true";
    /** The nanosat mo supervisor name. */
    public static final String NANOSAT_MO_SUPERVISOR_NAME = "nanosat-mo-supervisor";
    /** The nanosat mo ground proxy name. */
    public static final String NANOSAT_MO_GROUND_PROXY_NAME = "ground-mo-proxy";
    /** The nmf package suffix. */
    public static final String NMF_PACKAGE_SUFFIX = "nmfpack";
    /** The platform iadcs caching period. */
    public static final String PLATFORM_IADCS_CACHING_PERIOD = "esa.mo.nmf.platform.iadcs.caching.period";
    /** The platform gnss caching period. */
    public static final String PLATFORM_GNSS_CACHING_PERIOD = "esa.mo.nmf.platform.gnss.caching.period";
    /** The platform gnss fallback to tle property. */
    public static final String PLATFORM_GNSS_FALLBACK_TO_TLE_PROPERTY = "esa.mo.nmf.platform.gnss.fallback.to.tle";
    /** The platform gnss fallback to tle default. */
    public static final String PLATFORM_GNSS_FALLBACK_TO_TLE_DEFAULT = "true";

    /* UTC offset in milliseconds */
    /** The platform gnss utc offset property. */
    public static final String PLATFORM_GNSS_UTC_OFFSET_PROPERTY = "esa.mo.nmf.platform.gnss.utc.offset";
    /** The platform gnss utc offset default. */
    public static final String PLATFORM_GNSS_UTC_OFFSET_DEFAULT = "-18.000";
    /** The archivesync chunk size property. */
    public static final String ARCHIVESYNC_CHUNK_SIZE_PROPERTY = "esa.nmf.archive.sync.chunk.size";
    /** The archivesync chunk size default. */
    public static final String ARCHIVESYNC_CHUNK_SIZE_DEFAULT = "200";
    /** The archivesync objects limit property. */
    public static final String ARCHIVESYNC_OBJECTS_LIMIT_PROPERTY = "esa.nmf.archive.sync.objects.limit";
    /** The archivesync objects limit default. */
    public static final String ARCHIVESYNC_OBJECTS_LIMIT_DEFAULT = "30000";
    /** The store in archive property. */
    public static final String STORE_IN_ARCHIVE_PROPERTY = "esa.nmf.parameters.storeInArchive";
}
