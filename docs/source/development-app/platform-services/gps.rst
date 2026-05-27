===
GPS
===

.. contents:: Table of contents
   :local:

The GPS service exposes the spacecraft's GPS receiver to apps. It provides NMEA sentence access and a
higher-level position interface.

Defined in ``area105-Platform.xml`` as service number 2. Implemented by ``GPSProviderServiceImpl``; the
adapter interface is ``GPSAdapterInterface``. A ``GPSNMEAonlyAdapter`` is provided for receivers that only
emit NMEA.

Operations
----------

The service supports retrieval of NMEA sentences by identifier and parsed position / velocity / time data. See
``area105-Platform.xml`` for the full operation list.

Consuming the service
---------------------

.. code-block:: java

   GPSStub gps = connector.getPlatformServices().getGPSService();
   String gga = gps.getNMEASentence("GGA");
   // ... parse or use the result

Simulated adapter
-----------------

The simulated GPS adapter generates NMEA-compliant data based on the simulator's orbital model
(Orekit-driven). Configuration:

- ``gps.adapter`` — selects the adapter class (simulator vs hardware).
- Whether to update GPS constellation TLEs from the Internet is controlled by a flag in the simulator header
  (see :doc:`../../tooling/simulator`).

Reference example
-----------------

``sdk/examples-space/gps`` exposes GPS-derived parameters via the MC Parameter service.
