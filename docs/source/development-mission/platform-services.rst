==============================
Implementing Platform Services
==============================

.. contents:: Table of contents
   :local:

Platform services are the boundary between the NMF and the spacecraft's
hardware. They translate MO service calls into driver or bus commands
and route sensor readings back up as MO responses. Every Platform
service the mission exposes must be backed by a hardware adapter
specific to that mission.

The adapter model
-----------------

Each Platform service has a pair of Java interfaces in
``core/mo-services-impl/nmf-services-platform-generic``:

- An **adapter interface** (in the ``gen`` package) that the service
  skeleton calls for every incoming MO operation. The mission
  implements this interface to forward the call to real hardware or a
  hardware abstraction layer.
- A **generic provider** that owns the MO skeleton, validates
  inputs, and delegates to the adapter.

The available adapter interfaces are:

.. list-table::
   :header-rows: 1
   :widths: 30 70

   * - Interface
     - Covers
   * - ``CameraAdapterInterface``
     - Image capture, snapshot streaming
   * - ``GPSAdapterInterface``
     - Position, velocity, time, NMEA sentences
   * - ``AutonomousADCSAdapterInterface``
     - Attitude determination, mode commands
   * - ``SoftwareDefinedRadioAdapterInterface``
     - SDR configuration, I/Q streaming
   * - ``OpticalDataReceiverAdapterInterface``
     - Optical downlink
   * - ``AIAdapterInterface``
     - On-board AI/ML inference
   * - ``ClockAdapterInterface``
     - Spacecraft time source
   * - ``PowerControlAdapterInterface``
     - Power line switching

``PlatformServicesProviderInterface``
--------------------------------------

All adapters are collected by a single top-level interface::

    public interface PlatformServicesProviderInterface {
        void init(COMServicesProvider comServices) throws MALException;

        CameraInheritanceSkeleton      getCameraService();
        GPSInheritanceSkeleton         getGPSService();
        AutonomousADCSInheritanceSkeleton getAutonomousADCSService();
        SoftwareDefinedRadioInheritanceSkeleton getSoftwareDefinedRadioService();
        OpticalDataReceiverInheritanceSkeleton  getOpticalDataReceiverService();
        ArtificialIntelligenceInheritanceSkeleton getAIService();
    }

A mission that only supports a subset of services returns ``null`` for
those it does not implement. The ``NanoSatMOConnector`` inside each
NMF App checks for ``null`` before trying to use a service.

The ``init`` method receives the ``COMServicesProvider`` so adapters
can publish COM Events when hardware state changes (e.g. ADCS mode
transitions, power faults).

Implementing an adapter
-----------------------

Create a class that implements the relevant adapter interface. The
simulator adapters in ``mission/simulator/`` are the reference
implementations; they demonstrate the method contracts and are useful
as a starting point.

A minimal GPS adapter skeleton:

.. code-block:: java

    public class MyGPSAdapter implements GPSAdapterInterface {

        @Override
        public GetLastKnownPositionResponse getLastKnownPosition() {
            // Query hardware GPS receiver and build response
            Position pos = new Position(lat, lon, alt);
            Velocity vel = new Velocity(vx, vy, vz);
            Time ts = Time.now();
            return new GetLastKnownPositionResponse(pos, vel, ts);
        }

        // ... implement remaining interface methods
    }

Runtime adapter selection
--------------------------

The simulator Supervisor selects the adapter class at runtime via the
``nmf.platform.impl`` Java system property::

    nmf.platform.impl=esa.mo.platform.impl.util.PlatformServicesProviderSoftSim

This allows the same Supervisor binary to run against simulated hardware
(on a developer workstation) or real hardware (on the spacecraft) simply
by changing the property in ``provider.properties``. Mission integrators
can adopt the same pattern to support mixed configurations during
development and testing.

Adapter not available
----------------------

If a Platform service is not available on the target hardware, do not
implement a stub that returns dummy data — return ``null`` from the
relevant ``get*Service()`` method in the provider. The framework
propagates a ``SERVICE_NOT_AVAILABLE`` error to any consumer that
attempts to use it, which is far more useful than silently returning
nonsense values.

For documentation on each individual service's semantics, see
:doc:`../development-app/platform-services/index`.
