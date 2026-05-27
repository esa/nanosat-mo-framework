==================
Software Simulator
==================

.. contents:: Table of contents
   :local:

The **Software Simulator** is a CubeSat simulator modelled on the OPS-SAT spacecraft. It provides synthetic
data for the NMF's Platform services so apps can be tested against realistic inputs without real hardware.

The simulator is integrated into the Supervisor with simulator (see :doc:`supervisor`). It can also be
configured externally via a companion GUI client.

What the simulator provides
---------------------------

- **Orbital propagation** via Orekit, producing time, position, and velocity at realistic rates.
- **Attitude propagation** for the ADCS simulator.
- **GPS sentences** generated from the propagated orbit.
- **Camera images** drawn from a fixed file or a random pool.
- Plus stubs for the remaining Platform services.

Configuration files
-------------------

On first launch, the simulator generates a set of configuration files in its working directory and unpacks
resources to ``~/.ops-sat-simulator``. The two main files are:

- ``_OPS-SAT-SIMULATOR-header.txt`` — general simulator configuration.
- ``platformsim.properties`` — per-service adapter selection and service-specific parameters.

Both use Java properties syntax. Edit the value on the right-hand side of each assignment.

General configuration (``_OPS-SAT-SIMULATOR-header.txt``):

- Internal model processing on/off
- Simulated time advancement and real-time factor
- Keplerian orbital elements
- Use of the Orekit library
- Updating GPS constellation TLEs from the Internet
- Celestia visualisation port
- Simulation start and end dates
- Logging verbosity

Platform configuration (``platformsim.properties``):

- ``<service>.adapter`` — selects the adapter class for each Platform service (simulator or hardware).
- ``camerasim.imagemode`` — ``Fixed`` or ``Random``.
- ``camerasim.imagefile`` — image returned in ``Fixed`` mode.
- ``camerasim.imagedirectory`` — directory sampled in ``Random`` mode.

Running the simulator UI
------------------------

The simulator is, in essence, a TCP server. A companion GUI client connects to a running simulator and lets
the operator inspect and modify its configuration interactively.

To start the GUI from NetBeans, right-click the ``ESA OPS-SAT - Spacecraft Simulator`` project, select
**Run**, and choose ``opssat.simulator.main.MainClient`` as the main class.

From Eclipse, import the ``SimClient`` launch configuration from ``sdk/launch-configs`` and run it.

Connecting the UI to the simulator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

When the server and client run on the same machine, the client connects to ``localhost`` automatically. For a
remote simulator, enter the host and port (``11111`` by default) in the centre of the window.

General configuration via the UI
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The top of the window shows the real-time factor and simulation start. The ``Edit Header`` button opens a
dialog covering the same options as ``_OPS-SAT-SIMULATOR-header.txt`` plus the simulation end date. ``Submit
to server`` applies changes to the running simulator.

Platform configuration via the UI
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``Camera Settings`` tab exposes the camera-simulator configuration (fixed vs random image source and
paths). For a local simulator, ``Browse`` opens the local file explorer; for a remote simulator, SFTP must be
enabled on the server so a basic SFTP browser can be displayed.

Limitations
-----------

- The simulator does not model every spacecraft subsystem in detail — some Platform services return stubbed
  responses rather than physically realistic data.
- Real-time-factor accuracy depends on the host's load.
- The configurable UI currently covers only the camera service; other platform configuration is via the
  properties file.

Background
----------

For the design of the simulator and the modelling choices behind it, see the Software Simulator MSc Thesis and
Software Simulator User Manual referenced under :doc:`../background/index`.
