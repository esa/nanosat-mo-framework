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
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFProvider;
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.environment.SoftwareBaseline;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * A default Supervisor MC adapter for the NMF Bootloader (NMF Bootloader
 * Specification). It exposes the three software baseline files
 * ({@code bootloader/baseline-<role>.properties}) and the bootloader runtime
 * state as read-only parameters, and lets ground command the <em>primary</em>
 * baseline through a validated action.
 *
 * <p>
 * The <em>secondary</em> baseline is not operator-settable: it is written only
 * by the NMF itself, through the Package Management rotation on a confirmed
 * upgrade (NMF.BOOT.REC.04). The <em>factory</em> baseline is immutable in
 * flight (NMF.BOOT.BMM.03). A rollback to the secondary is therefore performed
 * by commanding {@code setPrimaryBaseline} with the secondary's field values.
 *
 * <p>
 * {@code setPrimaryBaseline} validates the request before accepting it
 * (NMF.BOOT.BMM.02): the requested framework and mission versions must be
 * installed on disk and pass their integrity tests, and the Java runtime must
 * execute. Each check is a reported stage (NMF.BOOT.REC.05); the first failure
 * rejects the whole command and leaves the baseline file untouched.
 *
 * @author Cesar Coelho
 */
public class BootloaderMCAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOGGER = Logger.getLogger(BootloaderMCAdapter.class.getName());

    private static final String PREFIX = "bootloader.";
    private static final String ACTION_SET_PRIMARY = "bootloader.setPrimaryBaseline";

    private static final String[] ROLES = {"primary", "secondary", "factory"};

    private static final String FIELD_NMF_VERSION = "nmf-version";
    private static final String FIELD_MISSION_VERSION = "mission-version";
    private static final String FIELD_JAVA = "java";
    private static final String FIELD_MAIN_CLASS = "main-class";
    private static final String[] FIELDS = {
        FIELD_NMF_VERSION, FIELD_MISSION_VERSION, FIELD_JAVA, FIELD_MAIN_CLASS
    };

    private static final String STATE_RUNG = "rung";
    private static final String STATE_FAILED_ATTEMPTS = "failed-attempts";

    private static final int SET_PRIMARY_STAGES = 5;

    private final NMFProvider provider;

    /**
     * Constructor.
     *
     * @param provider The provider used to report action execution progress.
     * May be {@code null}, in which case progress is not reported.
     */
    public BootloaderMCAdapter(NMFProvider provider) {
        this.provider = provider;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        ParameterDefinitionList defs = new ParameterDefinitionList();
        for (String role : ROLES) {
            for (String field : FIELDS) {
                defs.add(new ParameterDefinition(new Identifier(PREFIX + role + "." + field),
                        "The " + field + " of the " + role + " software baseline.",
                        AttributeType.STRING, false, new Duration(0), true));
            }
        }
        defs.add(new ParameterDefinition(new Identifier(PREFIX + STATE_RUNG),
                "The fallback ladder rung the bootloader will boot next (primary, secondary or factory).",
                AttributeType.STRING, false, new Duration(0), true));
        defs.add(new ParameterDefinition(new Identifier(PREFIX + STATE_FAILED_ATTEMPTS),
                "The number of consecutive failed boot attempts recorded for the current rung.",
                AttributeType.STRING, false, new Duration(0), true));
        registration.registerParameters(defs);

        ArgumentDefinitionList args = new ArgumentDefinitionList();
        args.add(new ArgumentDefinition(new Identifier(FIELD_NMF_VERSION), null, AttributeType.STRING, ""));
        args.add(new ArgumentDefinition(new Identifier(FIELD_MISSION_VERSION), null, AttributeType.STRING, ""));
        args.add(new ArgumentDefinition(new Identifier(FIELD_JAVA), null, AttributeType.STRING, ""));
        args.add(new ArgumentDefinition(new Identifier(FIELD_MAIN_CLASS), null, AttributeType.STRING, ""));

        ActionDefinitionList actionDefs = new ActionDefinitionList();
        actionDefs.add(new ActionDefinition(new Identifier(ACTION_SET_PRIMARY),
                "Sets the primary software baseline the bootloader will boot. The requested framework "
                + "and mission versions must be installed and pass their integrity tests, and the Java "
                + "runtime must execute, otherwise the command is rejected.",
                new UShort(SET_PRIMARY_STAGES), args));
        registration.registerActions(actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, AttributeType rawType) {
        if (identifier == null || identifier.getValue() == null
                || !identifier.getValue().startsWith(PREFIX)) {
            return null;
        }
        String rest = identifier.getValue().substring(PREFIX.length());

        if (STATE_RUNG.equals(rest) || STATE_FAILED_ATTEMPTS.equals(rest)) {
            return str(readState(rest));
        }

        int dot = rest.indexOf('.');
        if (dot < 0) {
            return null;
        }
        String role = rest.substring(0, dot);
        String field = rest.substring(dot + 1);
        SoftwareBaseline baseline = loadBaseline(role);
        if (baseline == null) {
            return str("");
        }
        switch (field) {
            case FIELD_NMF_VERSION:
                return str(baseline.getNmfVersion());
            case FIELD_MISSION_VERSION:
                return str(baseline.getMissionVersion());
            case FIELD_JAVA:
                return str(baseline.getJava());
            case FIELD_MAIN_CLASS:
                return str(baseline.getMainClass());
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
            Long executionId, MALInteraction interaction)
            throws ExecutionFailedException, ActionNotFoundException {
        if (name == null || !ACTION_SET_PRIMARY.equals(name.getValue())) {
            throw new ActionNotFoundException(name == null ? null : name.getValue());
        }
        setPrimaryBaseline(attributeValues, executionId);
    }

    /**
     * Validates and commits a new primary baseline. Runs the checks of
     * NMF.BOOT.BMM.02 as reported stages; any failure rejects the whole command.
     */
    private void setPrimaryBaseline(AttributeValueList attributeValues, Long executionId)
            throws ExecutionFailedException {
        String nmfVersion = argAsString(attributeValues, 0);
        String missionVersion = argAsString(attributeValues, 1);
        String java = argAsString(attributeValues, 2);
        String mainClass = argAsString(attributeValues, 3);

        // Stage 1 — arguments well-formed
        if (isBlank(nmfVersion) || isBlank(missionVersion) || isBlank(java) || isBlank(mainClass)) {
            fail(executionId, 1, "All baseline fields (nmf-version, mission-version, java, main-class) "
                    + "must be provided.");
        }
        if (isUnsafeSegment(nmfVersion) || isUnsafeSegment(missionVersion)) {
            fail(executionId, 1, "The nmf-version and mission-version must be plain directory names "
                    + "(no path separators or '..').");
        }
        report(true, 1, executionId);

        // Stage 2 — NMF baseline installed and intact
        File nmfDir = new File(Deployment.getJarsNMFDir(), nmfVersion);
        checkBaselineDir(nmfDir, "jars-nmf/" + nmfVersion, executionId, 2);
        report(true, 2, executionId);

        // Stage 3 — mission baseline installed and intact
        File missionDir = new File(Deployment.getJarsMissionDir(), missionVersion);
        checkBaselineDir(missionDir, "jars-mission/" + missionVersion, executionId, 3);
        report(true, 3, executionId);

        // Stage 4 — Java runtime resolves and executes (functional check, BTE.03)
        if (!javaRuntimeExecutes(java)) {
            fail(executionId, 4, "The Java runtime '" + java + "' does not execute.");
        }
        report(true, 4, executionId);

        // Stage 5 — commit: write the primary baseline file atomically
        try {
            File bootloaderDir = Deployment.getBootloaderDir();
            bootloaderDir.mkdirs();
            new SoftwareBaseline(nmfVersion, missionVersion, java, mainClass)
                    .store(new File(bootloaderDir, Deployment.FILE_BASELINE_PRIMARY));
        } catch (IOException ex) {
            fail(executionId, 5, "The primary baseline file could not be written: " + ex.getMessage());
        }
        report(true, 5, executionId);

        LOGGER.log(Level.INFO, "Primary baseline set: nmf={0} mission={1} java={2} main-class={3}",
                new Object[]{nmfVersion, missionVersion, java, mainClass});
    }

    private void checkBaselineDir(File dir, String label, Long executionId, int stage)
            throws ExecutionFailedException {
        if (!dir.isDirectory()) {
            fail(executionId, stage, "The baseline component " + label + " is not installed.");
        }
        try {
            if (!ChecksumGenerator.verifyChecksums(dir)) {
                fail(executionId, stage, "The baseline component " + label + " failed its integrity test.");
            }
        } catch (IOException ex) {
            fail(executionId, stage, "The baseline component " + label
                    + " could not be verified: " + ex.getMessage());
        }
    }

    /**
     * Resolves the Java runtime specifier exactly as the bootloader script does
     * ({@code system} → {@code java}; absolute path → as-is; otherwise relative
     * to the NMF root) and checks that it executes.
     */
    private static boolean javaRuntimeExecutes(String javaSpec) {
        String command;
        if ("system".equals(javaSpec)) {
            command = "java";
        } else {
            File file = new File(javaSpec);
            command = file.isAbsolute() ? javaSpec
                    : new File(Deployment.getNMFRootDir(), javaSpec).getPath();
        }
        try {
            Process process = new ProcessBuilder(command, "-version")
                    .redirectErrorStream(true).start();
            process.getInputStream().readAllBytes(); // drain, then wait
            return process.waitFor() == 0;
        } catch (IOException ex) {
            return false;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private void report(boolean success, int stage, Long executionId) {
        if (provider == null || executionId == null) {
            return;
        }
        try {
            provider.reportExecutionProgress(success, 0, stage, SET_PRIMARY_STAGES, executionId);
        } catch (NMFException ex) {
            LOGGER.log(Level.WARNING, "The action execution progress could not be reported.", ex);
        }
    }

    private void fail(Long executionId, int stage, String message) throws ExecutionFailedException {
        report(false, stage, executionId);
        throw new ExecutionFailedException(message);
    }

    private SoftwareBaseline loadBaseline(String role) {
        File file = new File(Deployment.getBootloaderDir(), "baseline-" + role + ".properties");
        if (!file.isFile()) {
            return null;
        }
        try {
            return SoftwareBaseline.load(file);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "The baseline file could not be read: " + file, ex);
            return null;
        }
    }

    private String readState(String key) {
        File file = new File(Deployment.getBootloaderDir(), Deployment.FILE_BOOTLOADER_STATE);
        if (!file.isFile()) {
            return "";
        }
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException ex) {
            return "";
        }
        return props.getProperty(key, "");
    }

    private static String argAsString(AttributeValueList values, int index) {
        if (values == null || index >= values.size() || values.get(index) == null) {
            return null;
        }
        Object value = Attribute.attribute2JavaType(values.get(index).getValue());
        return (value == null) ? null : value.toString();
    }

    private static Attribute str(String value) {
        return (Attribute) Attribute.javaType2Attribute(value == null ? "" : value);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isUnsafeSegment(String value) {
        return value.contains("..") || value.contains("/") || value.contains("\\");
    }
}
