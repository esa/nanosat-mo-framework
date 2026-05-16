================================
Supervisor with simulator
================================

.. contents:: Table of contents
   :local:

The Supervisor is the process that owns the spacecraft-side of the NMF
runtime — hosting the SM and Platform services, the Directory Service,
and the supervision of apps it spawns (see
:doc:`../concepts/apps-and-supervisor`). The SDK includes a
**Supervisor with simulator** build that combines a Supervisor with
the in-process Software Simulator, so Platform services return
realistic data without requiring real hardware.

Running the Supervisor
----------------------

After ``mvn install``, the assembled SDK is under
``sdk/sdk-execution-environment/target/nmf-sdk-5.0-SNAPSHOT/``. Start
the Supervisor with simulator:

.. code-block:: bash

   sdk/sdk-execution-environment/target/nmf-sdk-5.0-SNAPSHOT/home/nmf/nanosat-mo-supervisor-sim/nanosat-mo-supervisor-sim.sh

On startup, the Supervisor prints:

- Its **Directory Service URI** in the form
  ``maltcp://<host>:<port>/nanosat-mo-supervisor-Directory``.
- Banners as each MO service registers.

Working directory contents
--------------------------

The Supervisor's working directory contains:

- ``provider.properties`` — provider-side runtime configuration.
- ``transport.properties`` — MAL transport selection (typically
  ``maltcp``).
- ``settings.properties`` — global NMF settings.
- ``logging.properties`` — ``java.util.logging`` configuration.
- ``platformsim.properties`` — per-service Platform adapter selection
  (sim vs hardware).
- ``providerURIs.properties`` — written at runtime; lists active
  provider URIs.
- ``comArchive.db`` — SQLite database backing the COM Archive.

Deleting ``comArchive.db`` before starting the Supervisor produces a
fresh archive.

Hybrid mode
-----------

The Supervisor with simulator supports **hybrid mode**: some Platform
services backed by the simulator, others by real hardware. The mode is
selected per service via the ``<service>.adapter`` property in
``platformsim.properties``. This is mainly used in mission-specific
test setups where some hardware (e.g. the camera) is available and the
rest is simulated.

Stopping the Supervisor
-----------------------

The Supervisor handles SIGTERM and shuts down gracefully, stopping
each running app via the ``AppsLauncher.stopApp`` operation before
terminating itself. From an IDE such as Eclipse, the red stop button
may not deliver SIGTERM reliably; if the process persists, kill it
manually.

See also
--------

- :doc:`simulator` — configuration of the in-process simulator.
- :doc:`../concepts/apps-and-supervisor` — the Supervisor's role in
  the runtime model.
