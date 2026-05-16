==================
MAL data types
==================

.. contents:: Table of contents
   :local:

This page is a quick reference for the MAL data types and their
mapping to Java primitives. The types are defined formally in
``area001-MAL.xml`` and the generated API JARs.

The types live in ``org.ccsds.moims.mo.mal.structures``.

Primitive attribute types
-------------------------

.. list-table::
   :header-rows: 1
   :widths: 15 18 12 55

   * - Java
     - MAL
     - Range
     - Notes
   * - ``boolean``
     - ``Boolean``
     - —
     - —
   * - ``byte``
     - ``Octet``
     - signed
     - —
   * - ``byte``
     - ``UOctet``
     - unsigned
     - —
   * - ``short``
     - ``Short``
     - signed
     - —
   * - ``short``
     - ``UShort``
     - unsigned
     - —
   * - ``int``
     - ``Integer``
     - signed
     - —
   * - ``int``
     - ``UInteger``
     - unsigned
     - —
   * - ``long``
     - ``Long``
     - signed
     - —
   * - ``long``
     - ``ULong``
     - unsigned
     - —
   * - ``float``
     - ``Float``
     - —
     - —
   * - ``double``
     - ``Double``
     - —
     - —
   * - ``String``
     - ``String``
     - —
     - —
   * - ``String``
     - ``Identifier``
     - —
     - Restricted character set; used for names.
   * - ``String``
     - ``URI``
     - —
     - MAL URI.
   * - ``byte[]``
     - ``Blob``
     - —
     - Variable-length binary buffer.
   * - —
     - ``Time``
     - —
     - Absolute time.
   * - —
     - ``FineTime``
     - —
     - High-precision absolute time.
   * - —
     - ``Duration``
     - —
     - Duration in seconds (``Double``).

Choose the unsigned (``U*``) variants when the value cannot be
negative.

Composite and enumeration types
-------------------------------

Beyond the primitive attributes, MAL supports:

- **Composites** — structured records (e.g. ``ParameterDefinition``,
  ``ActionInstanceDetails``). Generated as Java classes with typed
  getters/setters.
- **Enumerations** — generated as Java enum types
  (e.g. ``ActionCategory``).
- **Lists** — every type ``T`` has a corresponding ``TList`` generated
  by the API generator.

Use the generated classes rather than constructing the underlying MAL
structures by hand.

Authoritative definitions
-------------------------

The MAL XML in ``core/mo-services-xml/src/main/resources/xml/area001-MAL.xml``
is the source of truth for the type set and its semantics. The
generated APIs in ``core/mo-services-apis/api-nmf-com`` (and the other
API JARs) provide the Java bindings.
