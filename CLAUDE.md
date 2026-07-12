# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

The NanoSat MO Framework (NMF) is a Java software framework for small satellites based on CCSDS Mission Operations (MO) services. It enables apps running on a spacecraft to be started/stopped from the ground. Apps have specific Parameters, Actions and Alerts that can be monitored and controlled. The NMF has a set of MO services (Monitor & Control, Platform, COM, Software Management).

- **Documentation**: https://nanosat-mo-framework.readthedocs.io/en/latest/
- **Version**: 5.0-SNAPSHOT (`int.esa.nmf` group ID)
- **Java**: 11 (source/target)
- **License**: ESA-PL Weak Copyleft v2.4

## Build Commands

```bash
# Full build
mvn clean install

# Fast intermediate build (skip Javadoc and slow assembly)
mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true

# Build with standalone fat JARs (statically linked equivalents)
mvn install -P assembly-with-dependencies

# Build a single module
mvn install -pl core/nmf-composites

# Run all tests
mvn test

# Run tests for a single module
mvn test -pl sdk/examples-apps/all-mc-services

# Run a specific test class
mvn test -pl sdk/examples-apps/all-mc-services -Dtest=APIsTest
```

Javadoc generation produces expected (non-breaking) warnings about missing module descriptors.

## Module Structure

```
parent/                     # Parent POM with dependency management
core/
  mo-services-xml/          # CCSDS MO XML service definitions (source of truth for APIs)
  mo-services-apis/         # Generated API jars: api-nmf-com, api-nmf-mc,
                            #   api-nmf-sm, api-nmf-platform
  nmf-services-impl/        # Single unified module: all service implementations (COM, MC, SM,
                            #   Platform) plus helper utilities (clock, misc, environment)
  nmf-package-lib/          # NMF package management (install/uninstall apps on satellite)
  nmf-package-maven-plugin/ # Maven plugin: builds .nmfpackage files
  nmf-linux-maven-plugin/   # Maven plugin: generates Linux filesystem layout + bootloader files
  nmf-composites/           # Key composites and the NMF app-facing API (see below)
nmf-mission-barebone/       # Minimal mission impl for testing NMF features at runtime
mission/simulator/          # OPS-SAT spacecraft simulator (platform services impl)
sdk/
  consumer-test-tool/       # GUI tool for consuming all NMF services (CTT)
  cli-tool/                 # CLI interface to NMF
  examples-apps/            # Space app examples
  examples-ground/          # Ground app examples
  sdk-playground-environment/ # Local SDK execution environment; run scripts + generated NMF
                            #   filesystem in target/space-filesystem/ (replaces the old
                            #   ant-based sdk-execution-environment)
```

## Architecture: Key Composites (`core/nmf-composites`)

This module defines the main abstractions that space apps and missions use:

| Class | Role |
|---|---|
| `NMFInterface` | Core interface for NMF providers; exposes `getCOMServices()`, `getMCServices()`, `getPlatformServices()` |
| `NanoSatMOConnectorImpl` | Space app connector. Provides its own independent COM stack (Archive, Directory, ArchiveSync) and MC stack (Parameter, Alert, Aggregation, Action), plus Heartbeat. Consumes Platform services and the AppsLauncher service from the Supervisor. |
| `NanoSatMOSupervisor` | Satellite supervisor. Provides its own independent COM stack (Archive, Directory, ArchiveSync) and MC stack (Parameter, Alert, Aggregation, Action), plus Heartbeat, AppsLauncher, PackageManagement, and CommandExecutor. Platform services are provided by the mission-specific `initPlatformServices` implementation. |
| `NanoSatMOMonolithic` | Self-contained provider composite (extends `NMFProvider`) for single-process deployments. **Not an NMF App** — used for standalone demos and tests; does not run under an Apps Launcher and does not coexist with other apps. |
| `SpaceMOAdapterImpl` | Higher-level adapter for the space side |
| `GroundMOAdapterImpl` | Higher-level adapter for the ground side |
| `GroundMOProxy` | Protocol bridge between ground and space networks |

## Writing a Space App

A space app creates a `NanoSatMOConnectorImpl`, initialises it with an MC adapter, and lets the Supervisor manage its lifecycle.

Two equivalent APIs are available; pick either based on style.

**Simple (listener) API** — extend `SimpleMonitorAndControlAdapter`; explicit method dispatch, no reflection:
```java
public class MyApp {
    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    public MyApp() { connector.init(new MyAdapter()); }
    public static void main(String[] args) { new MyApp(); }

    class MyAdapter extends SimpleMonitorAndControlAdapter {
        @Override public void initialRegistrations(MCRegistration r) { /* register params/actions */ }
        @Override public Serializable onGetValueSimple(String name) { ... }
        @Override public boolean onSetValueSimple(String name, Serializable value) { ... }
        @Override public boolean actionArrivedSimple(String name, Serializable[] values, Long id) { ... }
    }
}
```

**Annotation API** — extend `MonitorAndControlNMFAdapter` and annotate fields/methods (registration scans annotations via reflection):
```java
@Parameter(description = "Sensor reading", rawUnit = "µT", reportIntervalSeconds = 2, readOnly = true)
Float sensorValue = 0.0f;

@Action(name = "Reset", description = "Resets the sensor")
public void reset(Long actionInstanceObjId, MALInteraction interaction) throws ExecutionFailedException { ... }
```

See `sdk/examples-apps/all-mc-services` for the full annotation-based example and `sdk/examples-apps/hello-world-simple` for the simple API.

## Running the Playground Environment

After a full build, the playground environment generates a runnable NMF filesystem under
`sdk/sdk-playground-environment/target/space-filesystem/nanosat-mo-framework/`. Convenience scripts in
`sdk/sdk-playground-environment/` wrap it:

1. Start the Supervisor Simulator (provides platform services and app lifecycle management):
   ```
   sdk/sdk-playground-environment/run_Supervisor.sh
   ```
2. Start the Consumer Test Tool (GUI):
   ```
   sdk/sdk-playground-environment/run_CTT.sh
   ```
3. In the CTT, connect using the `maltcp://` Directory Service URI printed in the Supervisor log (also written to `providerURIs.properties` in the Supervisor working directory).
4. Navigate to Apps Launcher Service → select an app → `runApp`.

## Service Layer Hierarchy

```
CCSDS MAL (transport/encoding)
  └─ COM (Archive, ArchiveSync, Directory, Login, Configuration)
       ├─ MC (Parameter, Action, Aggregation, Alert, Conversion)
       ├─ Platform (Camera, GPS, AutonomousADCS, SoftwareDefinedRadio,
       │            OpticalDataReceiver, PowerControl,
       │            ArtificialIntelligence, FPGA)
       └─ SM (AppsLauncher, PackageManagement, Heartbeat, CommandExecutor)
```

Note: the previously separate "Common" area (Directory, Configuration, Login) has been folded into COM. `Configuration` and `Login` are defined in XML; only `Login` has a consumer-side implementation today, and configuration persistence lives in `esa.mo.reconfigurable.provider.PersistProviderConfiguration` rather than a `ConfigurationProviderServiceImpl`.

Service XML definitions in `core/mo-services-xml/` are the authoritative source; the API JARs in `mo-services-apis/` are generated from them. When CCSDS MO service versions change, update the XML first.

## NMF Package System

Space apps are deployed as `.nmfpackage` files (ZIP archives) built by the `nmf-package-maven-plugin`. The Supervisor's `PackageManagement` service installs/uninstalls them and verifies their integrity via the CRC checksums in the bundled `package-metadata.properties`. Six package types are supported (`app`, `dependency`, `java`, `mission`, `nmf`, `delta`); most code deals with `app`. The `nmf-linux-maven-plugin` generates the Linux filesystem layout, the bootloader baseline files, and `setup_linux_userspace.sh` (linux-userspace isolation only) for initial hardware deployment; it is being extended to also generate the project structure for new mission integrations.

## Architectural patterns

**App chaining** (also called "Cascading NMF Apps" in earlier documentation): apps consume each other's services so that one app's output drives the next's behaviour. Used on ɸ-Sat-2 to split image processing across two apps — a first app classified image tiles as cloudy or clear, and a second app processed only the clear ones. Implemented via `SpaceMOAdapterImpl.forNMFApp(directoryURI, peerAppName)` from inside an app.

## Documentation Structure

Sphinx docs under `docs/source/` are organised into ten sections: `quickstart/`, `concepts/`, `development-app/`, `development-ground/`, `development-mission/`, `mission-integration/`, `tooling/`, `reference/`, `background/`, `removed-features/`. The MO service XML in `core/mo-services-xml/` is the source of truth referenced from both the Concepts and Reference sections.

## Environment

Logging uses `java.util.logging`; verbosity is controlled by `logging.properties` in the Supervisor's working directory (`sdk/sdk-playground-environment/target/space-filesystem/nanosat-mo-framework/etc/`).
