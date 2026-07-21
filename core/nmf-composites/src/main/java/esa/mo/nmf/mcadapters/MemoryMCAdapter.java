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
package esa.mo.nmf.mcadapters;

import esa.mo.mc.impl.interfaces.ActionNotFoundException;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterDefinitionList;

/**
 * A default Supervisor MC adapter exposing host RAM and swap telemetry as
 * read-only parameters, sourced from Linux {@code /proc}. Values that are not
 * available on the host default to zero.
 *
 * @author Cesar Coelho
 */
public class MemoryMCAdapter extends MonitorAndControlNMFAdapter {

    private static final String RAM_TOTAL = "memory.ram.total";
    private static final String RAM_USED = "memory.ram.used";
    private static final String RAM_PERCENTAGE = "memory.ram.percentage";
    private static final String SWAP_TOTAL = "memory.swap.total";
    private static final String SWAP_USAGE = "memory.swap.usage";
    private static final String SWAP_PERCENTAGE = "memory.swap.percentage";

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        ParameterDefinitionList defs = new ParameterDefinitionList();
        addParam(defs, RAM_TOTAL, "Total physical RAM.", AttributeType.LONG, "bytes");
        addParam(defs, RAM_USED, "Used physical RAM (total minus available).", AttributeType.LONG, "bytes");
        addParam(defs, RAM_PERCENTAGE, "Used physical RAM as a percentage.", AttributeType.DOUBLE, "%");
        addParam(defs, SWAP_TOTAL, "Total swap space.", AttributeType.LONG, "bytes");
        addParam(defs, SWAP_USAGE, "Used swap space.", AttributeType.LONG, "bytes");
        addParam(defs, SWAP_PERCENTAGE, "Used swap as a percentage.", AttributeType.DOUBLE, "%");
        registration.registerParameters(defs);
    }

    private static void addParam(ParameterDefinitionList defs, String name,
            String description, AttributeType type, String unit) {
        defs.add(new ParameterDefinition(new Identifier(name), description, type, unit,
                false, new Duration(0), null, null, true));
    }

    @Override
    public Attribute onGetValue(Identifier identifier, AttributeType rawType) {
        if (identifier == null) {
            return null;
        }
        switch (identifier.getValue()) {
            case RAM_TOTAL:
                return asLong(meminfoBytes("MemTotal"));
            case RAM_USED:
                return asLong(usedBytes("MemTotal", "MemAvailable"));
            case RAM_PERCENTAGE:
                return asDouble(usedPercentage("MemTotal", "MemAvailable"));
            case SWAP_TOTAL:
                return asLong(meminfoBytes("SwapTotal"));
            case SWAP_USAGE:
                return asLong(usedBytes("SwapTotal", "SwapFree"));
            case SWAP_PERCENTAGE:
                return asDouble(usedPercentage("SwapTotal", "SwapFree"));
            default:
                return null;
        }
    }

    @Override
    public void actionArrived(Identifier name, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction) throws ActionNotFoundException {
        throw new ActionNotFoundException(name == null ? null : name.getValue());
    }

    // ------------------------------------------------------------------------
    // Value sources (Linux /proc); return safe defaults if absent
    // ------------------------------------------------------------------------

    private static long usedBytes(String totalKey, String freeKey) {
        long total = meminfoBytes(totalKey);
        long free = meminfoBytes(freeKey);
        return (total >= 0 && free >= 0) ? total - free : -1;
    }

    private static double usedPercentage(String totalKey, String freeKey) {
        long total = meminfoBytes(totalKey);
        long free = meminfoBytes(freeKey);
        if (total <= 0 || free < 0) {
            return 0.0;
        }
        return 100.0 * (total - free) / total;
    }

    /** Reads a "Key: value kB" line from /proc/meminfo, returning bytes or -1. */
    private static long meminfoBytes(String key) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get("/proc/meminfo"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(key + ":")) {
                    return Long.parseLong(line.split("\\s+")[1]) * 1024L; // kB -> bytes
                }
            }
        } catch (Exception ex) {
            // File unavailable or unparsable: fall through to the default
        }
        return -1;
    }

    private static Attribute asLong(long value) {
        return (Attribute) Attribute.javaType2Attribute(value);
    }

    private static Attribute asDouble(double value) {
        return (Attribute) Attribute.javaType2Attribute(value);
    }
}
