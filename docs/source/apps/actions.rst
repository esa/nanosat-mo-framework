=======
Actions
=======

.. contents:: Table of contents
    :local:

Apart from parameters, actions are our main way to interact with an NMF app. An action can be anything that does something inside your app.
In our case, the only action we need is called ``takeSobel`` and will take care of taking an image with the camera, grayscaling it and applying the sobel filter on top of the grayscaled image.
We also want to keep the user informed about the current progress of the action, i.e. if we are currently taking a picture, grayscaling it or applying the filter.

Registering the action
----------------------
Just like parameters, actions need to be registered to the NMF. So, when we connect to the app it can tell us which actions it provides.

To register actions we're going to be using the ``@Action`` annotation.
This annotation has the following parameters:

- ``String name`` - The name of the action. If empty the name of the function will be used. Empty by default.
- ``String description`` - The description of the action. Empty by default.
- ``short category`` - The category of the action. 0 by default.
- ``int stepCount`` - Number of steps in the action. 0 by default.

To register the action we need a function that will be performing the action's tasks. This function needs to meet some requirements:

- The return type must be ``UInteger`` and the return value must be ``null`` if the action succeeded or an integer number representing an error code.
- The first three arguments must be: ``Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction`` (in that order)

Every argument after the first three must be annotated with ``@ActionParameter`` and will be treated as input to the action.
The ``@ActionParameter`` annotation has the following parameters:

- ``String name`` - The name of the parameter. This argument is required.
- ``String description`` - The description of the parameter. Empty by default.
- ``byte rawType`` - The raw type of the parameter. 0 by default.
- ``String rawUnit`` - The raw unit of the parameter. Empty by default.
- ``String conditionalConversionFieldName`` - Name of the field containing the ``ConditionalConversionList``. Empty by default.
- ``byte convertedType`` - Type of the converted parameter value. -1 by default.
- ``String convertedUnit`` - Unit of the converted parameter value. Empty by default.

The function for the ``takeSobel`` action could look like this:

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

Note the following things:

1. The category 0 is the default value. Other ActionCategory possibilities are ActionCategory.CRITICAL and ActionCategory.HIPRIORITY.
2. The next supplied value is the number of stages that our action consists of. Our stages are: take picture, grayscaling, filtering.
3. If your action does not need any arguments, there is no need to specify that. It is only important to have the three required arguments.

The variables *width* and *height* are additional attributes and correspond to the width and height of the BST IMS-100 camera which is used on OPS-SAT.
In this method, we use the platform services for the first time, the camera service to be precise (see highlighted line). The camera service offers a method ``takePicture`` which uses a **PixelResolution**, a **PictureFormat**, a **Duration**, three **Float** and a **CameraAdapter**. The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter` which extends the **CameraAdapter** class required for ``takePicture`` is explained further in the next section.
Now, when a user calls the action "takeSobel", our app requests the camera service to take a picture with the provided parameters and return the data over the :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter`.
Now the only thing left is to implement the logic inside the :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter` and filter the returned image!

Example action with parameters could look like this:

.. code-block:: java
   :linenos:

   public UInteger actionWithParameters(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
                                        @ActionParameter(name = "Parameter A") Duration parameterA,
                                        @ActionParameter(name = "Parameter B") Float parameterB) {
      // Do something with the parameters here
      return null;
   }

Handling action calls
---------------------
There is no need for any special handling of action calls. Whenever a user calls an action, the method ``actionArrived`` from ``MonitorAndControlNMFAdapter`` will be called and it will automatically dispatch to your function corresponding with the action name.

DataReceivedAdapter
-------------------
In order to apply the sobel filter, we need to do three things: Convert the raw byte data into a **BufferedImage**, grayscale that image and apply the sobel filter on that image. This is all done in the :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter`.
The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter` extends the abstract class **CameraAdapter** which provides methods for basic message handling between the camera service and your app.
The **CameraAdapter** class offers several (empty) default implementations, so you can just override the ones in which you actually want to do something meaningful.
So, in our case, we only want to implement the method ``takePictureResponseReceived``. Therefore, we can get rid of every other overridden method.
We also want to change the names of the constant integers at the beginning of the class from **STAGE_ACK** and **STAGE_RSP** to **STAGE_IMG** and **STAGE_GS**. Further, we want to add a third constant for the last execution stage: ``private final int STAGE_SOBEL = 3``.
We'll come back to them, later.
Now, let's talk about ``takePictureResponseReceived``. This method is invoked when the camera service acquired an image for us. This image is wrapped into the CCSDS Picture structure which offers us the image data as a **Blob** (essentially a byte array) and the **CameraSettings** which were used to take the picture.
What we need to do is to get the content of the *picture*, get its bytes and convert them into a BufferedImage. This is done in the method ``byteArrToBufferedImage`` in the reference implementation.
We won't cover this method (and other non-NMF related methods) in this tutorial. After that, we take the **BufferedImage** and grayscale it (method ``grayscale``) and take the grayscaled image and apply the sobel operator on it (method ``sobel``).
In the end, we use ``ImageIO.write(sobel, "bmp", new File(filenamePrefix + "sobel.bmp"))`` to write the image to disk. The code for the method ``takePictureResponseReceived`` looks like this:

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

We have to catch some exceptions in between, so everything is surrounded by a try/catch-construction.
Now when we call the action ``takeSobel`` from our ground application (e.g. the CTT), a picture is taken, filtered and the result is stored on disk.

Reporting execution progress
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The only thing missing from our implementation now is to report our execution progress. Manually reported execution stages are 1-indexed (we start with stage 1) because the NMF distinguishes *progress stages* (handled by your app) and *execution stages* (your apps progress stages + an additional initial stage and final stage generated by the NMF).
So, in this example we have three progress stages and, therefore, five execution stages.
We want to report that we obtained a **BufferedImage** from the camera service, grayscaled the image and that we finished writing the image to a file.
To achieve that, we simply have to call ``connector.reportActionExecutionProgress(success, errorCode, currentStage, maxStages, actionID)`` after each method call. ``success`` is a boolean, describing if everything worked fine.
If ``success`` is false, the parameter ``errorCode`` represents the occurring problem. ``currentStage`` is the stage that we want to report as finished and ``maxStages`` is the total number of stages that will be reported by our app (the same number we used when registering the action).
The last parameter is the object instance ID of the action which is used to map the progress to the action in the event service.
Therefore, our finished code for ``takePictureReceived`` looks as follows:

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

Note that the catch blocks are auto-generated and should contain logging calls so you can trace down problems in your app.
Now that your first app is implemented, it is time to learn about :doc:`packaging`.
