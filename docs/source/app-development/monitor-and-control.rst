=============================
Monitor & Control integration
=============================

.. contents:: Table of contents
   :local:

Monitor & Control (M&C) integration is the mechanism by which an app
exposes its state and behaviour: telemetry parameters that ground
operators can read and subscribe to, actions they can invoke, alerts
the app can raise, and aggregations that group related parameters.

The NMF provides two equivalent APIs for M&C integration. They are
equally supported; pick the one that matches your style:

- :doc:`mc-listener-api` — extend ``SimpleMonitorAndControlAdapter`` and
  override explicit methods for each operation. No reflection at
  runtime; all dispatch is explicit. Closer to the underlying MO
  service interface.
- :doc:`mc-annotation-api` — extend ``MonitorAndControlNMFAdapter`` and
  annotate fields and methods with ``@Parameter``, ``@Action``, and
  ``@ActionParameter``. More compact for apps with many parameters and
  actions; relies on reflection at registration time to discover the
  annotated members.

Both APIs ultimately register the same MO objects with the Supervisor's
Directory Service, and the resulting app is indistinguishable from the
consumer side.

The MC services
---------------

The MC service category provides five services. An app's adapter
exposes whichever of these are relevant to its behaviour:

- **Parameter** — read, set, and subscribe to telemetry values.
- **Action** — invoke commands, optionally with multi-stage progress
  reporting.
- **Aggregation** — group parameters into reports published as a unit.
- **Alert** — publish operational alerts.
- **Conversion** — declarative conversions between raw and engineering
  parameter values.

See :doc:`../concepts/mo-architecture` for where the MC services sit in
the broader CCSDS MO stack.

What follows in this guide
--------------------------

The next two pages cover the two APIs in detail. The :doc:`worked-example`
page at the end of the guide implements the same app twice — once with
each API — so you can compare them side by side.
