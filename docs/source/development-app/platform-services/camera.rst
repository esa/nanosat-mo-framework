======
Camera
======

.. contents:: Table of contents
   :local:

The Camera service exposes the spacecraft's imaging payload to apps.
It supports configurable resolution, pixel format, exposure time, and
RGB channel gains.

Defined in ``area105-Platform.xml`` as service number 1. Implemented by
``CameraProviderServiceImpl``; the adapter interface is
``CameraAdapterInterface``.

Capturing an image
------------------

The primary operation is ``takePicture``, which accepts a
``CameraSettings`` request and delivers the captured ``Picture`` to a
caller-supplied ``CameraAdapter``:

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

The ``MyCameraAdapter`` subclass overrides
``takePictureResponseReceived(...)`` to receive the captured image. The
image data is wrapped in the CCSDS ``Picture`` structure, exposing the
bytes via a ``Blob`` and the settings actually used during capture.

Supported pixel formats include ``BMP``, ``PNG``, and ``RAW``; the
exact set depends on the loaded adapter.

Simulated adapter
-----------------

The default simulated adapter returns one of two image sources,
controlled by ``camerasim.imagemode`` in ``platformsim.properties``:

- ``Fixed`` — always returns the image at
  ``camerasim.imagefile``.
- ``Random`` — returns a random image from the directory at
  ``camerasim.imagedirectory``.

Hardware adapter
----------------

Mission-specific hardware adapters target the actual on-board camera.
OPS-SAT uses the BST IMS-100 via a dedicated adapter; ɸ-Sat-2 uses its
multispectral imager. The hardware adapter is selected via the
``camera.adapter`` property in ``platformsim.properties``.

Reference example
-----------------

``sdk/examples-space/camera`` consumes the Camera service end to end
and is a good starting point for adaptation.
