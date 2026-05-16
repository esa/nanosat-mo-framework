==================
M&C Annotation API
==================

.. contents:: Table of contents
   :local:

The annotation API exposes parameters and actions by annotating fields
and methods on a subclass of ``MonitorAndControlNMFAdapter``. At
registration time, the framework uses reflection to discover annotated
members and registers the corresponding MO objects with the Supervisor.

The annotation classes live in ``esa.mo.nmf.annotations``:

- ``@Parameter`` — declares a parameter from a field.
- ``@Action`` — declares an action from a method.
- ``@ActionParameter`` — declares an argument of an action.
- ``@Aggregation`` — declares a parameter aggregation.

Adapter skeleton
----------------

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
--------------

Apply ``@Parameter`` to a field whose value is the parameter's value.
The annotation accepts:

- ``name`` — the parameter name. Defaults to the field name when empty.
- ``description`` — text shown to the consumer.
- ``rawUnit`` — unit string (e.g. ``"degC"``, ``"rad/s"``).
- ``generationEnabled`` — whether automatic generation is on at startup.
- ``reportIntervalSeconds`` — interval for periodic reporting; ``0``
  disables periodic generation.
- ``readOnly`` — rejects writes if ``true``. Always ``true`` for
  ``final`` fields.
- ``onGetFunction`` — name of a no-argument method called immediately
  before the value is read, used to refresh the field. The method must
  be ``public``.

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

Writes are dispatched automatically to the annotated field unless
``onSetValue`` is overridden in the adapter.

``@Action``
-----------

Apply ``@Action`` to a method. The annotation accepts:

- ``name`` — the action name. Defaults to the method name when empty.
- ``description`` — text shown to the consumer.
- ``category`` — ``0`` (default), ``ActionCategory.CRITICAL``, or
  ``ActionCategory.HIPRIORITY``.
- ``stepCount`` — number of progress stages reported by the action.
  ``0`` for single-shot actions.

The method signature must be:

.. code-block:: java

   public UInteger <name>(
       Long actionInstanceObjId,
       boolean reportProgress,
       MALInteraction interaction,
       <optional @ActionParameter arguments>);

Return ``null`` on success or a ``UInteger`` error code on failure.

``@ActionParameter``
^^^^^^^^^^^^^^^^^^^^

Each argument after the three required ones must be annotated with
``@ActionParameter``:

- ``name`` (required) — the parameter's display name.
- ``description`` — text shown to the consumer.
- ``rawType``, ``rawUnit``, ``convertedType``, ``convertedUnit`` —
  declarative type and unit metadata.
- ``conditionalConversionFieldName`` — name of a field containing a
  ``ConditionalConversionList`` for value-dependent conversions.

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
^^^^^^^^^^^^^^^^^^^

For an action with non-zero ``stepCount``, call
``connector.reportActionExecutionProgress(...)`` after each stage; see
the listener-interface API page for the signature.

Caveats
-------

- **Reflection at registration.** The framework scans the adapter class
  at registration time. Field and method names referenced in
  ``onGetFunction``, ``conditionalConversionFieldName``, and similar
  attributes are resolved by name and not checked at compile time;
  mistyped names surface only at runtime.
- **No method-level call paths.** All dispatch goes through the
  annotated members, so static analysis tools that look for unused
  methods may flag annotated handlers as unused.
- **Equivalent to the listener API.** The set of MO objects ultimately
  registered is the same. Choose the API that matches your style and
  the project's coding conventions.

Reference example
-----------------

The ``sdk/examples-space/all-mc-services`` and
``sdk/examples-space/camera-acquisitor-system`` examples use this API
end to end.
