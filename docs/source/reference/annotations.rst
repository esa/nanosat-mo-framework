====================
Annotation reference
====================

.. contents:: Table of contents
   :local:

This page is the complete reference for the M&C annotation API. For
discussion and examples, see
:doc:`../app-development/monitor-and-control`.

The annotations live in ``esa.mo.nmf.annotations``.

``@Parameter``
--------------

Applied to a field. Registers the field as an MC Parameter.

.. list-table::
   :header-rows: 1
   :widths: 28 12 60

   * - Attribute
     - Default
     - Description
   * - ``name``
     - ``""``
     - Parameter name. Defaults to the field name.
   * - ``description``
     - ``""``
     - Description shown to consumers.
   * - ``malType``
     - ``""``
     - MAL type. Auto-detected for ``Attribute`` subtypes and built-in Java types.
   * - ``rawUnit``
     - ``""``
     - Unit string (e.g. ``"degC"``).
   * - ``generationEnabled``
     - ``true``
     - Whether generation is enabled at startup.
   * - ``reportIntervalSeconds``
     - ``0``
     - Periodic generation interval; ``0`` disables periodic generation.
   * - ``validityExpressionFieldName``
     - ``""``
     - Name of a field holding a ``ParameterExpression`` for validity.
   * - ``conversionFunctionName``
     - ``""``
     - Name of a field holding a ``ParameterConversion``.
   * - ``readOnly``
     - ``false``
     - Reject writes if true. Always true for ``final`` fields.
   * - ``restored``
     - ``true``
     - Restore the last-known value on startup.
   * - ``onGetFunction``
     - ``""``
     - Name of a public, no-argument method called before each read.
   * - ``aggregations``
     - ``{}``
     - Names of aggregations this parameter belongs to.

``@Action``
-----------

Applied to a method. Registers the method as an MC Action.

.. list-table::
   :header-rows: 1
   :widths: 18 12 70

   * - Attribute
     - Default
     - Description
   * - ``name``
     - ``""``
     - Action name. Defaults to the method name.
   * - ``description``
     - ``""``
     - Description shown to consumers.
   * - ``category``
     - ``0``
     - ``0`` (default), ``ActionCategory.CRITICAL``, or ``ActionCategory.HIPRIORITY``.
   * - ``stepCount``
     - ``0``
     - Number of progress stages reported; ``0`` for single-shot.

The annotated method must have this signature:

.. code-block:: java

   public UInteger <name>(
       Long actionInstanceObjId,
       boolean reportProgress,
       MALInteraction interaction,
       <optional @ActionParameter arguments>);

Return ``null`` on success or a ``UInteger`` error code on failure.

``@ActionParameter``
--------------------

Applied to each non-required parameter of an ``@Action`` method.

.. list-table::
   :header-rows: 1
   :widths: 35 15 50

   * - Attribute
     - Default
     - Description
   * - ``name`` *(required)*
     - —
     - Parameter display name.
   * - ``description``
     - ``""``
     - Description shown to consumers.
   * - ``rawType``
     - ``0``
     - Raw MAL attribute type ordinal.
   * - ``rawUnit``
     - ``""``
     - Unit of the raw value.
   * - ``conditionalConversionFieldName``
     - ``""``
     - Field holding a ``ConditionalConversionList``.
   * - ``convertedType``
     - ``-1``
     - MAL attribute type after conversion.
   * - ``convertedUnit``
     - ``""``
     - Unit of the converted value.

``@Aggregation``
----------------

Applied to a field declaring an aggregation that other ``@Parameter``
fields can join via their ``aggregations`` attribute. See the API JAR
for the exact attribute set.
