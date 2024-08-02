/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
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
package esa.mo.nmf.nmfpackage.utils;

import esa.mo.helpertools.misc.OSValidator;
import esa.mo.nmf.nmfpackage.Deployment;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;

/**
 * Generates the necessary auxiliary files.
 *
 * @author Cesar Coelho
 */
public class AuxFilesGenerator {

    private static String getBanner() {
        StringBuilder str = new StringBuilder();
        str.append("########################################################\n");
        str.append("#  This file was auto-generated by the NMF\n");
        str.append("#  during the installation of this App\n");
        str.append("#  We do not advise to change it directly!\n");
        str.append("#  The file is generated by the class: AuxFilesGenerator\n");
        str.append("########################################################\n");
        str.append("\n");
        return str.toString();
    }

    public static String generateLinuxStartAppScript(String javaCommand,
            MetadataApp meta) throws IOException {
        StringBuilder str = new StringBuilder();
        str.append("#!/bin/sh\n");
        str.append(getBanner());
        str.append("cd ${0%/*}\n\n");

        str.append("# Java Runtime Environment:\n");
        str.append("JAVA_CMD=").append(javaCommand).append("\n\n");
        str.append("# App-related constants:\n");
        str.append("MAIN_CLASS=").append(meta.getAppMainclass()).append("\n");
        str.append("MAX_HEAP=").append(meta.getAppMaxHeap()).append("\n");
        str.append("MIN_HEAP=").append(meta.getAppMinHeap()).append("\n\n");

        str.append("# Jars from: App, NMF, and Shared dependencies:\n");
        str.append("JAR_APP=").append(meta.getAppMainJar()).append("\n");
        // The following code must be changed:
        str.append("JARS_NMF=").append(Deployment.getJarsNMFDir()).append("\n");
        String shared = "";

        if (meta.hasDependencies()) {
            File sharedLibs = Deployment.getJarsSharedDir();
            String paths = meta.getAppDependenciesFullPaths(sharedLibs);
            str.append("JARS_SHARED=").append(paths);
            shared = ":$JARS_SHARED";
        }
        str.append("\n\nJARS_ALL=$JAR_APP:$JARS_NMF/*").append(shared);
        str.append("\n\n");

        str.append("if [ -z \"$JAVA_OPTS\" ] ; then\n");
        str.append("    JAVA_OPTS=\"-Xms$MIN_HEAP -Xmx$MAX_HEAP $JAVA_OPTS\"\n");
        str.append("fi\n\n");

        str.append("export JAVA_OPTS\n");
        str.append("export NMF_LIB\n\n");

        str.append("NOW=$(date +\"%F\")\n");
        String appName = meta.getPackageName();
        str.append("FILENAME=").append(appName).append("_$NOW.log\n");
        String logPath = Deployment.getLogsDirForApp(appName).getAbsolutePath();
        str.append("LOG_PATH=").append(logPath).append("\n");
        str.append("mkdir -p $LOG_PATH\n\n");

        // The command "exec" spawns the execution in a different process
        // str.append("exec -a MyUniqueProcessName ");
        str.append("$JAVA_CMD $JAVA_OPTS \\\n");
        str.append("  -classpath \"$JARS_ALL\" \\\n");
        str.append("  \"$MAIN_CLASS\" \\\n");
        str.append("  \"$@\" \\\n");
        str.append("  2>&1 | tee -a $LOG_PATH/$FILENAME \n");

        return str.toString();
    }

    public static String generateWindowsStartAppScript(String javaCommand,
            String jarFilename, MetadataApp meta) throws IOException {
        StringBuilder str = new StringBuilder();
        str.append("@echo off\n\n");
        str.append(":: Java Runtime Environment:\n");
        str.append("set JAVA_CMD=").append(javaCommand).append("\n\n");

        str.append(":: App-related constants:\n");
        str.append("set MAIN_CLASS=").append(meta.getAppMainclass()).append("\n");
        str.append("set MAX_HEAP=").append(meta.getAppMaxHeap()).append("\n");
        str.append("set MIN_HEAP=").append(meta.getAppMinHeap()).append("\n\n");

        str.append(":: Jars from: App, NMF, and Shared dependencies:\n");
        str.append("set JAR_APP=").append(jarFilename).append("\n");

        str.append("set BASEDIR=%~dp0\\..\\..\\..").append("\n");
        str.append("set JARS_NMF=").append("%BASEDIR%\\lib\\*").append("\n");

        str.append("\n\nset JARS_ALL=\"%JAR_APP%;%JARS_NMF%\"");
        str.append("\n\n");

        str.append("%JAVA_CMD% %JAVA_OPTS% -classpath %JARS_ALL%");
        str.append(" -Dapp.name=\"").append(meta.getPackageName());
        str.append("\" ").append(meta.getAppMainclass());
        return str.toString();
    }

    public static String generateProviderProperties(String runAs, String transportPath) throws IOException {
        StringBuilder str = new StringBuilder();
        str.append(getBanner());
        str.append("# MO App configurations\n");
        str.append(HelperMisc.PROP_ORGANIZATION_NAME).append("=").append("esa\n");
        str.append(HelperMisc.APP_CATEGORY).append("=").append("NMF_App\n");
        if (runAs != null) {
            str.append(HelperMisc.APP_USER).append("=").append(runAs).append("\n");
        }
        str.append("\n");

        str.append("# Enables the new Home directory mode: \n");
        str.append(HelperMisc.PROP_WORK_DIR_STORAGE_MODE + "=" + "2\n\n");

        str.append("# NanoSat MO Framework transport configuration\n");
        str.append("helpertools.configurations.provider.transportfilepath=");
        str.append(transportPath);
        str.append("\n\n");

        str.append("# NanoSat MO Framework Settings\n");
        str.append("esa.mo.nanosatmoframework.provider.settings=settings.properties");
        str.append("\n\n");

        str.append("# NanoSat MO Framework dynamic configurations\n");
        str.append("esa.mo.nanosatmoframework.provider.dynamicchanges=true");
        str.append("\n\n");

        str.append("# Cleanup space archive from synchronised objects\n");
        str.append("esa.mo.nanosatmoframework.archivesync.purgearchive=true\n");

        return str.toString();
    }

    public static String generateTransportProperties() throws IOException {
        StringBuilder str = new StringBuilder();
        str.append(getBanner());
        str.append("# The following property sets the protocol to be used:\n");
        str.append("org.ccsds.moims.mo.mal.transport.default.protocol = maltcp://\n");
        str.append("\n");

        str.append("# TCP/IP protocol properties:\n");
        str.append("org.ccsds.moims.mo.mal.transport.protocol.maltcp=esa.mo.mal.transport.tcpip.TCPIPTransportFactoryImpl\n");
        str.append("org.ccsds.moims.mo.mal.encoding.protocol.maltcp=esa.mo.mal.encoder.binary.fixed.FixedBinaryStreamFactory\n");

        // Bind to localhost
        str.append("org.ccsds.moims.mo.mal.transport.tcpip.autohost=true\n");
        str.append("#org.ccsds.moims.mo.mal.transport.tcpip.host=xxx.xxx.xxx.xxx\n");
        str.append("#org.ccsds.moims.mo.mal.transport.tcpip.port=1024\n");
        /*
        str.append("org.ccsds.moims.mo.mal.transport.tcpip.autohost=false\n");
        str.append("org.ccsds.moims.mo.mal.transport.tcpip.host=127.0.0.1\n");
        str.append("#org.ccsds.moims.mo.mal.transport.tcpip.port=54321\n");
         */

        return str.toString();
    }

    public static void generateStartScript(MetadataApp appDetails,
            File appDir, File nmfDir) throws IOException {
        String name = appDetails.getPackageName();
        String jarName = appDetails.getAppMainJar();

        if (jarName.equals("")) {
            File jar = HelperNMFPackage.findAppJarInFolder(appDir);
            jarName = jar.getName();
        }

        // The Java version for now will be forced to 8, however in
        // the future the package should recommend a version
        String javaCMD = Deployment.findJREPath(8, 8, 8);

        OSValidator os = new OSValidator();

        if (os.isUnix() || os.isMac()) {
            String content = generateLinuxStartAppScript(javaCMD, appDetails);
            File startApp = new File(appDir, "start_" + name + ".sh");
            writeFile(startApp, content);
            startApp.setExecutable(true, true);
        }

        if (os.isWindows()) {
            String content = generateWindowsStartAppScript(
                    javaCMD, jarName, appDetails);
            File startApp = new File(appDir, "start_" + name + ".bat");
            writeFile(startApp, content);
        }
    }

    public static void writeFile(File file, String content) {
        System.out.println("   >> Creating file on: " + file);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Handle the exception
        }
    }
}
