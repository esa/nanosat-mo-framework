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

ArtificialIntelligence
----------------------

On-board AI inference: submit input data to a deployed model and retrieve predictions. Operations include
model selection, inference invocation, and result retrieval.

.. code-block:: java

   ArtificialIntelligenceStub ai = connector.getPlatformServices().getArtificialIntelligenceService();
   // submit input, retrieve predictions

Reference example: ``sdk/examples-space/edge-ai`` demonstrates on-board inference. The ɸ-Sat-2 mission used
this service for image classification — see :doc:`app-chaining` for the cloud-tile filtering chain.

FPGA
----

Loads and unloads gateware modules (partial bitstreams) into the reconfigurable partitions of the platform
FPGA at runtime, without disturbing the static shell design. The bitstreams are compiled on ground against
the mission's shell and delivered inside the app's NMF Package together with a module manifest that declares
the shell version and one bitstream variant per partition. The service selects a free partition, verifies
the checksum and the shell compatibility, and returns the allocated partition — including its
``dataPlaneRef`` (for example a Linux UIO device path), which the app then uses for direct data-plane access
to its module. MO carries only the control plane.

.. code-block:: java

   FPGAStub fpga = connector.getPlatformServices().getFPGAService();
   fpga.loadModule(new Identifier("fft"), null, new FPGAAdapter() {   // NULL: the service picks the slot
       @Override
       public void loadModuleResponseReceived(MALMessageHeader msgHeader,
               FPGAPartition partition, Map qosProperties) {
           // ... open partition.getDataPlaneRef(), run the accelerator ...
       }
   });
   // and in the app's stop path:
   fpga.unloadModule(partitionId);

The module manifest is a sidecar properties file next to the bitstreams:

.. code-block:: properties

   module.name        = fft
   module.shell       = v3
   module.slot-a.file = module_fft_a.bin
   module.slot-a.crc  = 0x8F21C3D0
   module.slot-b.file = module_fft_b.bin
   module.slot-b.crc  = 0x11A047E9

Every load and unload is recorded in the COM Archive (``FPGAModuleLoaded`` / ``FPGAModuleUnloaded`` objects), and
partition state transitions are published through the ``monitorPartitions`` PUB-SUB operation.

No SDK example consumes this service yet. The simulated adapter provides two partitions (``slot-a``,
``slot-b``) with shell version ``sim-v1``.

SoftwareImages
--------------

Starts, stops and restarts software images (bare-metal binaries or guest operating systems) in the
partitions of the platform hypervisor, without disturbing the other partitions. Images are compiled on
ground against the mission's hypervisor configuration and delivered inside an NMF Package together with an
image manifest that declares the configuration version and one image variant per partition. The service
selects a free partition, verifies the checksum and the configuration compatibility, and returns the
allocated partition.

.. code-block:: java

   SoftwareImagesStub images = connector.getPlatformServices().getSoftwareImagesService();
   images.startImage(new Identifier("payload-os"), null, new SoftwareImagesAdapter() {
       @Override
       public void startImageResponseReceived(MALMessageHeader msgHeader,
               SoftwareImagePartition partition, Map qosProperties) {
           // the image is running in partition.getPartitionId()
       }
   });
   // and later:
   images.stopImage(partitionId);       // halt and clear the partition
   images.restartImage(partitionId);    // warm reset without reloading

The image manifest is a sidecar properties file next to the image files:

.. code-block:: properties

   image.name    = payload-os
   image.config  = xmcf-v2
   image.p1.file = payload_os_p1.img
   image.p1.crc  = 0x5A1EC3D0
   image.p2.file = payload_os_p2.img
   image.p2.crc  = 0x7700A4E9

Every start and stop is recorded in the COM Archive (``SoftwareImageStarted`` / ``SoftwareImageStopped``
objects). The reference hypervisor target is XtratuM, integrated through a mission-provided adapter; the
simulated adapter provides two partitions (``p1``, ``p2``) with configuration version ``sim-v1``.

No SDK example consumes this service yet.
