==========
Parameters
==========

.. contents:: Table of contents
    :local:

Parameters can be used to capture or influence the current state of the spacecraft or your app.
In the case of our example app, the parameters will be the gains for the RGB colour channels and the exposure time of the camera.

Defining parameters
-------------------
Internally, parameters are most likely just Java attributes of your app or provided by some other service. So to define our available parameters, we just need to define them in our class:

.. code-block:: java
   :linenos:

   private float gainR = 4.0f;
   private float gainG = 4.0f;
   private float gainB = 4.0f;
   private float exposureTime = 0.2f;

It is also recommended that you define constant strings containing the names of your parameters, so they can be used later.

.. code-block:: java
   :linenos:

   private static final String GR = "GainRed";
   private static final String GG = "GainGreen";
   private static final String GB = "GainBlue";
   private static final String ET = "ExposureTime";

Registering parameters
----------------------
We then need to register those parameters, so they are known to the NMF's parameter service. This is done in the method ``initialRegistrations`` which is provided by :java:type:`~esa.mo.nmf.MonitorAndControlNMFAdapter`.
The first thing you should do in ``initialRegistrations`` is setting the registration mode of the passed in :java:type:`~esa.mo.nmf.MCRegistration`.
The default value is **DONT_UPDATE_IF_EXISTS**, but it could change in between, so it makes sense to set it explicitly to that value. The other option would be **UPDATE_IF_EXISTS**.
So, all you need to do for now is ``registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);``

Now, let's actually register our parameters. To define our parameters, we need two things: a **ParameterDefinitionDetailsList** and an **IdentifierList**.
The **ParameterDefinitionDetailsList** contains all the details of our parameters, except for the name. 
The parameter names are provided in the **IdentifierList** in the same order, as the corresponding **ParameterDefinitionDetails** are passed to the other list. 
So, if we supplied the details in the order gainR, gainG, gainB, exposureTime, then the **IdentifierList** would contain the **Identifiers** for "GainRed", "GainGreen", "GainBlue" and "ExposureTime" in that order.
**ParameterDefinitionDetails** contain basic information about your parameter. This information is a short description (**String**) which can be displayed in the CTT, its raw type (**Byte**), if the parameter value is sent to the NMF on a regular basis (**Boolean**), the delay between parameter updates (**Duration**), an expression to check if the current parameter value is valid (**ParameterExpression**) and finally a **ParameterConversion** if the parameter has a converted type.
The last two values can be null, if they are not needed.
However, the other values should be set. Otherwise, NullPointerExceptions will occur.

To create the ParameterDefinitionDetails for a parameter, we just have to create instances of the **ParameterDefinitionDetails** class. So, let's do that!

.. code-block:: java
   :linenos:

   ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
   IdentifierList paramNames = new IdentifierList();

   ParameterDefinitionDetails detailsGainR = new ParameterDefinitionDetails(
       "The red channel gain", Union.FLOAT_TYPE_SHORT_FORM.byteValue(), "", false, new Duration(0), null,
       null);
   ParameterDefinitionDetails detailsGainG = new ParameterDefinitionDetails(
       "The green channel gain", Union.FLOAT_TYPE_SHORT_FORM.byteValue(), "", false, new Duration(0), null,
       null);
   ParameterDefinitionDetails detailsGainB = new ParameterDefinitionDetails(
       "The blue channel gain", Union.FLOAT_TYPE_SHORT_FORM.byteValue(), "", false, new Duration(0), null,
       null);
   ParameterDefinitionDetails detailsExpTime = new ParameterDefinitionDetails(
       "The camera's exposure time", Union.FLOAT_TYPE_SHORT_FORM.byteValue(), "", false, new Duration(0),
       null, null);

And add them to the **ParameterDefinitionDetailsList** and set the **Identifiers**:

.. code-block:: java
   :linenos:

   defs.addAll(Arrays.asList(new ParameterDefinitionDetails[] { detailsGainR, detailsGainG,
        detailsGainB, detailsExpTime }));
   paramNames.add(new Identifier(GR));
   paramNames.add(new Identifier(GG));
   paramNames.add(new Identifier(GB));
   paramNames.add(new Identifier(ET));

All that is left to do for the parameter registration is to call ``registration.registerParameters(paramNames, defs)``.

Getting the value of a parameter
--------------------------------
Without ground access to your parameters, they are most likely useless. To make your parameter values accessible from the ground you need to implement the method ``onGetValue`` which provides you with and **Identifier** and a rawType as a **Byte**.
In ``onGetValue`` we basically need to check, if our app knows the provided identifier and return the corresponding value. So our code looks like this:

.. code-block:: java
   :linenos:

   if (connector == null) {
     return null;
   }

   if (identifier.getValue().equals(GR)) {
     return new Union(gainR);
   } else if (identifier.getValue().equals(GG)) {
     return new Union(gainG);
   } else if (identifier.getValue().equals(GB)){
     return new Union(gainB);
   } else if (identifier.getValue().equals(ET)) {
     return new Union(exposureTime);
   }
   return null;

Note that **Union** is a MAL wrapper for Java primitive types and extends the **Attribute** class.

Setting the value of a parameter
--------------------------------
Right now, our parameters are read-only, as nothing will happen when we call setParameter from the ground.
To change that, we need to implement the method ``onSetValue``. The method is provided with an **IdentifierList** and a **ParameterRawValueList**.
The idea is to iterate over the **IdentifierList** and assign the corresponding value of the **ParameterRawValueList** to the correct parameter.
This can be done by using a similar if/else if construction as in ``onGetValue``, or by storing your parameters in a HashMap that you declare in your adapter.
In this example, we will use the first approach.

.. code-block:: java
   :linenos:

   boolean result = false;
   for (int i = 0; i < identifiers.size(); i++) {
     if (identifiers.get(i).getValue().equals(GR)) {
       gainR = (float) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
       result = true;
     } else if (identifiers.get(i).getValue().equals(GG)) {
       gainG = (float) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
       result = true;
     } else if (identifiers.get(i).getValue().equals(GB)) {
       gainB = (float) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
       result = true;
     } else if (identifiers.get(i).getValue().equals(ET)) {
       exposureTime = (float) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
       result = true;
     }
   }

   return result; // to confirm if the variable was set

Summary
-------
We are now able to use parameters in our app! Here is just a quick recap of what you need to do in order to use parameters:

1. Declare some variables that hold your parameters values and provide a default value.
2. Register your parameters in ``initialRegistrations``.
3. Implement ``onGetValue``.
4. Implement ``onSetValue``.

We only covered the basics of parameter handling. There is even more stuff that you can do with them (e.g. updating parameter values on a regular basis)!
If you want to learn about this, check out the `Publish Clock Example <https://github.com/esa/nanosat-mo-framework/blob/dev/sdk/examples/space/publish-clock/src/main/java/esa/mo/nmf/apps/PushClock.java>`_ on GitHub.

Now that our parameters are ready to go, it is time to implement some :doc:`actions`.
