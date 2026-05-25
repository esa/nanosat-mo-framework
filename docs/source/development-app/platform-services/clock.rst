=====
Clock
=====

.. contents:: Table of contents
   :local:

The Clock service exposes the spacecraft's on-board clock to apps.

Defined in ``area105-Platform.xml`` as service number 8. Implemented by
``ClockProviderServiceImpl``; the adapter interface is
``ClockAdapterInterface``.

Operations
----------

Operations include retrieval of the current on-board time and (where
permitted) commanded time updates. See ``area105-Platform.xml`` for
the full operation list.

Consuming the service
---------------------

.. code-block:: java

   ClockStub clock = connector.getPlatformServices().getClockService();
   Time now = clock.getTime();

Adapter selection
-----------------

Selected via the ``clock.adapter`` property in
``platformsim.properties``. The simulated adapter returns the
simulator's modelled time, which advances according to the configured
real-time factor.

Reference example
-----------------

``sdk/examples-space/publish-clock`` and
``sdk/examples-space/periodic-alert`` demonstrate time-based behaviour.
