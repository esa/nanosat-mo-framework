/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
package esa.mo.nmf.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;

/**
 * Resolves the MO domain identity (organization and mission names) before the
 * domain is first constructed and cached by the MAL
 * {@code ConfigurationProviderSingleton}.
 *
 * <p>
 * The resolution is layered and non-destructive. A value already present as a
 * System property (set with a {@code -D} flag on the command line, or loaded
 * from a {@code provider.properties} file) is always kept. Only when a value is
 * missing is it filled in from {@code etc/mission.properties}, and only when
 * that too is unavailable does it fall back to a built-in default:
 *
 * <ol>
 * <li>an already-set System property ({@code -D} or a loaded properties file);</li>
 * <li>{@code etc/mission.properties}, when present and readable;</li>
 * <li>the built-in {@link #DEFAULT_ORGANIZATION_NAME} / {@link #DEFAULT_MISSION_NAME}.</li>
 * </ol>
 *
 * <p>
 * Because a default always exists, every properties file is optional: the
 * domain is always constructible, even when an App is run straight from an IDE
 * with no configuration at all, so it never degrades to the
 * {@code domainNotFoundInPropertiesFile} placeholder.
 *
 * @author Cesar Coelho
 */
public class MissionConfiguration {

    private static final Logger LOGGER = Logger.getLogger(MissionConfiguration.class.getName());

    /**
     * Default organization name used when no configuration provides one.
     */
    public static final String DEFAULT_ORGANIZATION_NAME = "nmf";

    /**
     * Default mission name used when no configuration provides one.
     */
    public static final String DEFAULT_MISSION_NAME = "dev";

    /**
     * Key of the organization abbreviation in {@code etc/mission.properties}.
     */
    private static final String MISSION_KEY_ORGANIZATION = "organization.abbreviation";

    /**
     * Key of the mission name in {@code etc/mission.properties}.
     */
    private static final String MISSION_KEY_MISSION = "mission.name";

    private MissionConfiguration() {
        // Utility class: prevent instantiation.
    }

    /**
     * Ensures that the domain identity System properties (OrganizationName and
     * MissionName) hold a value, so the MO domain can always be constructed.
     * Must be called before the first call to
     * {@code ConfigurationProviderSingleton.getDomain()}, which computes and
     * caches the domain for the rest of the process. Values already set (via
     * {@code -D} or a loaded properties file) are left untouched.
     */
    public static void ensureDomainIdentity() {
        Properties mission = loadMissionProperties();
        setIfAbsent(HelperMisc.PROP_ORGANIZATION_NAME,
                mission.getProperty(MISSION_KEY_ORGANIZATION), DEFAULT_ORGANIZATION_NAME);
        setIfAbsent(HelperMisc.PROP_MISSION_NAME,
                mission.getProperty(MISSION_KEY_MISSION), DEFAULT_MISSION_NAME);
    }

    /**
     * Sets a System property only when it is not already set, preferring the
     * mission.properties value and otherwise using the built-in default.
     *
     * @param key The System property key.
     * @param missionValue The value from mission.properties, or null.
     * @param fallback The built-in default value.
     */
    private static void setIfAbsent(String key, String missionValue, String fallback) {
        if (System.getProperty(key) != null) {
            return; // Already provided via -D or a properties file; keep it.
        }
        String value = (missionValue != null && !missionValue.trim().isEmpty())
                ? missionValue : fallback;
        System.setProperty(key, value);
    }

    /**
     * Reads {@code etc/mission.properties} if it is present, returning an empty
     * {@link Properties} when the file is absent or cannot be read.
     *
     * @return The mission properties; never null.
     */
    public static Properties loadMissionProperties() {
        Properties props = new Properties();
        File file = new File(Deployment.getEtcDir(), Deployment.FILE_MISSION_PROPERTIES);
        if (!file.exists()) {
            return props;
        }
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Could not read {0}", file.getPath());
        }
        return props;
    }
}
