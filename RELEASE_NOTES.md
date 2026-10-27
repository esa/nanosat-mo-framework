ESA NanoSat MO Framework - Release Notes
========================

These Release Notes include a short summary of the updates done for each version.
The different versions and respective updates are the following:

### Version 6.0 (in development)
* Upgrades to CCSDS MO services v15.0, whose consumer stubs throw the declared error classes instead of MALInteractionException
* Requires Java 21 or later
* Removes the automatic serialization of objects into parameter and action values: values must now be MO attribute types, which closes an unsafe Java deserialization vulnerability (NMF-2026-002)
* Adds access control to the Supervisor: ground and each App must present a credential, and an App may call only a baseline of services plus the permissions it declares and ground acknowledges, which closes unauthenticated command execution through the CommandExecutor (NMF-2026-010)
* Adds the accessControl option of the nmf-linux-maven-plugin (off, audit or enforce), with enforce as the default; the playground and the testbeds use audit
* Changes the install and update operations of the Package Management service to take a list of PackageApproval, holding the checksum and the acknowledged permissions of each package
* Stores each refused or audited call in the COM Archive as an UnauthorisedCall object of the Apps Launcher service
* Requires by default that install and update carry the SHA-256 of each package file, computed on ground, so that only the exact package file approved there is used; the playground and the testbeds turn it off with the requirePackageChecksum option of the nmf-linux-maven-plugin
* Fixes runApp starting an App more than once when requests for it arrive together, or when a request names it twice, which left the Supervisor tracking a process that had already ended
* Fixes the percentage filter of the Aggregation service, which never reported a change of a parameter whose previous value was negative
* Changes the socket between the spacecraft simulator and its GUI from Java serialization to JSON, so that no Java object is deserialized on it, and fixes the GUI losing its connection when it changed a camera setting or the header
* Adds testbed-simulator, which runs the spacecraft simulator and checks what its clients receive
* Fixes an App being able to take over the Supervisor's entry in the Directory service, so that ground and the other Apps looked it up instead of the Supervisor (NMF-2026-011): an App may now add and remove only its own entry, no caller may add or remove the Supervisor's, and an App cannot be named nanosat-mo-supervisor; each refused call is stored as an UnauthorisedCall with the entry it named
* Fixes uninstall and update removing the files of an App that was still running, either started outside the Apps Launcher or not closing when asked: they now fail with the new App Running error of the Software Management area, and no package of the request is changed
* Fixes OS command injection through the name of an App package in the Linux launcher (NMF-2026-004): a package name must now be a plain file name
* Confines each package to its own area of the NMF root, so that an App package can no longer add, replace or delete the files of another App or of the software baseline (NMF-2026-006)
* Creates the nmf-admin account locked, with no password, and limits its sudo rights to the App accounts (NMF-2026-007)
* Fixes OS command injection through the launch values of an App package (NMF-2026-008): its main class, jars and heap sizes are now checked and quoted
* Fixes OS command injection through the model and input paths of the Artificial Intelligence service (NMF-2026-009)
* Checks each file of an NMF Package by its SHA-256 instead of its CRC-32, from package metadata version 6, and makes the integrity check of the Package Management service real, as it always answered true before
* Renames the upgrade operation of the Package Management service to update, and its PackageUpgraded COM object to PackageUpdated
* Removes the Configuration service: its ConfigurationService and ConfigurationProvider COM objects are now objects 2 and 3 of the Directory service
* Removes the source link of the COM objects, together with the ObjectLinks structure: ArchiveDetails now has a relatedLink field
* Changes ParameterStatusListener.onGetValue(Identifier, AttributeType) to onGetValue(Identifier): Apps that implement it must update the signature, because without @Override the old method silently stops being called
* Removes ParameterStatusListener.onGetValue(Long), so parameter values are asked for by name only; Apps that use the annotations are not affected
* Removes the deprecated pushParameterValue(Identifier, ParameterValue, ObjectKey, Time) of the Parameter service; use pushSingleParameterValueAttribute instead
* Removes the deprecated MonitorAndControlNMFAdapter.getTypeShortForm; use HelperAttributes.getTypeShortForm of the MO library instead
* Merges nmf-package-lib into nmf-services-impl: the nmf-package-lib artifact is no longer published, so projects that used it depend on nmf-services-impl instead, with the same Java packages
* Moves the simulator code from the opssat.simulator packages to esa.mo.nmf.mission.orekit.simulator and esa.mo.nmf.mission.lite.simulator, and the Orekit Supervisor from esa.mo.nmf.sim.supervisor to esa.mo.nmf.mission.orekit.NanosatMOSupervisorBasicImpl, which is the main class to give when setting a 6.0 baseline
* Removes the OPS-SAT leftovers from the code and the READMEs, and renames the add-on folder of the Celestia visualisation to extras/cubesat
* Builds the MCP adapter with the rest of the SDK again, as a prototype
* Fixes the stopApp crash when an App is killed at the same time
* Fixes the Geofence of the Raspberry Pi mission, which never started the App it names
* Gives nmf-admin and the App accounts a home directory on Debian and Ubuntu
* Changes the Raspberry Pi guide to copy the NMF with an SSH key for root, instead of a root password
* Removes the unused stop_app.sh launch path of the Apps Launcher, which was meant for non-NMF Apps on OPS-SAT
* Removes the unfinished package signing code, which was never called
* Removes the Facebook ground example

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

