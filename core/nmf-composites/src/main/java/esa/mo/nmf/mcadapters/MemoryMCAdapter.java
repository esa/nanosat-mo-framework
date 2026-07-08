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
import java.io.File;
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
import org.ccsds.moims.mo.mc.structures.ParameterValue;

/**
 * A default Supervisor MC adapter exposing host memory telemetry as read-only
 * parameters, sourced from Linux {@code /proc} and {@code /sys}. Values that
 * are not available on the host (e.g. ECC counters without EDAC, or pressure
 * without PSI) default to zero.
 *
 * @author Cesar Coelho
 */
public class MemoryMCAdapter extends MonitorAndControlNMFAdapter {

    private static final String RAM_TOTAL = "memory.ram.total";
    private static final String RAM_USED = "memory.ram.used";
    private static final String RAM_PERCENTAGE = "memory.ram.percentage";
    private static final String RAM_ERRORS_CORRECTED = "memory.ram.errors.corrected";
    private static final String RAM_ERRORS_UNCORRECTED = "memory.ram.errors.uncorrected";
    private static final String SWAP_TOTAL = "memory.swap.total";
    private static final String SWAP_USAGE = "memory.swap.usage";
    private static final String SWAP_PERCENTAGE = "memory.swap.percentage";
    private static final String PRESSURE = "memory.pressure";
    private static final String PAGE_FAULTS = "memory.page_faults";

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        ParameterDefinitionList defs = new ParameterDefinitionList();
        addParam(defs, RAM_TOTAL, "Total physical RAM.", AttributeType.LONG, "bytes");
        addParam(defs, RAM_USED, "Used physical RAM (total minus available).", AttributeType.LONG, "bytes");
        addParam(defs, RAM_PERCENTAGE, "Used physical RAM as a percentage.", AttributeType.DOUBLE, "%");
        addParam(defs, RAM_ERRORS_CORRECTED, "EDAC corrected memory errors.", AttributeType.LONG, "errors");
        addParam(defs, RAM_ERRORS_UNCORRECTED, "EDAC uncorrected memory errors.", AttributeType.LONG, "errors");
        addParam(defs, SWAP_TOTAL, "Total swap space.", AttributeType.LONG, "bytes");
        addParam(defs, SWAP_USAGE, "Used swap space.", AttributeType.LONG, "bytes");
        addParam(defs, SWAP_PERCENTAGE, "Used swap as a percentage.", AttributeType.DOUBLE, "%");
        addParam(defs, PRESSURE, "Memory pressure (PSI 'some' avg10).", AttributeType.DOUBLE, "%");
        addParam(defs, PAGE_FAULTS, "Cumulative page faults since boot.", AttributeType.LONG, "faults");
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
            case RAM_ERRORS_CORRECTED:
                return asLong(edacErrors("ce_count"));
            case RAM_ERRORS_UNCORRECTED:
                return asLong(edacErrors("ue_count"));
            case SWAP_TOTAL:
                return asLong(meminfoBytes("SwapTotal"));
            case SWAP_USAGE:
                return asLong(usedBytes("SwapTotal", "SwapFree"));
            case SWAP_PERCENTAGE:
                return asDouble(usedPercentage("SwapTotal", "SwapFree"));
            case PRESSURE:
                return asDouble(memoryPressure());
            case PAGE_FAULTS:
                return asLong(vmstat("pgfault"));
            default:
                return null;
        }
    }

    @Override
    public ParameterValue getValueWithCustomValidityState(Attribute rawValue, ParameterDefinition pDef) {
        return null;
    }

    @Override
    public void actionArrived(Identifier name, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction) throws ActionNotFoundException {
        throw new ActionNotFoundException(name == null ? null : name.getValue());
    }

    // ------------------------------------------------------------------------
    // Value sources (Linux /proc and /sys); return safe defaults if absent
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

    /** Reads a "key value" line from /proc/vmstat, returning the count or -1. */
    private static long vmstat(String key) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get("/proc/vmstat"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(key + " ")) {
                    return Long.parseLong(line.split("\\s+")[1]);
                }
            }
        } catch (Exception ex) {
            // File unavailable or unparsable
        }
        return -1;
    }

    /** Sums an EDAC counter (ce_count/ue_count) over all memory controllers. */
    private static long edacErrors(String counterFile) {
        File[] controllers = new File("/sys/devices/system/edac/mc")
                .listFiles((dir, name) -> name.startsWith("mc"));
        if (controllers == null) {
            return 0; // No EDAC on this host
        }
        long total = 0;
        for (File mc : controllers) {
            try {
                total += Long.parseLong(Files.readString(new File(mc, counterFile).toPath()).trim());
            } catch (Exception ex) {
                // Missing counter on this controller: ignore
            }
        }
        return total;
    }

    /** Parses the PSI 'some avg10' value from /proc/pressure/memory (0 if absent). */
    private static double memoryPressure() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get("/proc/pressure/memory"))) {
            String line = br.readLine(); // "some avg10=.. avg60=.. avg300=.. total=.."
            if (line != null) {
                for (String token : line.split("\\s+")) {
                    if (token.startsWith("avg10=")) {
                        return Double.parseDouble(token.substring("avg10=".length()));
                    }
                }
            }
        } catch (Exception ex) {
            // No PSI support on this host
        }
        return 0.0;
    }

    private static Attribute asLong(long value) {
        return (Attribute) Attribute.javaType2Attribute(value);
    }

    private static Attribute asDouble(double value) {
        return (Attribute) Attribute.javaType2Attribute(value);
    }
}
