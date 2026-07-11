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

The full operation list, request/response composites, and error codes for every service on this page are
defined in ``core/mo-services-xml/src/main/resources/xml/area054-Platform.xml``. Each section below
summarises what the service does and how to call it.

This page covers the **consumer side** only. For configuring which adapter (simulated or hardware) the
Supervisor loads for each Platform service, see :doc:`../tooling/simulator`. For implementing a
mission-specific hardware adapter, see :doc:`../development-mission/platform-services`.

Camera
------

Captures images from the spacecraft's imaging payload. Supports configurable resolution, pixel format,
exposure time, and RGB channel gains. Supported pixel formats include ``BMP``, ``PNG``, and ``RAW``; the
exact set depends on the loaded adapter.

The primary operation is ``takePicture``, which accepts a ``CameraSettings`` request and delivers the
captured ``Picture`` to a caller-supplied ``CameraAdapter`` subclass:

.. code-block:: java

   PixelResolution resolution = new PixelResolution(new UInteger(width), new UInteger(height));

   CameraSettings settings = new CameraSettings(
           resolution, PictureFormat.BMP, new Duration(exposureTime),
           gainR, gainG, gainB);

   connector.getPlatformServices().getCameraService().takePicture(settings, new MyCameraAdapter());

The ``MyCameraAdapter`` subclass overrides ``takePictureResponseReceived(...)`` to receive the captured
image. The image data is wrapped in the CCSDS ``Picture`` structure, exposing the bytes via a ``Blob`` and
the settings actually used during capture.

Reference example: ``sdk/examples-space/camera``.

GPS
---

Exposes the spacecraft's GPS receiver. Supports retrieval of NMEA sentences by identifier and parsed
position / velocity / time data.

.. code-block:: java

   GPSStub gps = connector.getPlatformServices().getGPSService();
   String gga = gps.getNMEASentence("GGA");
   // ... parse or use the result

Reference example: ``sdk/examples-space/gps``.

AutonomousADCS
--------------

Attitude determination and control. Supports querying current attitude and commanding the spacecraft to a
target orientation or pointing mode.

.. code-block:: java

   AutonomousADCSStub adcs = connector.getPlatformServices().getAutonomousADCSService();
   // command a target, query status, etc.

Reference example: ``sdk/examples-space/camera-acquisitor-system`` uses ADCS together with the Camera service
to point at targets before capture.

SoftwareDefinedRadio
--------------------

Configuration of the software-defined radio payload (frequency, bandwidth, gain) and sample streaming.

.. code-block:: java

   SoftwareDefinedRadioStub sdr = connector.getPlatformServices().getSoftwareDefinedRadioService();
   // configure and stream

OpticalDataReceiver
-------------------

Receiver configuration and data stream access for the optical communication receiver.

.. code-block:: java

   OpticalDataReceiverStub optrx = connector.getPlatformServices().getOpticalDataReceiverService();

PowerControl
------------

Power-subsystem telemetry and load switching. Operations include reading bus and battery state, listing
controllable devices, and switching loads on or off.

.. code-block:: java

   PowerControlStub power = connector.getPlatformServices().getPowerControlService();
   // read bus voltages, switch loads, etc.

Clock
-----

Reads the spacecraft's on-board clock; supports commanded time updates where permitted by the mission.

.. code-block:: java

   ClockStub clock = connector.getPlatformServices().getClockService();
   Time now = clock.getTime();

No SDK example currently consumes the Clock Platform service. ``sdk/examples-space/publish-clock`` and
``sdk/examples-space/periodic-alert`` are time-related but unrelated to this service — both schedule MC
publications from the JVM clock via ``TaskScheduler``.

ArtificialIntelligence
----------------------

On-board AI inference: submit input data to a deployed model and retrieve predictions. Operations include
model selection, inference invocation, and result retrieval.

.. code-block:: java

   ArtificialIntelligenceStub ai = connector.getPlatformServices().getArtificialIntelligenceService();
   // submit input, retrieve predictions

Reference example: ``sdk/examples-space/edge-ai`` demonstrates on-board inference. The ɸ-Sat-2 mission used
this service for image classification — see :doc:`app-chaining` for the cloud-tile filtering chain.
