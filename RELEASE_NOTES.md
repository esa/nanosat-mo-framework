ESA NanoSat MO Framework - Release Notes
========================

These Release Notes include a short summary of the updates done for each version.
The different versions and respective updates are the following:

### Version 5.0 (20 July 2026)
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

### Version 2.0.0
* Untracked

### Version 1.0.0
* First release of the NMF

