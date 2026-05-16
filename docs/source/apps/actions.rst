=======
Actions
=======

.. contents:: Table of contents
    :local:

In addition to parameters, actions are the primary mechanism for interacting with an NMF app. An action represents any operation that the app performs in response to a ground command.
In this example, the only action required is ``takeSobel``, which acquires an image from the camera, converts it to grayscale, and applies the Sobel filter to the grayscaled image.
The action also reports its current progress to the user — that is, whether the app is currently capturing the image, grayscaling it, or applying the filter.

Registering the action
----------------------
As with parameters, actions must be registered with the NMF so that they can be discovered and invoked from a connected consumer.

Action registration is performed using the ``@Action`` annotation, which accepts the following arguments:

- ``String name`` — the name of the action. Defaults to the name of the annotated method when empty.
- ``String description`` — a description of the action. Empty by default.
- ``short category`` — the category of the action. ``0`` by default.
- ``int stepCount`` — the number of steps in the action. ``0`` by default.

The annotated method, which performs the action, must satisfy the following requirements:

- The return type must be ``UInteger``. The method must return ``null`` on success, or an integer representing an error code on failure.
- The first three arguments must be, in order: ``Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction``.

Any argument beyond the first three must be annotated with ``@ActionParameter`` and is treated as an input to the action.
The ``@ActionParameter`` annotation accepts the following arguments:

- ``String name`` — the name of the parameter. Required.
- ``String description`` — a description of the parameter. Empty by default.
- ``byte rawType`` — the raw type of the parameter. ``0`` by default.
- ``String rawUnit`` — the raw unit of the parameter. Empty by default.
- ``String conditionalConversionFieldName`` — the name of the field containing the ``ConditionalConversionList``. Empty by default.
- ``byte convertedType`` — the type of the converted parameter value. ``-1`` by default.
- ``String convertedUnit`` — the unit of the converted parameter value. Empty by default.

A typical implementation of the ``takeSobel`` action is shown below:

.. code-block:: java
   :linenos:

   @Action(description = "Uses the NMF Camera service to take a sobel filtered picture.", category = 0, stepCount = 3)
   public UInteger takeSobel(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
     try {
        PixelResolution resolution = new PixelResolution(new UInteger(width), new UInteger(height));
        connector.getPlatformServices()
                 .getCameraService()
                 .takePicture(new CameraSettings(resolution, PictureFormat.BMP, new Duration(exposureTime), gainR, gainG, gainB),
                              new DataReceivedAdapter(actionInstanceObjId));
        return null; // Success!
     } catch (MALInteractionException | MALException | IOException | NMFException ex) {
        Logger.getLogger(SobelMCAdapter.class.getName()).log(Level.SEVERE, null, ex);
     }
   }

Note the following:

1. The default category value ``0`` corresponds to a standard action. Alternative values are ``ActionCategory.CRITICAL`` and ``ActionCategory.HIPRIORITY``.
2. The ``stepCount`` value declares the number of stages the action performs. In this example: capture, grayscale, and filter.
3. An action without input arguments does not require any additional declaration; only the three required arguments need to be present.

The fields ``width`` and ``height`` are additional class attributes corresponding to the dimensions of the BST IMS-100 camera used on OPS-SAT.
This method uses a Platform service — specifically the camera service (highlighted line) — for the first time. The camera service exposes a ``takePicture`` method that accepts a **PixelResolution**, a **PictureFormat**, a **Duration**, three **Float** values, and a **CameraAdapter**. The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter`, which extends the required **CameraAdapter** class, is described in the next section.
When the ``takeSobel`` action is invoked, the app calls the camera service with the supplied parameters and receives the resulting image data through the :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter`.
The remaining work is to implement the image processing logic inside :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter`.

An example of an action that accepts parameters:

.. code-block:: java
   :linenos:

   public UInteger actionWithParameters(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
                                        @ActionParameter(name = "Parameter A") Duration parameterA,
                                        @ActionParameter(name = "Parameter B") Float parameterB) {
      // Do something with the parameters here
      return null;
   }

Handling action invocations
---------------------------
No additional handling is required for action invocations. When an action is called by a consumer, the ``actionArrived`` method of ``MonitorAndControlNMFAdapter`` is invoked and automatically dispatches to the method corresponding to the action name.

DataReceivedAdapter
-------------------
Applying the Sobel filter requires three steps: converting the raw byte data into a **BufferedImage**, grayscaling that image, and applying the Sobel filter to the grayscaled result. These steps are implemented in :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter`.
The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter` extends the abstract class **CameraAdapter**, which provides methods for handling messages exchanged between the camera service and the app.
**CameraAdapter** offers empty default implementations; only those methods that perform meaningful work need to be overridden.
In this case, only ``takePictureResponseReceived`` is required, and all other overrides may be removed.
The constants at the top of the class — originally **STAGE_ACK** and **STAGE_RSP** — should be renamed to **STAGE_IMG** and **STAGE_GS**, and a third constant should be added for the final execution stage: ``private final int STAGE_SOBEL = 3``. These constants are used later when reporting execution progress.
The ``takePictureResponseReceived`` method is invoked once the camera service has acquired an image. The image is wrapped in the CCSDS ``Picture`` structure, which exposes the image data as a **Blob** (effectively a byte array) along with the **CameraSettings** used during capture.
The first step is to retrieve the byte content from the ``picture`` and convert it into a ``BufferedImage``. This is implemented in the ``byteArrToBufferedImage`` method of the reference implementation; this method, along with other non-NMF-specific helpers, is not detailed in this guide.
The resulting ``BufferedImage`` is then grayscaled (``grayscale`` method) and passed to the Sobel operator (``sobel`` method).
Finally, the filtered image is written to disk via ``ImageIO.write(sobel, "bmp", new File(filenamePrefix + "sobel.bmp"))``. The complete ``takePictureResponseReceived`` implementation is shown below:

.. code-block:: java
   :linenos:

   final String folder = "toGround";
   File dir = new File(folder);
   dir.mkdirs();

   Date date = new Date(System.currentTimeMillis());
   Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_");
   final String timeNow = format.format(date);
   final String filenamePrefix = folder + File.separator + timeNow;

   try {
     byte[] data = picture.getContent().getValue();
     BufferedImage rgb = byteArrToBufferedImage(data);
     BufferedImage gs = grayscale(rgb);
     BufferedImage sobel = sobel(gs);
     ImageIO.write(sobel, "bmp", new File(filenamePrefix + "sobel.bmp"));
   } catch (MALException e) {
     e.printStackTrace();
   } catch (IOException e) {
     e.printStackTrace();
   }

The implementation is wrapped in a try/catch block to handle the checked exceptions thrown by the I/O and MAL calls.
Once in place, invoking the ``takeSobel`` action from a ground application (such as the CTT) captures an image, applies the filter, and writes the result to disk.

Reporting execution progress
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The remaining step is to report execution progress. Manually reported execution stages are 1-indexed (starting from stage 1) because the NMF distinguishes between *progress stages* (managed by the app) and *execution stages* (the app's progress stages plus an additional initial stage and final stage generated by the NMF).
This example has three progress stages, corresponding to five execution stages.
The app reports progress after each significant step: obtaining the **BufferedImage** from the camera service, grayscaling the image, and writing the filtered image to disk.
Progress is reported by calling ``connector.reportActionExecutionProgress(success, errorCode, currentStage, maxStages, actionID)``:

- ``success`` — boolean indicating whether the step completed successfully.
- ``errorCode`` — when ``success`` is ``false``, identifies the error that occurred.
- ``currentStage`` — the stage being reported as completed.
- ``maxStages`` — the total number of stages reported by the app (must match the value declared during action registration).
- ``actionID`` — the object instance ID of the action, used to associate the progress report with the action in the event service.

The complete implementation of ``takePictureResponseReceived`` is therefore:

.. code-block:: java
   :linenos:

   final String folder = "toGround";
   File dir = new File(folder);
   dir.mkdirs();

   Date date = new Date(System.currentTimeMillis());
   Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_");
   final String timeNow = format.format(date);
   final String filenamePrefix = folder + File.separator + timeNow;

   try {
     byte[] data = picture.getContent().getValue();
     BufferedImage rgb = byteArrToBufferedImage(data);
     connector.reportActionExecutionProgress(true, 0, STAGE_IMG, TOTAL_STAGES,
         actionInstanceObjId);
     BufferedImage gs = grayscale(rgb);
     connector.reportActionExecutionProgress(true, 0, STAGE_GS, TOTAL_STAGES,
         actionInstanceObjId);
     BufferedImage sobel = sobel(gs);
     ImageIO.write(sobel, "bmp", new File(filenamePrefix + "sobel.bmp"));
     connector.reportActionExecutionProgress(true, 0, STAGE_SOBEL, TOTAL_STAGES,
         actionInstanceObjId);
   } catch (MALException e) {
     e.printStackTrace();
   } catch (IOException e) {
     e.printStackTrace();
   } catch (NMFException e) {
     e.printStackTrace();
   }

The catch blocks shown above are auto-generated stubs and should be replaced with proper logging calls to aid in diagnosing failures.
With the app now fully implemented, proceed to :doc:`packaging` to learn how to package it for deployment.
