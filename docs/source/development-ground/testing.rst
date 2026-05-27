============================
Testing a ground application
============================

.. contents:: Table of contents
   :local:

A ground application can be tested against a real Supervisor + App deployment, or against a self-contained
Monolithic provider.

Distributed setup
-----------------

The distributed setup mirrors operations: a Supervisor (with or without the simulator) runs and accepts a real
app, and the ground application connects to the Supervisor's Directory Service.

1. Start the Supervisor with simulator (see :doc:`../quickstart/index`).
2. Launch the target app through ``AppsLauncher.runApp``.
3. Run the ground application with ``-Desa.mo.nmf.centralDirectoryURI=...`` pointing at the Supervisor's
   Directory Service URI.

This setup exercises the full distributed code path, including transport serialisation and Directory Service
discovery.

Monolithic provider
-------------------

For faster turnaround in unit-test-like scenarios, a **Monolithic provider** (``NanoSatMOMonolithic``) runs as
a single self-contained process that exposes the MO services a consumer needs. It is not an NMF App — see
:doc:`../concepts/apps-and-supervisor` — but from the ground application's perspective the consumer-side code
is identical: only the Directory Service URI changes.

The Monolithic pattern is suitable for:

- Unit testing of consumer-side logic that needs a real M&C provider.
- Quick demonstrations and integration smoke tests.

Limitations:

- There is no notion of multiple coexisting Apps, so consumer code paths that depend on App-level operations
  (listing apps, monitoring app execution) cannot be exercised this way.
- Platform services are present inside the Monolithic process (the concrete subclass implements
  ``initPlatformServices`` to wire them up). Whether they are also exposed to external consumers depends on
  the specific Monolithic subclass.

Automated regression
--------------------

For automated regression tests, structure the ground application as a JUnit test that brings up a Monolithic
provider in ``@BeforeAll``, exercises the consumer-side code paths, and tears down in ``@AfterAll``. The CTT
is not needed for automation; the ``GroundMOAdapterImpl`` consumer pattern in :doc:`ground-mo-adapter` is
sufficient.
