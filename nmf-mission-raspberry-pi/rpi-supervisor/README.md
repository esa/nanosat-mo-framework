NanoSat MO Supervisor: OPS-SAT
============

The NanoSat MO Supervisor application acts as a supervisor to be deployed on the NanoSat segment of OPS-SAT. It extends the generic NanoSat MO Supervisor of the Java implementation presented in chapter 3 and therefore most of the default behavior of the component is already present.

It includes the Platform services implementation from the NMF Core implementation with the additional adapters for the OPS-SAT platform devices.

The Platform services can be consumed from both the MAL-SPP transport binding and the MAL-TCP/IP transport binding. The former is intended to be used between the NanoSat and the Ground segments, while the latter is intended to be used by IPC between NMF Apps and the NanoSat MO Supervisor.

A dedicated adapter for the monitoring and control was implemented. The getters and setters for the Parameter service were implemented for a few set of parameters and the correct dispatch of actions for certain method calls was also implemented.

Three parameters were defined with the following names and respective descriptions:
* CurrentPartition: "The Current partition where the OS is running."
* LinuxVersion: "The version of the software."
* CANDataRate: "The data rate on the can bus."


Three actions were defined with the following names and respective descriptions:
* GPS_Sentence: "Injects the NMEA sentence identifier into the CAN bus."
* Reboot_MityArm: "Reboots the mityArm."
* Clock.setTimeUsingDeltaMilliseconds: "Sets the clock using a diff between the on-board time and the desired time."



