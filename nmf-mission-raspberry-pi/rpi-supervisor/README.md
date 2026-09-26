NanoSat MO Supervisor: Raspberry Pi
============

The NanoSat MO Supervisor of the Raspberry Pi mission. It extends the generic NanoSat MO Supervisor of the NMF Core, so most of its behaviour comes from there.

It provides the Platform services of `rpi-platform-impl`. Two of them are backed by an adapter: the GPS service, which takes its data from the software simulator, and the Camera service, which returns a single stored image (`CameraSingleImageAdapter`). The module also contains an adapter for a USB webcam (`CameraRaspberryPiAdapter`), which is not used by default.

Its Monitor and Control adapter (`MCRaspberryPiAdapter`) adds the following Parameters:
* System.CurrentPartition: the partition that the operating system runs from.
* Linux.Version: the output of `uname -a`.
* App.Geofence: the geofences that start or stop Apps depending on the position of the spacecraft. Setting it adds or removes a geofence, for example `ADD:app1:40.123456:50.123456:100.5:true`.

And the following Actions:
* System.SetTimeUsingDeltaMilliseconds: sets the system clock to the current time plus the given difference in milliseconds.
* System.Reboot: reboots the Raspberry Pi.
* GPS_Sentence: accepted, but does nothing on this platform.
