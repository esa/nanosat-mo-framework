=====================================
Adding Monitor & Control capabilities
=====================================

.. contents:: Table of contents
   :local:

Monitor & Control (M&C) integration is the mechanism by which an app exposes its state and behaviour:
telemetry parameters that ground operators can read and subscribe to, actions they can invoke, alerts the app
can raise, and aggregations that group related parameters.

The NMF provides two equivalent APIs for M&C integration. Pick the one that matches your style:

- `Listener-interface API`_ — extend ``SimpleMonitorAndControlAdapter`` and override explicit methods for each
  operation. No reflection at runtime; all dispatch is explicit. Closer to the underlying MO service
  interface.
- `Annotation API`_ — extend ``MonitorAndControlNMFAdapter`` and annotate fields and methods with
  ``@Parameter``, ``@Action``, and ``@ActionParameter``. More compact for apps with many parameters and
  actions; relies on reflection at registration time to discover annotated members.

Both APIs ultimately register the same MO objects with the Supervisor's Directory Service, and the resulting
app is indistinguishable from the consumer side. The :doc:`worked-example` page implements the same app twice
— once with each API — so you can compare them side by side.

The MC services
---------------

The MC service category provides five services. An app's adapter exposes whichever of these are relevant to
its behaviour:

- **Parameter** — read, set, and subscribe to telemetry values.
- **Action** — invoke commands, optionally with multi-stage progress reporting.
- **Aggregation** — group parameters into reports published as a unit.
- **Alert** — publish operational alerts.
- **Conversion** — declarative conversions between raw and engineering parameter values.

See :doc:`../concepts/mo-architecture` for where the MC services sit in the broader CCSDS MO stack.

Listener-interface API
----------------------

The listener-interface API exposes parameters and actions through explicit method calls on the
``SimpleMonitorAndControlAdapter`` superclass. No reflection is involved at runtime.

Adapter skeleton
^^^^^^^^^^^^^^^^

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
^^^^^^^^^^^^^^^^^^^^^^

Parameters are registered during ``initialRegistrations`` by building a ``ParameterDefinitionList`` and
passing it to ``r.registerParameters(defs)``:

.. code-block:: java

   ParameterDefinitionList defs = new ParameterDefinitionList();
   defs.add(new ParameterDefinition(
           new Identifier("temperatureC"),
           "Sensor temperature in degrees Celsius",
           AttributeType.FLOAT,
           "degC",
           false,                  // reportingEnabled
           new Duration(3),        // reportInterval
           null,                   // validityExpression
           null                    // conversion
   ));
   r.registerParameters(defs);

A parameter is identified by its name (the ``Identifier``); the same name is used when handling reads and
writes.

Reading parameter values
~~~~~~~~~~~~~~~~~~~~~~~~

When a consumer queries a parameter, the framework calls ``onGetValueSimple(String name)``. The adapter
returns the current value:

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
~~~~~~~~~~~~~~~~~~~~~~~~

When a consumer writes a parameter, ``onSetValueSimple(String name, Serializable value)`` is called. Return
``true`` to confirm the assignment, ``false`` to reject:

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
^^^^^^^^^^^^^^^^^^^

Actions are registered similarly:

.. code-block:: java

   ActionDefinitionList actions = new ActionDefinitionList();
   ArgumentDefinitionList args = new ArgumentDefinitionList();
   args.add(new ArgumentDefinition(new Identifier("setpoint"), null, AttributeType.DOUBLE, "-"));
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
~~~~~~~~~~~~~~~~~~~~~~~~~~~

When a consumer invokes an action, ``actionArrivedSimple(String name, Serializable[] values, Long
executionId)`` is called. Return ``true`` on success, ``false`` on failure:

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
~~~~~~~~~~~~~~~~~~~

For actions that take more than a moment, declare a non-zero step count when registering, then report progress
from the action handler:

.. code-block:: java

   connector.reportActionExecutionProgress(true, 0, currentStage, totalStages, executionId);

The Supervisor forwards each progress update to subscribed consumers via the COM Event service.

Reference example
^^^^^^^^^^^^^^^^^

The ``sdk/examples-space/hello-world-simple`` example uses this API end to end and is a good starting point
for adaptation.

Annotation API
--------------

The annotation API exposes parameters and actions by annotating fields and methods on a subclass of
``MonitorAndControlNMFAdapter``. At registration time, the framework uses reflection to discover annotated
members and registers the corresponding MO objects with the Supervisor.

The annotation classes live in ``esa.mo.nmf.annotations``:

- ``@Parameter`` — declares a parameter from a field.
- ``@Action`` — declares an action from a method.
- ``@ActionParameter`` — declares an argument of an action.
- ``@Aggregation`` — declares a parameter aggregation.

Adapter skeleton
^^^^^^^^^^^^^^^^

.. code-block:: java

   import esa.mo.nmf.MonitorAndControlNMFAdapter;
   import esa.mo.nmf.annotations.Action;
   import esa.mo.nmf.annotations.ActionParameter;
   import esa.mo.nmf.annotations.Parameter;
   import org.ccsds.moims.mo.mal.MALInteraction;
   import org.ccsds.moims.mo.mal.structures.UInteger;

   public class MyAppAdapter extends MonitorAndControlNMFAdapter {
       // fields with @Parameter
       // methods with @Action
   }

``@Parameter``
^^^^^^^^^^^^^^

Apply ``@Parameter`` to a field whose value is the parameter's value. The annotation accepts:

- ``name`` — the parameter name. Defaults to the field name when empty.
- ``description`` — text shown to the consumer.
- ``rawUnit`` — unit string (e.g. ``"degC"``, ``"rad/s"``).
- ``reportingEnabled`` — whether automatic report generation is on at startup.
- ``reportIntervalSeconds`` — interval for periodic reporting; ``0`` disables periodic generation.
- ``readOnly`` — rejects writes if ``true``. Always ``true`` for ``final`` fields.
- ``onGetFunction`` — name of a no-argument method called immediately before the value is read, used to
  refresh the field. The method must be ``public``.

Example:

.. code-block:: java

   @Parameter(description = "Sensor temperature",
              rawUnit = "degC",
              reportIntervalSeconds = 3,
              onGetFunction = "refreshTemperature")
   private float temperatureC = 0.0f;

   public void refreshTemperature() {
       temperatureC = sensor.readCelsius();
   }

Writes are dispatched automatically to the annotated field unless ``onSetValue`` is overridden in the adapter.

``@Action``
^^^^^^^^^^^

Apply ``@Action`` to a method. The annotation accepts:

- ``name`` — the action name. Defaults to the method name when empty.
- ``description`` — text shown to the consumer.
- ``stepCount`` — number of progress stages reported by the action. ``0`` for single-shot actions.
- ``rawUnit`` — unit string for the action's raw value, if applicable.

The method signature must be:

.. code-block:: java

   public UInteger <name>(
       Long actionInstanceObjId,
       boolean reportProgress,
       MALInteraction interaction,
       <optional @ActionParameter arguments>);

Return ``null`` on success or a ``UInteger`` error code on failure.

``@ActionParameter``
~~~~~~~~~~~~~~~~~~~~

Each argument after the three required ones must be annotated with ``@ActionParameter``:

- ``name`` (required) — the parameter's display name.
- ``description`` — text shown to the consumer.
- ``rawType`` — MAL attribute type ordinal (defaults to ``AttributeType.STRING_VALUE``).
- ``rawUnit`` — unit of the raw value.

Example:

.. code-block:: java

   @Action(description = "Reset the sensor", stepCount = 0)
   public UInteger reset(
           Long actionInstanceObjId,
           boolean reportProgress,
           MALInteraction interaction,
           @ActionParameter(name = "setpoint") Double setpoint) {
       applyReset(setpoint);
       return null;  // success
   }

Multi-stage actions
~~~~~~~~~~~~~~~~~~~

For an action with non-zero ``stepCount``, call ``connector.reportActionExecutionProgress(...)`` after each
stage; see the `Listener-interface API`_ section above for the signature.

Caveats
^^^^^^^

- **Reflection at registration.** The framework scans the adapter class at registration time. Field and method
  names referenced in ``onGetFunction``, ``validityExpressionFieldName``, ``conversionFunctionName``, and
  similar attributes are resolved by name and not checked at compile time; mistyped names surface only at
  runtime.
- **No method-level call paths.** All dispatch goes through the annotated members, so static analysis tools
  that look for unused methods may flag annotated handlers as unused.
- **Equivalent to the listener API.** The set of MO objects ultimately registered is the same. Choose the API
  that matches your style and the project's coding conventions.

Reference example
^^^^^^^^^^^^^^^^^

The ``sdk/examples-space/all-mc-services`` and ``sdk/examples-space/camera-acquisitor-system`` examples use
this API end to end.
