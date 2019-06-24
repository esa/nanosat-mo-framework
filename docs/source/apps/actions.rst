=======
Actions
=======
Apart from parameters, actions are our main way to interact with an NMF app. An action can be anything that does something inside your app.
In our case, the only action we need is called **takeSobel** and will take care of taking an image with the camera, grayscaling it and applying the sobel filter on top of the grayscaled image.
We also want to keep the user informed about the current progress of the action, i.e. if we are currently taking a picture, grayscaling it or applying the filter.

Registering the action
----------------------
Just like parameters, actions need to be registered to the NMF. So, when we connect to the app it can tell us which actions it provides.
To register an action, we have to revisit the **initialRegistrations** method where we also registered the parameters.

To register actions, we again need an **IdentifierList** and additionally an **ActionDefinitionDetailsList** (see the pattern here?).
The **IdentifierList** again contains the names of the actions we want to provide. The **ActionDefinitionDetailsList** contains information for each action, like a description, its category, the number of progress stages and information on expected arguments. 

.. code-block:: java

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
Whenever a user calls an action, the method **actionArrived** is called. This method call provides you with an **Identifier** *name* containing the name of the action, an **AttributeValueList** containing the argument values passed to your action (will be ignored in our case), an **actionInstanceObjId** which is a numeric identifier for our action, a boolean *reportProgress* which tells us if we should report the progress of the execution and a **MALInteraction** which we will ignore.
The pattern of **actionArrived** is similar to the one in **onGetValue** and **onSetValue** of the parameter service. We check if the provided identifier matches our action and then start the execution of the action.
So the method contains the following code:

.. code-block:: java
   :emphasize-lines: 9

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
In this method, we use the platform services for the first time, the camera service to be precise (see highlighted line). The camera service offers a method **takePicture** which uses a **PixelResolution**, a **PictureFormat**, a **Duration**, three **Float** and a **CameraAdapter**. The **DataReceivedAdapter** which extends the **CameraAdapter** class required for **takePicture** is explained further in the next section.
On success, **actionArrived** has to return the value **null** and a **UInteger** containing the error code if something goes wrong.
Now, when a user calls the action "takeSobel", our app requests the camera service to take a picture with the provided parameters and return the data over the **DataReceivedAdapter**.
Now the only thing left is to implement the logic inside the **DataReceivedAdapter** and filter the returned image!

DataReceivedAdapter
-------------------
In order to apply the sobel filter, we need to do three things: Convert the raw byte data into a **BufferedImage**, grayscale that image and apply the sobel filter on that image. This is all done in the **DataReceivedAdapter**.
