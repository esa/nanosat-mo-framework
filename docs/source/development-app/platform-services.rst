==============================
Accessing Platform services
==============================

.. contents:: Table of contents
   :local:

The Supervisor exposes the spacecraft's platform functions through the **Platform** service category (see
:doc:`../concepts/mo-architecture`). An app consumes these services through its Connector:

.. code-block:: java

   GPSStub gps = connector.getPlatformServices().getGPSService();
   CameraStub camera = connector.getPlatformServices().getCameraService();
   // ... and so on

Each Platform service has a corresponding **adapter interface** on the provider side. The Supervisor uses one
of two adapter implementations per service:

- A **simulated adapter** (loaded by the Supervisor with simulator) that returns realistic synthetic data,
  suitable for ground testing.
- A **hardware adapter** provided by a mission-specific module that forwards calls to the real spacecraft
  equipment.

Which adapter is loaded is configured per-service via the ``<service>.adapter`` property in
``platformsim.properties`` in the Supervisor's working directory (see :doc:`../tooling/simulator`).

Camera
------

The Camera service exposes the spacecraft's imaging payload to apps. It supports configurable resolution,
pixel format, exposure time, and RGB channel gains.

Defined in ``area105-Platform.xml`` as service number 1. Implemented by ``CameraProviderServiceImpl``; the
adapter interface is ``CameraAdapterInterface``.

Capturing an image
^^^^^^^^^^^^^^^^^^

The primary operation is ``takePicture``, which accepts a ``CameraSettings`` request and delivers the captured
``Picture`` to a caller-supplied ``CameraAdapter``:

.. code-block:: java

   PixelResolution resolution = new PixelResolution(
           new UInteger(width), new UInteger(height));

   CameraSettings settings = new CameraSettings(
           resolution, PictureFormat.BMP,
           new Duration(exposureTime),
           gainR, gainG, gainB);

   connector.getPlatformServices()
            .getCameraService()
            .takePicture(settings, new MyCameraAdapter());

The ``MyCameraAdapter`` subclass overrides ``takePictureResponseReceived(...)`` to receive the captured image.
The image data is wrapped in the CCSDS ``Picture`` structure, exposing the bytes via a ``Blob`` and the
settings actually used during capture.

Supported pixel formats include ``BMP``, ``PNG``, and ``RAW``; the exact set depends on the loaded adapter.

Simulated adapter
^^^^^^^^^^^^^^^^^

The default simulated adapter returns one of two image sources, controlled by ``camerasim.imagemode`` in
``platformsim.properties``:

- ``Fixed`` — always returns the image at ``camerasim.imagefile``.
- ``Random`` — returns a random image from the directory at ``camerasim.imagedirectory``.

Hardware adapter
^^^^^^^^^^^^^^^^

Mission-specific hardware adapters target the actual on-board camera. OPS-SAT uses the BST IMS-100 via a
dedicated adapter; ɸ-Sat-2 uses its multispectral imager. The hardware adapter is selected via the
``camera.adapter`` property in ``platformsim.properties``.

Reference example
^^^^^^^^^^^^^^^^^

``sdk/examples-space/camera`` consumes the Camera service end to end and is a good starting point for
adaptation.

GPS
---

The GPS service exposes the spacecraft's GPS receiver to apps. It provides NMEA sentence access and a
higher-level position interface.

Defined in ``area105-Platform.xml`` as service number 2. Implemented by ``GPSProviderServiceImpl``; the
adapter interface is ``GPSAdapterInterface``. A ``GPSNMEAonlyAdapter`` is provided for receivers that only
emit NMEA.

Operations
^^^^^^^^^^

The service supports retrieval of NMEA sentences by identifier and parsed position / velocity / time data. See
``area105-Platform.xml`` for the full operation list.

Consuming the service
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   GPSStub gps = connector.getPlatformServices().getGPSService();
   String gga = gps.getNMEASentence("GGA");
   // ... parse or use the result

Simulated adapter
^^^^^^^^^^^^^^^^^

The simulated GPS adapter generates NMEA-compliant data based on the simulator's orbital model
(Orekit-driven). Configuration:

- ``gps.adapter`` — selects the adapter class (simulator vs hardware).
- Whether to update GPS constellation TLEs from the Internet is controlled by a flag in the simulator header
  (see :doc:`../tooling/simulator`).

Reference example
^^^^^^^^^^^^^^^^^

``sdk/examples-space/gps`` exposes GPS-derived parameters via the MC Parameter service.

AutonomousADCS
--------------

The AutonomousADCS service exposes attitude determination and control to apps. It supports querying current
attitude and commanding the spacecraft to a target orientation or pointing mode.

Defined in ``area105-Platform.xml`` as service number 3. Implemented by ``AutonomousADCSProviderServiceImpl``;
the adapter interface is ``AutonomousADCSAdapterInterface``.

Operations
^^^^^^^^^^

Operations include attitude retrieval, target pointing commands, and status monitoring. See
``area105-Platform.xml`` for the full operation list and the relevant data structures.

Consuming the service
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   AutonomousADCSStub adcs = connector.getPlatformServices()
                                      .getAutonomousADCSService();
   // command a target, query status, etc.

Simulated adapter
^^^^^^^^^^^^^^^^^

The simulated ADCS adapter uses the spacecraft simulator's attitude propagator to return current orientation.
Configuration is via ``iadcs.adapter`` in ``platformsim.properties``.

Hardware adapters
^^^^^^^^^^^^^^^^^

Mission-specific hardware adapters target the actual ADCS hardware:

- OPS-SAT uses the **iADCS-100** from Berlin Space Technologies.
- ɸ-Sat-2 uses its own ADCS implementation.

The hardware adapter is selected via the ``iadcs.adapter`` property.

Reference example
^^^^^^^^^^^^^^^^^

``sdk/examples-space/camera-acquisitor-system`` uses ADCS together with the Camera service to point at targets
before capture.

SoftwareDefinedRadio
--------------------

The SoftwareDefinedRadio (SDR) service exposes the spacecraft's software-defined radio payload to apps. It
allows configuring the radio's RF parameters and streaming samples.

Defined in ``area105-Platform.xml`` as service number 4. Implemented by
``SoftwareDefinedRadioProviderServiceImpl``; the adapter interface is
``SoftwareDefinedRadioAdapterInterface``.

Operations
^^^^^^^^^^

Operations include configuration of frequency, bandwidth, and gain, and sample streaming. See
``area105-Platform.xml`` for the full operation list.

Consuming the service
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   SoftwareDefinedRadioStub sdr = connector.getPlatformServices()
                                           .getSoftwareDefinedRadioService();
   // configure and stream

Adapter selection
^^^^^^^^^^^^^^^^^

Selected via the ``sdr.adapter`` property in ``platformsim.properties``. Mission-specific adapters target the
actual SDR hardware (e.g. the OPS-SAT SDR).

OpticalDataReceiver
-------------------

The OpticalDataReceiver service exposes the spacecraft's optical communication receiver to apps.

Defined in ``area105-Platform.xml`` as service number 5. Implemented by
``OpticalDataReceiverProviderServiceImpl``; the adapter interface is ``OpticalDataReceiverAdapterInterface``.

Operations
^^^^^^^^^^

Operations include receiver configuration and data stream access. See ``area105-Platform.xml`` for details.

Consuming the service
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   OpticalDataReceiverStub optrx = connector.getPlatformServices()
                                            .getOpticalDataReceiverService();

Adapter selection
^^^^^^^^^^^^^^^^^

Selected via the ``optrx.adapter`` property in ``platformsim.properties``.

PowerControl
------------

The PowerControl service exposes the spacecraft's power subsystem to apps. It provides bus and battery
telemetry and allows switching of controllable loads.

Defined in ``area105-Platform.xml`` as service number 7. Implemented by ``PowerControlProviderServiceImpl``;
the adapter interface is ``PowerControlAdapterInterface``.

Operations
^^^^^^^^^^

Operations include reading bus and battery state, listing controllable devices, and switching loads on or off.
See ``area105-Platform.xml`` for the full operation list.

Consuming the service
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   PowerControlStub power = connector.getPlatformServices()
                                     .getPowerControlService();
   // read bus voltages, switch loads, etc.

Adapter selection
^^^^^^^^^^^^^^^^^

Selected via the ``power.adapter`` property in ``platformsim.properties``. Mission-specific adapters target
the actual power subsystem (EPS / PCDU).

Clock
-----

The Clock service exposes the spacecraft's on-board clock to apps.

Defined in ``area105-Platform.xml`` as service number 8. Implemented by ``ClockProviderServiceImpl``; the
adapter interface is ``ClockAdapterInterface``.

Operations
^^^^^^^^^^

Operations include retrieval of the current on-board time and (where permitted) commanded time updates. See
``area105-Platform.xml`` for the full operation list.

Consuming the service
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   ClockStub clock = connector.getPlatformServices().getClockService();
   Time now = clock.getTime();

Adapter selection
^^^^^^^^^^^^^^^^^

Selected via the ``clock.adapter`` property in ``platformsim.properties``. The simulated adapter returns the
simulator's modelled time, which advances according to the configured real-time factor.

Reference example
^^^^^^^^^^^^^^^^^

``sdk/examples-space/publish-clock`` and ``sdk/examples-space/periodic-alert`` demonstrate time-based
behaviour.

ArtificialIntelligence
----------------------

The ArtificialIntelligence service exposes on-board AI inference capabilities to apps. It allows an app to
submit data to a deployed model and retrieve predictions.

Defined in ``area105-Platform.xml`` as service number 9. Implemented by
``ArtificialIntelligenceProviderServiceImpl``; the adapter interface is ``AIAdapterInterface``.

Operations
^^^^^^^^^^

Operations include model selection, inference invocation, and result retrieval. See ``area105-Platform.xml``
for the full operation list.

Consuming the service
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   ArtificialIntelligenceStub ai = connector.getPlatformServices()
                                            .getArtificialIntelligenceService();
   // submit input, retrieve predictions

Adapter selection
^^^^^^^^^^^^^^^^^

Selected via the ``ai.adapter`` property in ``platformsim.properties``. A reference hardware adapter,
``AIMovidiusAdapter``, targets the Intel Movidius VPU used on ɸ-Sat-2 for accelerated on-board inference.

Reference example
^^^^^^^^^^^^^^^^^

``sdk/examples-space/edge-ai`` demonstrates on-board inference, and the ɸ-Sat-2 mission deployed apps in this
category for image classification (see :doc:`app-chaining` for the cloud-tile filtering chain).
