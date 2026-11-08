ESA NanoSat MO Framework - Release Notes
========================

These Release Notes include a short summary of the updates done for each version.
The different versions and respective updates are the following:

### Version 6.0 (in development)
* Upgrades to CCSDS MO services v15.0, whose consumer stubs throw the declared error classes instead of MALInteractionException
* Requires Java 21 or later
* Adds multiple layers of security to the NMF
* Adds NMF Access Control
* Several updates to the Package Management service and Apps Launcher service
* Several updates to the Aggregation service and Parameter service
* Adds the listExecutions operation to the Action service, with the action executions command of the CLI: the executions of actions that the provider received, each with its request and the stages that it reached
* Removes the Conversion service and the conversion of parameter values
* Removes the Configuration service: its ConfigurationService and ConfigurationProvider COM objects are now objects 2 and 3 of the Directory service
* Adds testbed-simulator, which runs the spacecraft simulator and checks what its clients receive
* Merges nmf-composites, nmf-services-impl and nmf-package-lib into a single nmf-core module: the three artifacts are no longer published, so Apps, missions and other projects that used them depend on nmf-core instead
* Removes the OPS-SAT leftovers from the code and the READMEs, and renames the add-on folder of the Celestia visualisation to extras/cubesat
* Builds the MCP adapter with the rest of the SDK as a prototype
* Fixes the Geofence of the Raspberry Pi mission
* Removes the Facebook ground example
* Adds additional errors that say why a request was refused: App Not Running, Package Installed, Package Not Installed, Integrity Check Failed, Checksum Mismatch and Permissions Not Acknowledged in Software Management, and Execution Failed in Platform
* Renumbers the COM errors Invalid Argument and Duplicate to 100 and 101: as 1 and 2 they had the numbers of the MC, SM and Platform errors, so a consumer could receive the wrong error, such as Read Only for an Invalid Argument

### Version 5.1 (25 September 2026)
* Upgrades to CCSDS MO services v14.2
* Adds the Swarms mission: a constellation of NMF satellites
* Adds the Raspberry Pi mission: merging its separate repository into the main one
* Splits the spacecraft simulator into two missions, simulator-lite (analytic orbit) and simulator-orekit (Orekit-propagated)
* Adds a Celestia visualisation of the simulated spacecraft: live position and attitude, NASA Earth textures, and a whole constellation in one window
* Ships the NMF core software baseline as an NMF Package
* Generates the space filesystem of every mission in the repository through the filesystem generator
* Renames the Parameters, Actions and Alerts to the dotted lowercase convention
* Completes the Javadoc of the public API, the core services, the Consumer Test Tool, the CLI tool and the example apps
* Binds each App's own storage directory in the bubblewrap and Docker isolation modes, and detects BusyBox when setting up linux-userspace isolation
* CTT: Adds direct button to connect to a running App from the Apps Launcher service
* Removes the SFTP browser and the SSH client from the Consumer Test Tool
* Removes the commons-exec dependency
* Adds testbeds for the framework upgrade and rollback path
* Fixes many smaller defects: files left open after being written, Delete in the Consumer Test Tool not reaching the COM Archive, separators lost from dumped values, and dead code throughout

### Version 5.0 (21 July 2026)
* Simplifies the APIs, especially the M&C (removal of the identity model and removal of the concept of Groups)
* Adds the nmf-mission-barebone for simple testing
* Merges all NMF Composites into one single project
* Migrates all COM timestamps from FineTime to Time (millisecond precision)
* Merges the Common services (Directory, Login) into the COM, removing the separate Common area
* Removes the ActivityTracking service from COM; action execution progress is now tracked via the Action service's `monitorExecution` PUB-SUB operation and `ExecutionStatus` COM objects
* Adds the NMF Bootloader: a primary → secondary → factory baseline fallback ladder with integrity checks
* Adds a Docker image build for the barebone mission
* Upgrades to CCSDS MO services v14.1
* Merges all service implementations (COM, MC, SM, Platform) and the environment helpers into a single nmf-services-impl module
* Adds app isolation modes: none, linux-userspace, bubblewrap, and docker-containers
* Adds a comprehensive testbed suite (MC/COM area, end-to-end, performance)
* Reworks the app lifecycle: AppStarted/AppStopped COM objects and monitorEvents replace COM Events
* Replaces the Clock Platform service with a Heartbeat getTime operation
* Adds live command output streaming to the CommandExecutor service (monitorOutput PUB-SUB)

### Version 4.0 (22 June 2025)
* Significantly improves the codebase of the NanoSat MO Framework
* Uses MO services v12.1
* Moves many of the Helper Tools classes to the MAL API
* Updates the Orekit data
* Removes unnecessary dummy/dumb auto-generated tests
* Removes the Mission Planning services

### Version 3.0.0 (July 2024)
* Adds the new Artificial Intelligence service
* Includes all updates for the OPS-SAT mission
* Includes all updates for the Phi-Sat-2 mission
* Introduces AppStorage class

> Note: this release consolidated the OPS-SAT and Phi-Sat-2 missions, which until then each developed on its own branch (Phi-Sat-2 ran on snapshot builds and was tagged, but never released as a proper version), into a single stable baseline for both and for future development. The privilege separation developed for Phi-Sat-2 — the supervisor running as the dedicated unprivileged nmf-admin user instead of root, and each App as its own dedicated Linux user — was brought into this baseline (see security-toolkit/SECURITY_ADVISORIES.md, NMF-2026-003).

### Version 2.0.0
* Untracked

### Version 1.0.0
* First release of the NMF

