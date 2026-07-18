ESA NanoSat MO Framework - Release Notes
========================

These Release Notes include a short summary of the updates done for each version.
The different versions and respective updates are the following:

### Version 5.0 (18 July 2026)
* Simplifies the APIs, especially the M&C (removal of the identity model; removal of the concept of Groups)
* Adds the nmf-mission-barebone for simple testing
* Merges all NMF Composites into one single project
* Migrates all COM timestamps from FineTime to Time (millisecond precision)
* Merges the Common services (Directory, Login) into the COM, removing the separate Common area
* Removes the ActivityTracking service from COM; action execution progress is now tracked via the Action service's `monitorExecution` PUB-SUB operation and `ExecutionStatus` COM objects
* Adds the NMF Bootloader: a primary → secondary → factory baseline fallback ladder with JAR integrity checks and a Boot Report
* Adds a Docker-based deployment path: a `docker-containers` apps-isolation mode and a Docker image build for the barebone mission

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

