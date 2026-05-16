# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

The NanoSat MO Framework (NMF) is a Java software framework for small satellites based on CCSDS Mission Operations (MO) services. It enables apps running on a spacecraft to be started/stopped from the ground and to expose telemetry and commands via standardized MO services (Monitor & Control, Platform, COM, Software Management).

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
mvn test -pl sdk/examples-space/all-mc-services

# Run a specific test class
mvn test -pl sdk/examples-space/all-mc-services -Dtest=APIsTest
```

Javadoc generation produces expected (non-breaking) warnings about missing module descriptors.

## Module Structure

```
parent/                     # Parent POM with dependency management
core/
  nmf-environment/          # Helper utilities (clock, misc)
  mo-services-xml/          # CCSDS MO XML service definitions (source of truth for APIs)
  mo-services-apis/         # Generated API jars: api-nmf-com, api-nmf-mc,
                            #   api-nmf-sm, api-nmf-platform
  mo-services-impl/         # Service implementations: nmf-services-com,
                            #   nmf-services-mc, nmf-services-sm, nmf-services-platform-generic
  nmf-package-lib/          # NMF package management (install/uninstall apps on satellite)
  nmf-package-maven-plugin/ # Maven plugin: builds .nmfpackage files
  nmf-linux-maven-plugin/   # Maven plugin: generates Linux filesystem layout + fresh_install.sh
  nmf-composites/           # Key composites and the NMF app-facing API (see below)
nmf-mission-barebone/       # Minimal mission impl for testing NMF features at runtime
mission/simulator/          # OPS-SAT spacecraft simulator (platform services impl)
sdk/
  consumer-test-tool/       # GUI tool for consuming all NMF services (CTT)
  cli-tool/                 # CLI interface to NMF
  examples-space/           # Space app examples
  examples-ground/          # Ground app examples
  sdk-execution-environment/ # Assembled SDK execution environment (output in sdk-execution-environment/target/)
```

## Architecture: Key Composites (`core/nmf-composites`)

This module defines the main abstractions that space apps and missions use:

| Class | Role |
|---|---|
| `NMFInterface` | Core interface for NMF providers; exposes `getCOMServices()`, `getMCServices()`, `getPlatformServices()` |
| `NanoSatMOConnectorImpl` | Space app connector — connects to the Supervisor for platform/SM services |
| `NanoSatMOSupervisor` | Runs on the satellite; manages the lifecycle of apps (start/stop) via `AppsLauncher` service |
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
public UInteger reset(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) { ... }
```

See `sdk/examples-space/all-mc-services` for the full annotation-based example and `sdk/examples-space/hello-world-simple` for the simple API.

## Running the SDK Locally

The assembled SDK is produced in `sdk/sdk-execution-environment/target/nmf-sdk-5.0-SNAPSHOT/` after a full build.

1. Start the Supervisor Simulator (provides platform services and app lifecycle management):
   ```
   sdk/sdk-execution-environment/target/nmf-sdk-5.0-SNAPSHOT/bin/space/nanosat-mo-supervisor-sim/nanosat-mo-supervisor-sim.sh
   ```
2. Start the Consumer Test Tool (GUI):
   ```
   sdk/consumer-test-tool/runCTT.sh
   ```
3. In the CTT, connect using the `maltcp://` Directory Service URI printed in the Supervisor log (also written to `providerURIs.properties` in the Supervisor working directory).
4. Navigate to Apps Launcher Service → select an app → `runApp`.

## Service Layer Hierarchy

```
CCSDS MAL (transport/encoding)
  └─ COM (Event, Archive, ArchiveSync, Directory, Login, Configuration)
       ├─ MC (Parameter, Action, Aggregation, Alert, Conversion)
       ├─ Platform (Camera, GPS, AutonomousADCS, SoftwareDefinedRadio,
       │            OpticalDataReceiver, PowerControl, Clock,
       │            ArtificialIntelligence)
       └─ SM (AppsLauncher, PackageManagement, Heartbeat, CommandExecutor)
```

Note: the previously separate "Common" area (Directory, Configuration, Login) has been folded into COM. `Configuration` and `Login` are defined in XML; only `Login` has a consumer-side implementation today, and configuration persistence lives in `esa.mo.reconfigurable.provider.PersistProviderConfiguration` rather than a `ConfigurationProviderServiceImpl`.

Service XML definitions in `core/mo-services-xml/` are the authoritative source; the API JARs in `mo-services-apis/` are generated from them. When CCSDS MO service versions change, update the XML first.

## NMF Package System

Space apps are deployed as `.nmfpackage` files built by the `nmf-package-maven-plugin`. The Supervisor's `PackageManagement` service installs/uninstalls these. The `nmf-linux-maven-plugin` generates the Linux filesystem layout and `fresh_install.sh` for initial deployment to hardware.

## Environment

Set `NMF_HOME` to the repository root before running apps:
```bash
export NMF_HOME=/path/to/nanosat-mo-framework
```

Logging uses `java.util.logging`; verbosity is controlled by `logging.properties` in `NMF_HOME`.
