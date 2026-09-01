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

    /**
     * Key of the spacecraft node in {@code etc/mission.properties}.
     */
    private static final String MISSION_KEY_NODE = "spacecraft.node";

    /**
     * Key in {@code etc/mission.properties} of whether the mission flies more
     * than one spacecraft.
     */
    private static final String MISSION_KEY_FLEET = "mission.fleet";

    /**
     * The node of a spacecraft whose mission does not say which it is.
     */
    private static final String DEFAULT_SPACECRAFT_NODE = "1";

    /**
     * Name in the environment of whether the mission flies more than one
     * spacecraft, for a spacecraft that is one of many built from one image.
     */
    public static final String ENV_MISSION_FLEET = "MISSION_FLEET";

    /**
     * Name in the environment of the node of this spacecraft, for a spacecraft
     * that is one of many built from one image.
     */
    public static final String ENV_SPACECRAFT_NODE = "SPACECRAFT_NODE";

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
        ensureFleetDomain(mission);
    }

    /**
     * Puts the node of the spacecraft into the domain, for a mission that flies
     * more than one.
     * <p>
     * The units of a fleet share a mission name, and the domain is otherwise
     * built out of the organization, the mission and the App, so two units of
     * one mission would be addressed identically. The node is what tells them
     * apart, so it is written between the mission and the App: the units of a
     * mission stay together, and the App keeps the place it has always had.
     * <p>
     * A mission of a single spacecraft is left alone. Its domain has nothing to
     * disambiguate, and adding a level to it would move every object already in
     * its archive.
     *
     * @param mission The contents of {@code etc/mission.properties}.
     */
    private static void ensureFleetDomain(Properties mission) {
        if (System.getProperty(HelperMisc.PROP_DOMAIN) != null) {
            return; // The domain was given outright; it is not ours to compose.
        }
        if (!Boolean.parseBoolean(fromEnvironmentOr(ENV_MISSION_FLEET,
                mission.getProperty(MISSION_KEY_FLEET)))) {
            return; // One spacecraft: the mission name is enough to address it.
        }

        String node = fromEnvironmentOr(ENV_SPACECRAFT_NODE,
                mission.getProperty(MISSION_KEY_NODE, DEFAULT_SPACECRAFT_NODE)).trim();
        String organization = System.getProperty(HelperMisc.PROP_ORGANIZATION_NAME);
        String missionName = System.getProperty(HelperMisc.PROP_MISSION_NAME);
        String app = System.getProperty(HelperMisc.PROP_MO_APP_NAME);

        // The domain is written as one string and split on the dot, so a value
        // holding one would quietly become two levels of domain.
        for (String part : new String[]{organization, missionName, node, app}) {
            if (part != null && part.contains(".")) {
                LOGGER.log(Level.WARNING, "The domain of this spacecraft cannot carry "
                        + "its node, because \"{0}\" contains a dot, which separates "
                        + "the levels of a domain. The units of this mission will be "
                        + "addressed identically.", part);
                return;
            }
        }

        StringBuilder domain = new StringBuilder();
        domain.append(organization).append('.').append(missionName).append('.').append(node);
        if (app != null) {
            domain.append('.').append(app);
        }
        System.setProperty(HelperMisc.PROP_DOMAIN, domain.toString());
        LOGGER.log(Level.INFO, "This mission flies more than one spacecraft, so the "
                + "domain carries the node of this one: {0}", domain);
    }

    /**
     * Reads a value the environment may override.
     * <p>
     * The file is written into the image at build time, so every spacecraft
     * built from one image is the same spacecraft. A spacecraft that is one of
     * many is told which it is when it is started, in the environment, beside
     * the orbit it flies.
     *
     * @param name The name in the environment.
     * @param fromFile The value from {@code mission.properties}, or null.
     * @return The value from the environment, or the one from the file where the
     * environment says nothing.
     */
    private static String fromEnvironmentOr(String name, String fromFile) {
        String value = System.getenv(name);
        return (value == null || value.trim().isEmpty()) ? fromFile : value;
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
