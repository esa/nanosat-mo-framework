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
   (``sdk/sdk-playground-environment/target/space-filesystem/nanosat-mo-framework``).
3. Add this VM option:

   .. code-block:: text

      -Dnmf.platform.impl=esa.mo.platform.impl.util.PlatformServicesProviderSoftSim

4. Save and run the project.

Setting up the CTT
^^^^^^^^^^^^^^^^^^

1. Right-click the project **ESA NMF SDK Tool - Consumer Test Tool (CTT)** and select **Properties**.
2. Under **Run**, set the working directory to the CTT module
   (``sdk/consumer-test-tool``).
3. Save and run.

Running a space app
^^^^^^^^^^^^^^^^^^^

1. Set the working directory to the installed app folder in the playground, e.g.
   ``sdk/sdk-playground-environment/target/space-filesystem/nanosat-mo-framework/apps/<app-name>/``.
   This directory already contains ``provider.properties`` and ``transport.properties`` after a build.
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

Running the Supervisor and the CTT
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The Supervisor (with simulator) and the Consumer Test Tool are launched from the playground environment
produced by a build, using the convenience scripts in ``sdk/sdk-playground-environment/`` —
``run_Supervisor.sh`` and ``run_CTT.sh``. Run them from a terminal (including the IDE's integrated
terminal). See :doc:`supervisor` and :doc:`ctt` for details.

Troubleshooting
^^^^^^^^^^^^^^^

- If a Maven build fails, refresh the workspace with ``F5``.
- The red stop button on the Eclipse console does not always terminate the Supervisor; kill the process
  manually if it persists.
- Workspace errors other than *Plugin execution not covered...* should be reported via GitHub or the OPS-SAT
  community platform.
