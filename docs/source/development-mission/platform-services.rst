==============================
Implementing Platform Services
==============================

.. contents:: Table of contents
   :local:

Platform services are the boundary between the NMF and the spacecraft's hardware. They translate MO service
calls into driver or bus commands and route back the responses from the sensor as MO responses. Every Platform service
the mission exposes must be integrated with a hardware adapter specific to that mission.

Each Platform service has two contact points:

1. The **frontend** — an MO service callable by NMF Apps and Ground software (see the MO Platform
   services API).
2. The **backend** — a Java interface that the mission implements as a hardware-specific adapter,
   following the **Adapter** structural design pattern.

The Adapter pattern
-------------------

The NMF Platform services follow the **Adapter** structural design pattern. The NMF defines a set of MO
service interfaces that can be used to develop the adapters. An NMF Mission is responsible for the creation of
the specific adapters for its mission. This is achieved by creating the concrete *adapter* class that implements
the defined interfaces and glues to the real hardware underneath.

.. code-block:: text

    NMF App / Ground Tool
          |
          | MO service call (frontend) (e.g. getLastKnownPosition)
          v
    Generic Platform Provider  <-- NMF-provided, stable
          |
          | delegates to adapter interface (backend)
          v
    Mission Adapter  <-- mission-specific, implements hardware access
          |
          v
    Hardware / OS driver

The Platform service backend interfaces
---------------------------------------

Each Platform service has a Java adapter interface in
``core/mo-services-impl/nmf-services-platform-generic`` (``gen`` package). The service itself calls
this interface for every incoming MO operation; the mission implements it to forward the call to real
hardware or a hardware abstraction layer.

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

        CameraInheritanceSkeleton                 getCameraService();
        GPSInheritanceSkeleton                    getGPSService();
        AutonomousADCSInheritanceSkeleton         getAutonomousADCSService();
        SoftwareDefinedRadioInheritanceSkeleton   getSoftwareDefinedRadioService();
        OpticalDataReceiverInheritanceSkeleton    getOpticalDataReceiverService();
        ArtificialIntelligenceInheritanceSkeleton getAIService();
    }

A mission that only supports a subset of services returns ``null`` for those it does not implement. The
``NanoSatMOConnector`` inside each NMF App checks for ``null`` before trying to use a service.

The ``init`` method receives the ``COMServicesProvider`` so adapters can publish COM Events when hardware
state changes (e.g. ADCS mode transitions, power faults).

Implementing an adapter
-----------------------

Create a class that implements the relevant adapter interface. The simulator adapters in
``mission/simulator/`` are the reference implementations; they demonstrate the method contracts and are
useful as a starting point.

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

Integrating with hardware drivers
-----------------------------------

The adapter bridges the MO service to the actual hardware. The two practical approaches for reaching
hardware from a Java adapter are:

**Option 1 — CLI process call (recommended)**

The adapter invokes a compiled native binary (a driver CLI tool or daemon) via a process call. This is
the simplest integration path and avoids any Java/native coupling. The binary lives in the
``drivers/`` directory (see `The drivers directory`_ below).

Two sub-variants are common:

- **Per-call mode** — the adapter spawns the binary on every service invocation and reads its standard
  output. Simpler to implement and debug; suitable for low-frequency operations such as a GPS fix
  request or a camera snapshot. Disadvantage: the process is spawned on every call.

- **Daemon mode** — the driver binary starts once at boot (or when the adapter is initialised) and
  listens on a local socket, pipe, or shared-memory channel. The adapter sends a request and reads the
  response. This is efficient for high-frequency services such as ADCS telemetry.

A per-call example:

.. code-block:: java

    @Override
    public GetLastKnownPositionResponse getLastKnownPosition() throws IOException {
        Process p = Runtime.getRuntime().exec(
                Deployment.getDriversDir() + "/gps-driver --get-position");
        String line = new BufferedReader(
                new InputStreamReader(p.getInputStream())).readLine();
        // parse line and build MO response
        ...
    }

**Option 2 — JNI (not recommended)**

A Java Native Interface binding can call a shared library (.so) directly from the adapter. This
approach is tightly coupled: the adapter must be compiled and linked against the driver library, the
JVM and the native code share the same process address space (a crash in the driver brings down the
entire Supervisor), and cross-compilation for the target OBC adds significant build complexity.

JNI is feasible when the hardware vendor supplies only a C/C++ SDK with no CLI equivalent, but in
practice the CLI approach is simpler, safer, and easier to test on a development machine.

The drivers directory
----------------------

The on-board filesystem layout generated by the ``nmf-linux-maven-plugin`` includes a ``drivers/``
directory under ``NMF_HOME``. Its path is available at runtime via ``Deployment.getDriversDir()``.

This directory is the conventional home for compiled driver binaries, shell wrappers, and any supporting
data files (calibration tables, device configuration files) that are specific to the platform hardware.
Driver binaries placed here are available to all adapters without the need to hard-code absolute paths.

.. code-block:: text

    NMF_HOME/
    ├── drivers/           ← native driver binaries and support files
    │   ├── gps-driver
    │   ├── adcs-daemon
    │   └── camera-driver
    ├── apps/
    ├── jars-nmf/
    └── ...

Reference mission hardware adapters
------------------------------------

Concrete hardware adapters that have been written for past missions, as a starting point for new mission
work:

.. list-table::
   :header-rows: 1
   :widths: 25 40 35

   * - Service
     - Mission
     - Hardware / adapter
   * - Camera
     - OPS-SAT
     - BST IMS-100
   * - Camera
     - ɸ-Sat-2
     - Multispectral imager
   * - AutonomousADCS
     - OPS-SAT
     - **iADCS-100** (Berlin Space Technologies)
   * - AutonomousADCS
     - ɸ-Sat-2
     - In-house implementation
   * - SoftwareDefinedRadio
     - OPS-SAT
     - OPS-SAT SDR
   * - ArtificialIntelligence
     - ɸ-Sat-2
     - Intel Movidius VPU via ``AIMovidiusAdapter``

Adapter not available
----------------------

If a Platform service is not available on the target hardware, do not implement a stub that returns
dummy data — return ``null`` from the relevant ``get*Service()`` method in the provider. The framework
propagates a ``SERVICE_NOT_AVAILABLE`` error to any consumer that attempts to use it, which is far more
useful than silently returning NULL values.

OPS-SAT: runtime adapter selection via property
-------------------------------------------------

On OPS-SAT, a property-based mechanism was added to allow switching Platform service adapters at
runtime without recompiling the mission layer. This was a mission-specific convenience for an
experimental in-orbit platform that needed to hot-swap adapter implementations during the mission.

The ``nmf.platform.impl`` Java system property in ``provider.properties`` selects the entire
``PlatformServicesProviderInterface`` implementation class::

    nmf.platform.impl=esa.mo.platform.impl.util.PlatformServicesProviderSoftSim

Within ``PlatformServicesProviderSoftSim``, individual adapter classes can be overridden via
``platformsim.properties`` in the Supervisor's working directory::

    camera.adapter=esa.mo.platform.impl.provider.adapters.MyCameraAdapter
    gps.adapter=esa.mo.platform.impl.provider.adapters.MyGPSAdapter
    adcs.adapter=esa.mo.platform.impl.provider.adapters.MyADCSAdapter

This mechanism is **not required** for a new mission. On ɸ-Sat-2, for example, each adapter was
simply compiled into the mission layer with no runtime switching. The property mechanism is documented
here because the SDK simulator uses it to swap in soft-sim adapters, and the OPS-SAT configuration
files reference it.

For documentation on each individual service's semantics, see
:doc:`../development-app/platform-services`.
