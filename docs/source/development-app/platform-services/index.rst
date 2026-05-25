==============================
Accessing Platform services
==============================

The Supervisor exposes the spacecraft's platform functions through the
**Platform** service category (see :doc:`../../concepts/mo-architecture`).
An app consumes these services through its Connector:

.. code-block:: java

   GPSStub gps = connector.getPlatformServices().getGPSService();
   CameraStub camera = connector.getPlatformServices().getCameraService();
   // ... and so on

Each Platform service has a corresponding **adapter interface** on the
provider side. The Supervisor uses one of two adapter implementations
per service:

- A **simulated adapter** (loaded by the Supervisor with simulator)
  that returns realistic synthetic data, suitable for ground testing.
- A **hardware adapter** provided by a mission-specific module that
  forwards calls to the real spacecraft equipment.

Which adapter is loaded is configured per-service via the
``<service>.adapter`` property in ``platformsim.properties`` in the
Supervisor's working directory (see :doc:`../../tooling/simulator`).

Services
--------

.. toctree::
   :maxdepth: 1

   camera
   gps
   autonomous-adcs
   software-defined-radio
   optical-data-receiver
   power-control
   clock
   artificial-intelligence
