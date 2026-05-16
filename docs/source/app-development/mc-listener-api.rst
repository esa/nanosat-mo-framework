==========================
M&C Listener-interface API
==========================

.. contents:: Table of contents
   :local:

The listener-interface API exposes parameters and actions through
explicit method calls on the ``SimpleMonitorAndControlAdapter``
superclass. No reflection is involved at runtime.

Adapter skeleton
----------------

.. code-block:: java

   import esa.mo.nmf.MCRegistration;
   import esa.mo.nmf.MCRegistration.RegistrationMode;
   import esa.mo.nmf.SimpleMonitorAndControlAdapter;
   import org.ccsds.moims.mo.mc.structures.*;

   public class MyAppAdapter extends SimpleMonitorAndControlAdapter {

       @Override
       public void initialRegistrations(MCRegistration r) {
           r.setMode(RegistrationMode.DONT_UPDATE_IF_EXISTS);
           // register parameters
           // register actions
       }

       @Override
       public Serializable onGetValueSimple(String name) { ... }

       @Override
       public boolean onSetValueSimple(String name, Serializable value) { ... }

       @Override
       public boolean actionArrivedSimple(String name, Serializable[] values, Long id) { ... }
   }

Registering parameters
----------------------

Parameters are registered during ``initialRegistrations`` by building a
``ParameterDefinitionList`` and passing it to
``r.registerParameters(defs)``:

.. code-block:: java

   ParameterDefinitionList defs = new ParameterDefinitionList();
   defs.add(new ParameterDefinition(
           new Identifier("temperatureC"),
           "Sensor temperature in degrees Celsius",
           AttributeType.FLOAT,
           "degC",
           false,                  // generationEnabled
           new Duration(3),        // reportInterval
           null,                   // validityExpression
           null                    // conversion
   ));
   r.registerParameters(defs);

A parameter is identified by its name (the ``Identifier``); the same
name is used when handling reads and writes.

Reading parameter values
^^^^^^^^^^^^^^^^^^^^^^^^

When a consumer queries a parameter, the framework calls
``onGetValueSimple(String name)``. The adapter returns the current
value:

.. code-block:: java

   private float temperatureC = 0.0f;

   @Override
   public Serializable onGetValueSimple(String name) {
       switch (name) {
           case "temperatureC": return temperatureC;
           default: return null;
       }
   }

Writing parameter values
^^^^^^^^^^^^^^^^^^^^^^^^

When a consumer writes a parameter, ``onSetValueSimple(String name,
Serializable value)`` is called. Return ``true`` to confirm the
assignment, ``false`` to reject:

.. code-block:: java

   @Override
   public boolean onSetValueSimple(String name, Serializable value) {
       if ("temperatureC".equals(name)) {
           temperatureC = ((Number) value).floatValue();
           return true;
       }
       return false;
   }

Registering actions
-------------------

Actions are registered similarly:

.. code-block:: java

   ActionDefinitionList actions = new ActionDefinitionList();
   ArgumentDefinitionList args = new ArgumentDefinitionList();
   args.add(new ArgumentDefinition(new Identifier("setpoint"), null,
            AttributeType.DOUBLE, "-"));
   actions.add(new ActionDefinition(
           new Identifier("reset"),
           "Reset the sensor",
           ActionCategory.DEFAULT,
           new UShort(0),          // step count (0 = single-shot)
           args
   ));
   r.registerActions(actions);

Action categories are: ``DEFAULT``, ``CRITICAL``, ``HIPRIORITY``.

Handling action invocations
^^^^^^^^^^^^^^^^^^^^^^^^^^^

When a consumer invokes an action, ``actionArrivedSimple(String name,
Serializable[] values, Long executionId)`` is called. Return ``true``
on success, ``false`` on failure:

.. code-block:: java

   @Override
   public boolean actionArrivedSimple(String name, Serializable[] values, Long executionId) {
       if ("reset".equals(name)) {
           double setpoint = ((Number) values[0]).doubleValue();
           applyReset(setpoint);
           return true;
       }
       return false;
   }

Multi-stage actions
^^^^^^^^^^^^^^^^^^^

For actions that take more than a moment, declare a non-zero step count
when registering, then report progress from the action handler:

.. code-block:: java

   connector.reportActionExecutionProgress(true, 0, currentStage, totalStages, executionId);

The Supervisor forwards each progress update to subscribed consumers
via the COM Event service.

Reference example
-----------------

The ``sdk/examples-space/hello-world-simple`` example uses this API end
to end and is a good starting point for adaptation.
