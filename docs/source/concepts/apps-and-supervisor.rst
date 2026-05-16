===========================
NMF Apps and the Supervisor
===========================

.. contents:: Table of contents
   :local:

The NMF runtime model is built around two composites that work together:
the **Supervisor**, which orchestrates the spacecraft-side runtime, and
the **Connector**, which lives inside each NMF App and bridges to the
Supervisor. A third composite, the **Monolithic** provider, is described
at the end of this page as a separate, single-process pattern that
does not involve Apps.

What an NMF App is
------------------

An **NMF App** is a JVM process that exposes MO services and is managed by a
Supervisor running on the same spacecraft host. From the outside, an app is
identified by its name and reached through its Directory Service URI. Apps
typically expose Monitor & Control services (parameters, actions, alerts)
and may consume Platform services from the Supervisor.

Apps are deployed as :doc:`packages` and started, stopped, and observed
through the SM ``AppsLauncher`` service. See :doc:`mo-architecture` for the
service stack and :doc:`lifecycle` for the operational view.

The Supervisor
--------------

The **Supervisor** (``NanoSatMOSupervisor``) is the process that owns the
spacecraft-side of the NMF runtime. Its responsibilities include:

- Hosting the **Software Management** services — ``AppsLauncher``,
  ``PackageManagement``, ``Heartbeat``, ``CommandExecutor`` — through which
  apps are managed and the spacecraft is operated.
- Hosting the **Platform** services that expose the spacecraft's hardware
  (camera, GPS, ADCS, power, etc.) to apps.
- Hosting a **Directory Service** that all apps register against, so that
  consumers and other apps can discover them.
- Spawning and supervising app processes started through the ``AppsLauncher``
  service.

A single Supervisor runs per spacecraft host. It is normally started at boot
and remains up for the duration of the mission.

The Connector
-------------

Inside each app, the **Connector** (``NanoSatMOConnectorImpl``) is the
gateway to the Supervisor. It performs three jobs:

- **Registers the app** with the Supervisor's Directory Service so it
  becomes discoverable.
- **Provides consumer stubs** for the Supervisor's Platform and SM services,
  accessed via ``NMFInterface`` methods such as ``getPlatformServices()``,
  so the app can read GPS, command the camera, and so on.
- **Routes the app's M&C services** (Parameter, Action, Alert, Aggregation,
  Conversion) for consumption by ground software or by other apps.

The Connector is the only NMF dependency the app's main class needs to know
about; everything else is reached through it.

The Monolithic provider
-----------------------

The **Monolithic** composite (``NanoSatMOMonolithic``) is a separate,
self-contained provider pattern. It is not an NMF App. A Monolithic
provider extends ``NMFProvider`` directly, wires up Platform services
internally through ``initPlatformServices``, and combines them with a
custom MC adapter — all in one process. There is no Apps Launcher
hosting multiple coexisting Apps; the entire deployment is the one
Monolithic process.

Monolithic is used for:

- Standalone testing of provider-side code without spinning up a full
  Supervisor + App deployment.
- Demonstrations and self-contained examples that bring their own
  simulator (``sdk/all-mc-services-with-sim``).
- Quick verifications during development.

A consumer connecting via the CTT cannot tell from the outside whether
it is talking to a Supervisor + App pair or to a Monolithic provider —
the same MO services are exposed. The conceptual difference is on the
provider side: a Monolithic provider has no notion of multiple
coexisting Apps, no Apps Launcher orchestration, and no Connector. For
anything that genuinely needs the App model (lifecycle management
across many components, package-based deployment, app chaining), use
the Supervisor + Apps pattern instead.

Communication paths
-------------------

Four communication patterns appear repeatedly in NMF deployments:

- **Consumer → Supervisor.** A ground operator (CTT, EUD4MO, custom ground
  software) connects to the Supervisor's Directory Service, discovers
  running apps, and invokes operations on the Supervisor's SM and Platform
  services.
- **Consumer → App.** Once an app is discovered through the Directory
  Service, the consumer connects directly to the app's MC services to read
  parameters, invoke actions, or subscribe to alerts.
- **App → Supervisor.** Apps consume the Supervisor's Platform services
  through their Connector — for example, reading GPS position before
  scheduling an observation.
- **App → App** (*app chaining*). One app's output can drive another app's
  behaviour. The ɸ-Sat-2 mission demonstrated this with two-stage image
  processing: a first app classified image tiles as cloudy or clear; a
  second app processed only the clear ones.

.. mermaid::

    flowchart LR
        subgraph ground[Ground segment]
            G[Consumer / CTT / Ground app]
        end
        subgraph space[Spacecraft]
            S[Supervisor]
            P[Platform services]
            A[App A]
            B[App B]
            S --- P
            S -. spawns .-> A
            S -. spawns .-> B
        end
        G -->|MO over maltcp| S
        G -->|MO over maltcp| A
        G -->|MO over maltcp| B
        A -. consumes .-> P
        A -->|app chaining| B

For ground deployments where the consumer is on a network separate from the
space link, a **GroundMOProxy** bridges the two. See the Mission Integration
section for mission-specific topologies.
