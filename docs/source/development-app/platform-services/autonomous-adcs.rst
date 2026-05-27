=================
AutonomousADCS
=================

.. contents:: Table of contents
   :local:

The AutonomousADCS service exposes attitude determination and control to apps. It supports querying current
attitude and commanding the spacecraft to a target orientation or pointing mode.

Defined in ``area105-Platform.xml`` as service number 3. Implemented by ``AutonomousADCSProviderServiceImpl``;
the adapter interface is ``AutonomousADCSAdapterInterface``.

Operations
----------

Operations include attitude retrieval, target pointing commands, and status monitoring. See
``area105-Platform.xml`` for the full operation list and the relevant data structures.

Consuming the service
---------------------

.. code-block:: java

   AutonomousADCSStub adcs = connector.getPlatformServices()
                                      .getAutonomousADCSService();
   // command a target, query status, etc.

Simulated adapter
-----------------

The simulated ADCS adapter uses the spacecraft simulator's attitude propagator to return current orientation.
Configuration is via ``iadcs.adapter`` in ``platformsim.properties``.

Hardware adapters
-----------------

Mission-specific hardware adapters target the actual ADCS hardware:

- OPS-SAT uses the **iADCS-100** from Berlin Space Technologies.
- ɸ-Sat-2 uses its own ADCS implementation.

The hardware adapter is selected via the ``iadcs.adapter`` property.

Reference example
-----------------

``sdk/examples-space/camera-acquisitor-system`` uses ADCS together with the Camera service to point at targets
before capture.
