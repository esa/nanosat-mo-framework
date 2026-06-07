======================================
Testing and Deployment Considerations
======================================

.. contents:: Table of contents
   :local:

Separation of concerns
-----------------------

NMF Mission development is separate from NMF App development and NMF Ground software development. The three
problem domains have independent build artefacts and independent development cycles. This separation makes it
possible to:

- Test NMF Apps against the generic SDK Supervisor (with simulated Platform services) before the
  mission-specific Supervisor exists.
- Update NMF Apps without rebuilding or redeploying the mission software, and vice versa.
- Run two different NMF implementations in different languages and still connect the NMF Composites to each
  other, because the only coupling is through the standardised MO service interfaces.

Mission-specific services
--------------------------

By reusing parts of an NMF implementation it is possible to develop mission-specific services — services whose
interface or data model is tailored to one particular spacecraft. An application connecting to such a service
is no longer mission-agnostic, because it depends on that custom service definition. This is a deliberate
trade-off: mission-specific services enable tighter integration with the hardware at the cost of portability.
Apps that rely on them cannot be deployed on a different mission without modification.

Testing progression
--------------------

The recommended path from development to flight is:

1. **SDK Playground environment.** Develop and validate the mission Supervisor against the generic simulator on a
   developer workstation. All Platform service calls are handled by ``PlatformServicesProviderSoftSim``.

2. **Mission Supervisor with simulated hardware.** Replace the generic Supervisor with the mission's own
   Supervisor subclass. Platform adapters can be configured to use either simulated or real hardware per
   service via the ``nmf.platform.impl`` property.

3. **FlatSat.** Integrate the full software stack with representative engineering hardware. Platform adapters
   talk to real devices over real buses.

4. **Flight model.** Final software validation on the actual flight unit before launch.

For the concrete example of this progression applied to OPS-SAT, see :doc:`../mission-integration/ops-sat`.

Transport and the Ground MO Proxy
-----------------------------------

The SDK uses MALTCP for all transport, which is convenient for development but not suitable for a real space
link. Missions that use MALSPP or another space-link transport must:

1. Implement or configure the transport binding for both the Supervisor and the Apps. The binding is
   configured via ``transport.properties`` in both the Supervisor's and the Ground MO Proxy's working
   directories.
2. Deploy a **Ground MO Proxy** that bridges the space-side transport to the MALTCP used by ground consumers
   (CTT, ground applications).

The Ground MO Proxy is transparent to ground consumers — they continue to connect via MALTCP without any
knowledge of the underlying space link protocol.

See :doc:`../development-ground/ground-mo-proxy` for the ground side of this configuration.

Further reading
----------------

- :doc:`../mission-integration/ops-sat` — complete worked example for the ESA OPS-SAT mission.
- :doc:`../mission-integration/phi-sat-2` — worked example for the ESA ɸ-Sat-2 mission.
