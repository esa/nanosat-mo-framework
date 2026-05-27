===============================
Worked example: Sobel image app
===============================

.. contents:: Table of contents
   :local:

This complementary worked example consumes a Platform service (Camera), applies image processing in Java, and
reports progress across multiple stages of a multi-stage action. It is more involved than the Hello World
example in :doc:`worked-example`.

The app
-------

The Sobel app:

1. Receives a ``takeSobel`` action invocation from the ground.
2. Requests an image from the Platform Camera service.
3. Converts the captured image to grayscale.
4. Applies the Sobel edge-detection operator.
5. Writes the result to disk and reports progress at each stage.

Parameters: gains for the RGB camera channels and the exposure time. Action: ``takeSobel`` with three progress
stages — ``STAGE_IMG`` (image captured), ``STAGE_GS`` (grayscaled), ``STAGE_SOBEL`` (filtered and written).

Consuming the Camera service
----------------------------

Inside the action handler, the Camera service is reached through the Connector:

.. code-block:: java

   PixelResolution resolution = new PixelResolution(
           new UInteger(width), new UInteger(height));

   connector.getPlatformServices()
            .getCameraService()
            .takePicture(
                new CameraSettings(resolution, PictureFormat.BMP,
                                   new Duration(exposureTime),
                                   gainR, gainG, gainB),
                new DataReceivedAdapter(actionInstanceObjId));

The ``CameraSettings`` carry the requested resolution, format, exposure, and channel gains. The
``DataReceivedAdapter`` is a subclass of the platform ``CameraAdapter`` that overrides
``takePictureResponseReceived(...)`` to receive the captured image.

Reporting progress
------------------

After each stage of the pipeline, the action handler reports back:

.. code-block:: java

   connector.reportActionExecutionProgress(
           true,                  // success
           0,                     // errorCode
           STAGE_IMG,             // currentStage
           TOTAL_STAGES,          // totalStages declared at registration
           actionInstanceObjId);

The Supervisor publishes each update via the COM Event service so the invoking consumer sees the stages in
real time.

Full source
-----------

The complete implementation lives under ``sdk/examples-space/picture-processor`` (image processing) and
``sdk/examples-space/camera`` (camera consumption). The historical documentation of this example as a
multi-page tutorial is preserved for now under ``apps/``.
