/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.filesystem;

import java.util.ArrayList;
import java.util.List;

/**
 * The mission and spacecraft designation supplied through the plugin's
 * {@code <mission>} configuration element. The values are written by the
 * filesystem generator into the {@code etc/mission.properties} file. See the
 * NMF documentation page {@code development-mission/mission-properties} for the
 * meaning of each field.
 *
 * <p>
 * Maven injects the nested configuration into the fields of this class by
 * matching the {@code <mission>} child element names to the field names.
 *
 * @author Cesar Coelho
 */
public class Mission {

    private static final int DEFAULT_SPACECRAFT_NODE = 1;

    /**
     * The name of the mission. Required.
     */
    private String missionName;

    /**
     * The name of this individual spacecraft. Required.
     */
    private String spacecraftName;

    /**
     * The spacecraft's unit number within the mission. Optional; defaults to
     * {@value #DEFAULT_SPACECRAFT_NODE}.
     */
    private Integer spacecraftNode;

    /**
     * Whether the mission flies more than one spacecraft. Optional; false when
     * not set.
     * <p>
     * A mission of one spacecraft is addressed by its name alone, because there
     * is nothing to tell apart. A fleet is not: its units share a mission name,
     * so the node of the spacecraft joins the domain to keep them distinct.
     */
    private Boolean fleet;

    /**
     * The spacecraft's CCSDS Spacecraft Identifier (SCID). Optional; omitted
     * from the generated file when not set.
     */
    private String spacecraftScid;

    /**
     * The operating organization's short acronym. Required.
     */
    private String organizationAbbreviation;

    /**
     * The operating organization's full name. Optional; omitted from the
     * generated file when not set.
     */
    private String organizationName;

    /**
     * Default constructor. Maven populates the fields from the nested {@code <mission>}
     * configuration element.
     */
    public Mission() {
    }

    /**
     * Returns the mission name.
     *
     * @return the mission name
     */
    public String getMissionName() {
        return missionName;
    }

    /**
     * Returns the spacecraft name.
     *
     * @return the spacecraft name
     */
    public String getSpacecraftName() {
        return spacecraftName;
    }

    /**
     * Returns the spacecraft's unit number within the mission.
     *
     * @return the spacecraft node, or {@code null} if not set
     */
    public Integer getSpacecraftNode() {
        return spacecraftNode;
    }

    /**
     * Returns the spacecraft's CCSDS Spacecraft Identifier (SCID).
     *
     * @return the spacecraft SCID, or {@code null} if not set
     */
    public String getSpacecraftScid() {
        return spacecraftScid;
    }

    /**
     * Returns the operating organization's short acronym.
     *
     * @return the organization abbreviation
     */
    public String getOrganizationAbbreviation() {
        return organizationAbbreviation;
    }

    /**
     * Returns the operating organization's full name.
     *
     * @return the organization name, or {@code null} if not set
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Verifies that the required fields ({@code name}, {@code spacecraftName}
     * and {@code organizationAbbreviation}) are present.
     *
     * @throws IllegalArgumentException if one or more required fields are
     * missing or blank; the message lists all of them.
     */
    public void checkRequiredFields() {
        List<String> missing = new ArrayList<>();
        if (isBlank(missionName)) {
            missing.add("missionName");
        }
        if (isBlank(spacecraftName)) {
            missing.add("spacecraftName");
        }
        if (isBlank(organizationAbbreviation)) {
            missing.add("organizationAbbreviation");
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("missing required <mission> field(s): "
                    + String.join(", ", missing));
        }
    }

    /**
     * Builds the content of the {@code mission.properties} file. The optional
     * {@code spacecraft.scid} and {@code organization.name} lines are only
     * written when the corresponding value is set, and {@code spacecraft.node}
     * defaults to {@value #DEFAULT_SPACECRAFT_NODE} when not set.
     *
     * @return The content of the file.
     */
    public String toPropertiesContent() {
        int node = (spacecraftNode != null) ? spacecraftNode : DEFAULT_SPACECRAFT_NODE;

        StringBuilder str = new StringBuilder();
        str.append("# NanoSat MO Framework - Mission and Spacecraft Designation\n");
        str.append("# Generated by the nmf-linux-maven-plugin.\n");
        str.append("\n");
        str.append("mission.name=").append(missionName).append("\n");
        str.append("mission.fleet=").append(fleet != null && fleet).append("\n");
        str.append("spacecraft.name=").append(spacecraftName).append("\n");
        str.append("spacecraft.node=").append(node).append("\n");
        if (!isBlank(spacecraftScid)) {
            str.append("spacecraft.scid=").append(spacecraftScid).append("\n");
        }
        str.append("organization.abbreviation=").append(organizationAbbreviation).append("\n");
        if (!isBlank(organizationName)) {
            str.append("organization.name=").append(organizationName).append("\n");
        }
        return str.toString();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
