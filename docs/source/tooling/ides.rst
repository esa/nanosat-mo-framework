================
IDE integration
================

.. contents:: Table of contents
   :local:

NMF development is fully supported in NetBeans and Eclipse. Either works; choose based on personal preference.

NetBeans
--------

Getting started
^^^^^^^^^^^^^^^

NetBeans imports the NMF Maven projects out of the box. Select ``File → Open Project`` and choose the NMF root
directory cloned from GitHub. All Maven sub-projects are imported into the workspace.

Setting up the Supervisor with simulator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

1. Right-click the project **ESA NMF Core Composite - NanoSat MO Supervisor** and select **Properties**.
2. Under **Run**, set the working directory to the Supervisor with simulator path
   (``sdk/sdk-execution-environment/target/nmf-sdk-5.0-SNAPSHOT/home/nmf/nanosat-mo-supervisor-sim``).
3. Add this VM option:

   .. code-block:: text

      -Dnmf.platform.impl=esa.mo.platform.impl.util.PlatformServicesProviderSoftSim

4. Save and run the project.

Setting up the CTT
^^^^^^^^^^^^^^^^^^

1. Right-click the project **ESA NMF SDK Tool - Consumer Test Tool (CTT)** and select **Properties**.
2. Under **Run**, set the working directory to the CTT execution environment
   (``sdk/sdk-execution-environment/target/nmf-sdk-5.0-SNAPSHOT/home/nmf/consumer-test-tool``).
3. Save and run.

Running a space app
^^^^^^^^^^^^^^^^^^^

1. Set the working directory to a folder containing ``provider.properties``, ``settings.properties``, and
   ``transport.properties`` (templates available under the SDK execution environment's ``space-app-root`` and
   ``space-common`` resources).
2. Add the VM option:

   .. code-block:: text

      -Desa.mo.nmf.centralDirectoryURI=maltcp://host:port/nanosat-mo-supervisor-Directory

3. Run the project. The app starts and registers with the Supervisor's Directory Service.

Eclipse
-------

Getting started
^^^^^^^^^^^^^^^

In Eclipse, select ``File → Import... → Maven → Existing Maven Projects → Next``. Browse to the NMF root
directory, uncheck **Add project(s) to working set**, and click **Finish**.

The recommended Eclipse version is 2019-03; other versions are expected to work.

If errors of the form *Plugin execution not covered by lifecycle configuration* appear, add a configuration
for the m2e lifecycle mapping plugin to the relevant ``pom.xml`` (see the reference ``m2e.xml`` template
included in the docs source).

Setting up the Supervisor with simulator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

1. ``File → Import... → Run/Debug → Launch Configurations → Next``.
2. Browse to ``sdk/launch-configs`` under the NMF root.
3. Import **SupervisorSimulator.launch**.
4. Right-click any imported project and select ``Run As → Run Configurations...``.
5. Select ``Maven Build → SupervisorSimulator``. Set:

   - ``exec.executable`` — path to the JDK ``java`` executable.
   - ``exec.workingdir`` — path to the Supervisor with simulator execution directory.
   - In the **Environment** tab, define ``JAVA_HOME``.

6. In the **Common** tab, enable **Run** and **Debug** in the *Display in favorites menu* panel.
7. Apply and run.

Setting up the CTT
^^^^^^^^^^^^^^^^^^

Repeat the same procedure with the **CTT.launch** launch configuration, setting ``exec.workingdir`` to the CTT
execution directory.

Troubleshooting
^^^^^^^^^^^^^^^

- If a Maven build fails, refresh the workspace with ``F5``.
- The red stop button on the Eclipse console does not always terminate the Supervisor; kill the process
  manually if it persists.
- Workspace errors other than *Plugin execution not covered...* should be reported via GitHub or the OPS-SAT
  community platform.
