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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * The Java-side read/write model of an NMF Software Baseline file
 * ({@code bootloader/baseline-<role>.properties}). It is the counterpart of the
 * {@code get_prop} reader in the bootloader shell script: the bootloader reads
 * these files, while the Supervisor (Parameter-service commanding and the
 * Package Management rotation) reads and writes them through this class.
 *
 * <p>
 * A baseline is the combination of a framework version, a mission software
 * version and a Java runtime, plus the Supervisor main class recorded so that
 * every version- or mission-specific value stays out of the bootloader itself.
 * See the NMF Bootloader Specification.
 *
 * @author Cesar Coelho
 */
public class SoftwareBaseline {

    /**
     * The version of the script-to-baseline-files interface contract. Must
     * match {@code BootloaderGenerator.SCHEMA_VERSION} and the {@code get_prop}
     * expectations in the bootloader script.
     */
    public static final String SCHEMA_VERSION = "1";

    private static final String KEY_SCHEMA_VERSION = "schema-version";
    /** Key of the NMF version in a baseline file. */
    public static final String KEY_NMF_VERSION = "nmf-version";
    /** Key of the mission version in a baseline file. */
    public static final String KEY_MISSION_VERSION = "mission-version";
    private static final String KEY_JAVA = "java";
    private static final String KEY_MAIN_CLASS = "main-class";

    private final String schemaVersion;
    private final String nmfVersion;
    private final String missionVersion;
    private final String java;
    private final String mainClass;

    /**
     * Constructor using the current {@link #SCHEMA_VERSION}.
     *
     * @param nmfVersion The NMF framework version.
     * @param missionVersion The mission software version.
     * @param java The Java runtime specifier ({@code system}, an absolute path,
     * or a path relative to the NMF root such as {@code java/<jre-version>/bin/java}).
     * @param mainClass The Supervisor main class.
     */
    public SoftwareBaseline(String nmfVersion, String missionVersion,
            String java, String mainClass) {
        this(SCHEMA_VERSION, nmfVersion, missionVersion, java, mainClass);
    }

    /**
     * Constructor.
     *
     * @param schemaVersion The schema version of the baseline file.
     * @param nmfVersion The NMF framework version.
     * @param missionVersion The mission software version.
     * @param java The Java runtime specifier.
     * @param mainClass The Supervisor main class.
     */
    public SoftwareBaseline(String schemaVersion, String nmfVersion,
            String missionVersion, String java, String mainClass) {
        this.schemaVersion = schemaVersion;
        this.nmfVersion = nmfVersion;
        this.missionVersion = missionVersion;
        this.java = java;
        this.mainClass = mainClass;
    }

    /**
     * Loads a baseline file. Missing keys yield {@code null} fields, so an
     * incomplete file can be recognised by the caller (the bootloader degrades
     * such a file to the next fallback rung).
     *
     * @param file The baseline file to read.
     * @return The parsed baseline.
     * @throws IOException if the file cannot be read.
     */
    public static SoftwareBaseline load(File file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        }
        return new SoftwareBaseline(
                props.getProperty(KEY_SCHEMA_VERSION),
                props.getProperty(KEY_NMF_VERSION),
                props.getProperty(KEY_MISSION_VERSION),
                props.getProperty(KEY_JAVA),
                props.getProperty(KEY_MAIN_CLASS));
    }

    /**
     * Stores this baseline to a file, atomically (written to a temporary file,
     * then renamed) so the bootloader never reads a half-written file. The
     * output matches the format generated by the {@code nmf-linux-maven-plugin}
     * at build time: a header comment followed by plain {@code key=value} lines.
     *
     * @param file The baseline file to write.
     * @throws IOException if the file cannot be written.
     */
    public void store(File file) throws IOException {
        // Reject line breaks in any field: values are written verbatim as
        // "key=value" lines, so an embedded newline would inject extra
        // properties into the file (property injection). This is the central
        // guard for every writer of a baseline file.
        String content = "# NMF Software Baseline\n"
                + KEY_SCHEMA_VERSION + "=" + requireSingleLine(schemaVersion) + "\n"
                + KEY_NMF_VERSION + "=" + requireSingleLine(nmfVersion) + "\n"
                + KEY_MISSION_VERSION + "=" + requireSingleLine(missionVersion) + "\n"
                + KEY_JAVA + "=" + requireSingleLine(java) + "\n"
                + KEY_MAIN_CLASS + "=" + requireSingleLine(mainClass) + "\n";

        File temp = new File(file.getParentFile(), file.getName() + ".tmp");
        Files.write(temp.toPath(), content.getBytes(StandardCharsets.UTF_8));
        Files.move(temp.toPath(), file.toPath(),
                StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Returns the schema version.
     *
     * @return the schema version
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Returns the nmf version.
     *
     * @return the nmf version
     */
    public String getNmfVersion() {
        return nmfVersion;
    }

    /**
     * Returns the mission version.
     *
     * @return the mission version
     */
    public String getMissionVersion() {
        return missionVersion;
    }

    /**
     * Returns the Java runtime version.
     *
     * @return the Java runtime version
     */
    public String getJava() {
        return java;
    }

    /**
     * Returns the main class.
     *
     * @return the main class
     */
    public String getMainClass() {
        return mainClass;
    }

    private static String requireSingleLine(String value) throws IOException {
        if (value == null) {
            return "";
        }
        if (value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            throw new IOException("A baseline field must not contain line breaks.");
        }
        return value;
    }
}
