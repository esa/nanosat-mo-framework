================================
Supervisor with simulator
================================

.. contents:: Table of contents
   :local:

The Supervisor is the process that owns the spacecraft-side of the NMF runtime — hosting the SM and Platform
services, the Directory Service, and the supervision of apps it spawns (see
:doc:`../concepts/apps-and-supervisor`). The SDK includes a **Supervisor with simulator** build that combines
a Supervisor with the in-process Software Simulator, so Platform services return realistic data without
requiring real hardware.

Running the Supervisor
----------------------

After ``mvn install``, start the Supervisor with simulator from the SDK Playground Environment:

.. code-block:: bash

   sdk/sdk-playground-environment/run_Supervisor.sh

On startup, the Supervisor prints:

- Its **Directory Service URI** in the form ``maltcp://<host>:<port>/nanosat-mo-supervisor-Directory``.
- Banners as each MO service registers.

Working directory contents
--------------------------

The Supervisor's working directory contains:

- ``provider.properties`` — provider-side runtime configuration.
- ``transport.properties`` — MAL transport selection (typically ``maltcp``).
- ``logging.properties`` — ``java.util.logging`` configuration.
- ``platformsim.properties`` — settings for the simulated Camera; written by the simulator itself.
- ``providerURIs.properties`` — written at runtime; lists active provider URIs.
- ``comArchive.db`` — SQLite database backing the COM Archive.

Deleting ``comArchive.db`` before starting the Supervisor produces a fresh archive.

Real hardware
-------------

Every Platform service of the Supervisor with simulator is answered by the simulator. There is no way to put
real hardware behind some of them and simulate the rest.

Until 2026 there was a **hybrid mode** for exactly that. It named a class per adapter in
``platformsim.properties`` and loaded each one by name at startup, so that the simulator could run on the
spacecraft itself, or on a flatsat, with the hardware that was really present answering for itself and the
rest simulated. OPS-SAT flew that way. The adapters belonged to the mission rather than to this repository.

That route is no longer pursued. A mission that wants its own hardware behind the Platform services now
provides its own implementation of them, as a dependency the build can see, instead of naming classes in a
configuration file for the Supervisor to find and load while starting.

Stopping the Supervisor
-----------------------

The Supervisor handles SIGTERM and shuts down gracefully, stopping each running app via the
``AppsLauncher.stopApp`` operation before terminating itself. From an IDE such as Eclipse, the red stop button
may not deliver SIGTERM reliably; if the process persists, kill it manually.

See also
--------

- :doc:`simulator` — configuration of the in-process simulator.
- :doc:`../concepts/apps-and-supervisor` — the Supervisor's role in the runtime model.
