===================================
Importing the NMF into Eclipse IDE
===================================

.. contents:: Table of contents
    :local:

Getting started
---------------
Eclipse is supported as an alternative to NetBeans. Some minor issues may still occur; any problems encountered should be reported as a GitHub issue.
The recommended Eclipse version is 2019-03, although other versions are expected to work.

In Eclipse, select `File -> Import... -> Maven -> Existing Maven Projects -> Next`. Use the **Browse** button to navigate to the NMF root directory, uncheck **Add project(s) to working set**, and click **Finish**.
No import errors should occur in Eclipse 2019-03. Any errors that do appear are most likely of the form 'Plugin execution not covered by lifecycle configuration' for one of the Maven projects inside the NMF. These errors can typically be resolved locally; reporting them via the OPS-SAT experimenter platform or GitHub is appreciated so that fixes can be applied for the benefit of other users. Any other import errors should also be reported through the experimenter platform.

Resolving 'Plugin execution not covered by lifecycle configuration' errors
--------------------------------------------------------------------------
When developing apps with Maven, certain plugins may need to be invoked from ``pom.xml``. In some cases, Eclipse's m2e plugin cannot determine when a given plugin should be executed. The resolution is to add a configuration for the m2e lifecycle mapping plugin to ``pom.xml``. The following example may be used as a reference; one ``pluginExecution`` is required for each plugin that is not covered. The highlighted lines indicate the values that must be adapted to the specific scenario.

.. literalinclude:: m2e.xml
    :language: xml
    :linenos:
    :emphasize-lines: 12-14,16,25-27,29

Setting up the Supervisor with simulator
----------------------------------------
1. Select `File -> Import... -> Run/Debug -> Launch Configurations -> Next`.
2. Use the **Browse...** button to navigate to the **nanosat-mo-framework** folder, then to **sdk**, and select the **launch-configs** folder.
3. Import the **SupervisorSimulator.launch** file.
4. Right-click any imported project in the workspace and select `Run As -> Run Configurations...`.
5. Select `Maven Build -> SupervisorSimulator` (the configuration just imported). Set ``exec.executable`` to the path of the JDK ``java`` executable, and ``exec.workingdir`` to the execution directory of the Supervisor with simulator (typically a target directory produced by a full NMF SDK build). Switch to the **Environment** tab and define the variable **JAVA_HOME**, pointing to the JDK installation directory.
6. In the **Common** tab, enable **Run** and **Debug** in the **Display in favorites menu** panel.
7. Apply the settings and **Run** the Supervisor. The standard Supervisor output should appear on the Eclipse console.

Setting up the CTT
------------------
1. Select `File -> Import... -> Run/Debug -> Launch Configurations -> Next`.
2. Use the **Browse...** button to navigate to the **nanosat-mo-framework** folder, then to **sdk**, and select the **launch-configs** folder.
3. Import the **CTT.launch** file.
4. Right-click any imported project in the workspace and select `Run As -> Run Configurations...`.
5. Select `Maven Build -> CTT` (the configuration just imported). Set ``exec.executable`` to the path of the JDK ``java`` executable, and ``exec.workingdir`` to the execution directory of the Consumer Test Tool (typically a target directory produced by a full NMF SDK build). Switch to the **Environment** tab and define the variable **JAVA_HOME**, pointing to the JDK installation directory.
6. In the **Common** tab, enable **Run** and **Debug** in the **Display in favorites menu** panel.
7. Apply the settings and **Run** the CTT. The CTT output should appear on the Eclipse console.

Troubleshooting
---------------
Maven and Eclipse do not always interact reliably. The following points may help when problems are encountered:

1. If a Maven build fails, refresh the workspace by selecting all imported projects and pressing ``F5``.
2. To terminate the Supervisor, the red stop button next to the Eclipse console is insufficient; the process must be killed manually (for example, via Windows Task Manager).
3. Workspace errors other than 'Plugin execution not covered...' may safely be ignored.
4. Any further issues should be reported on the OPS-SAT community platform.

The Supervisor with simulator and the CTT can now be started from Eclipse by selecting the corresponding launch configuration from the drop-down menu next to the green **Run as...** button in the Eclipse toolbar. Proceed to :doc:`apps/apps` to continue.
