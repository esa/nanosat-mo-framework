=========================
Integrating a new mission
=========================

.. contents:: Table of contents
   :local:

Integrating NMF into a new mission means producing a mission-specific
deployment of the framework, with hardware adapters for the platform
services that drive real spacecraft equipment and any transport or
packaging customisations the mission requires.

This page describes the integration shape at a high level. The
existing missions — :doc:`ops-sat` and :doc:`phi-sat-2` — are concrete
references.

What a mission integration provides
-----------------------------------

A mission integration is a separate Maven project (the **mission
module**) that depends on NMF Core and produces:

- **Hardware adapters** for the Platform services the spacecraft can
  fulfil. Each adapter implements the corresponding
  ``<Service>AdapterInterface`` from
  ``nmf-services-platform-generic``.
- A **Supervisor packaging** that bundles the mission-specific
  adapters, transport bindings, and configuration files alongside the
  generic Supervisor.
- An **assembly profile** that produces the mission's deployable
  artefacts. Apps for the mission are delivered as **NMF Packages**
  (see :doc:`../concepts/packages`); the assembly profile produces
  whatever additional artefacts the spacecraft host needs for initial
  bring-up (file trees, scripts).
- A **Ground MO Proxy packaging** if the mission uses a non-TCP
  transport such as MALSPP (typical for any real space link).

Mission project structure
-------------------------

A Maven plugin to generate the mission project structure is under
development in ``core/nmf-linux-maven-plugin``. The plugin is not yet
complete; until it is, the existing missions (:doc:`ops-sat`,
:doc:`phi-sat-2`) are the working references for the project layout.

Adapter implementations
-----------------------

For each Platform service the mission supports, implement the relevant
adapter interface. The simulator's adapters in
``mission/simulator/`` are a useful reference for the contract.

Selecting an adapter at runtime is done through
``platformsim.properties`` in the Supervisor's working directory: the
``<service>.adapter`` property names the adapter class to load.

Transport configuration
-----------------------

If the mission uses MALSPP over a real space link, configure the
transport via ``transport.properties`` in both the Supervisor's
working directory and the Ground MO Proxy's. The CTT and other ground
consumers continue to use MALTCP and reach the spacecraft through the
Ground MO Proxy.

Testing
-------

The recommended testing progression is:

1. **SDK environment.** Validate the app against the generic Supervisor
   with simulator on a developer workstation.
2. **Mission Supervisor with simulator.** Replace the generic Supervisor
   with the mission's hybrid Supervisor + simulator. Platform adapters
   can be configured to use simulated or real hardware per service.
3. **FlatSat.** Test against the mission's representative ground
   testbed with real hardware in the loop.
4. **Flight model.** Final validation on the flight unit.

See :doc:`ops-sat` for a concrete example of this progression.
