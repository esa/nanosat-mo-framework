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
To register an action, we have to revisit the ``initialRegistrations`` method where we also registered the parameters.

To register actions, we again need an **IdentifierList** and additionally an **ActionDefinitionDetailsList** (see the pattern here?).
The **IdentifierList** again contains the names of the actions we want to provide. The **ActionDefinitionDetailsList** contains information for each action, like a description, its category, the number of progress stages and information on expected arguments. 

.. code-block:: java
   :linenos:

   ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
   IdentifierList actionNames = new IdentifierList();

   actionDefs.add(new ActionDefinitionDetails("Uses the NMF Camera service to take a sobel filtered picture.",
       new UOctet((short) 0), new UShort(TOTAL_STAGES), new ArgumentDefinitionDetailsList()));
   actionNames.add(new Identifier(ACTION_TAKE_SOBEL));

We can then register our action by calling ``registration.registerActions(actionNames, actionDefs)``.
Note the following things:

1. The category 0 is the default value. Other ActionCategory possibilities are ActionCategory.CRITICAL and ActionCategory.HIPRIORITY.
2. The next supplied value is the number of stages that our action consists of. Our stages are: take picture, grayscaling, filtering, so TOTAL_STAGES is equal to 3.
3. If your action does not need any arguments, provide an empty **ArgumentDefinitionDetailsList**. Providing null in its place will result in an exception later on.

Now that our action is registered, we need to make sure that it does something sensible when it's called!

Handling action calls
---------------------
Whenever a user calls an action, the method ``actionArrived`` is called.
This method call provides you with an **Identifier** *name* containing the name of the action, an **AttributeValueList** containing the argument values passed to your action (will be ignored in our case), an ``actionInstanceObjId`` which is a numeric identifier for our action, a boolean *reportProgress* which tells us if we should report the progress of the execution and a **MALInteraction** which we will ignore.
The pattern of ``actionArrived`` is similar to the one in ``onGetValue`` and ``onSetValue`` of the parameter service. We check if the provided identifier matches our action and then start the execution of the action.
So the method contains the following code:

.. code-block:: java
   :emphasize-lines: 9-12
   :linenos:

   if (connector == null) {
     return new UInteger(0);
   }

   PixelResolution resolution = new PixelResolution(new UInteger(width), new UInteger(height));

   if (ACTION_TAKE_SOBEL.equals(name.getValue())) {
     try {
       connector.getPlatformServices().getCameraService()
           .takePicture(new CameraSettings(resolution, PictureFormat.BMP,
               new Duration(exposureTime), gainR, gainG, gainB),
               new DataReceivedAdapter(actionInstanceObjId));
       return null; // Success!
     } catch (MALInteractionException | MALException | IOException | NMFException ex) {
       Logger.getLogger(SobelMCAdapter.class.getName()).log(Level.SEVERE, null, ex);
     }
   }

   return new UInteger(0);

The variables *width* and *height* are additional attributes and correspond to the width and height of the BST IMS-100 camera which is used on OPS-SAT.
In this method, we use the platform services for the first time, the camera service to be precise (see highlighted line). The camera service offers a method ``takePicture`` which uses a **PixelResolution**, a **PictureFormat**, a **Duration**, three **Float** and a **CameraAdapter**. The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter` which extends the **CameraAdapter** class required for ``takePicture`` is explained further in the next section.
On success, ``actionArrived`` has to return the value ``null`` and a **UInteger** containing the error code if something goes wrong.
Now, when a user calls the action "takeSobel", our app requests the camera service to take a picture with the provided parameters and return the data over the :java:type`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter`.
Now the only thing left is to implement the logic inside the :java:type`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter` and filter the returned image!

DataReceivedAdapter
-------------------
In order to apply the sobel filter, we need to do three things: Convert the raw byte data into a **BufferedImage**, grayscale that image and apply the sobel filter on that image. This is all done in the :java:type`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter`.
The :java:type`~esa.mo.nmf.apps.SobelMCAdapter-DataReceivedAdapter` extends the abstract class **CameraAdapter** which provides methods for basic message handling between the camera service and your app.
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
