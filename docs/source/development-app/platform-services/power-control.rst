=============
PowerControl
=============

.. contents:: Table of contents
   :local:

The PowerControl service exposes the spacecraft's power subsystem to apps. It provides bus and battery
telemetry and allows switching of controllable loads.

Defined in ``area105-Platform.xml`` as service number 7. Implemented by ``PowerControlProviderServiceImpl``;
the adapter interface is ``PowerControlAdapterInterface``.

Operations
----------

Operations include reading bus and battery state, listing controllable devices, and switching loads on or off.
See ``area105-Platform.xml`` for the full operation list.

Consuming the service
---------------------

.. code-block:: java

   PowerControlStub power = connector.getPlatformServices()
                                     .getPowerControlService();
   // read bus voltages, switch loads, etc.

Adapter selection
-----------------

Selected via the ``power.adapter`` property in ``platformsim.properties``. Mission-specific adapters target
the actual power subsystem (EPS / PCDU).
