=========================
Configuring the simulator
=========================

.. contents:: Table of contents
    :local:

Configuring the simulator is pretty easy and can greatly improve your experience.
The simulator is already built into the Supervisor Sim, so the first time you start the NMF Supervisor Sim, the simulator will generate a set of configuration files with their default values in its working directory.

General configuration options
-----------------------------
These are the major choices you can make when configuring the simulator.

- Start the processing of internal models
- Increment simulated time
- Realtime factor
- Kepler elements of orbit
- Whether to use Orekit library
- Update GPS constellation TLEs from Internet
- Enable Celestia visualisation and the port to use
- Start and end date of simulation
- Logging levels

Platform configuration options
------------------------------
You have several options to configure the simulator's platform services.

- Platform simulation mode (sim or hybrid): Shall all spacecraft data come from simulation or shall some components (e.g. camera) come from real hardware?
- x.adapter where x is from {camera, optrx, gps, iadcs, sdr, power}: Which adapter class should be used (simulated or real OPS-SAT adapter for real hardware)? Depends on the simulation mode.
- camerasim.imagemode: Random/Fixed. Should the camera simulator provide the same image all the time or a random image from a folder?
- camerasim.imagefile: Absolute path to the image that shall be used if camerasim.imagemode is 'Fixed'.
- camerasim.imagedirectory: Absolute path to the folder that shall be used if camerasim.imagemode is 'Random'.

Configuring the simulator through files
---------------------------------------
If you run the supervisor with simulator for the first time, it will generate a set of configuration files in its working directory.
In addition, it will unpack resources and start logging to a directory named `.ops-sat-simulator` in your user's home directory.
The ones which are most interesting to you are ``_OPS-SAT-SIMULATOR-header.txt`` and ``platformsim.properties``.
The first file manages the general configuration options above while the second one manages the platform configuration options.
Both of them follow Java properties syntax, so to change an option you just need to change the value on the right hand side of the assignment.
Just make sure that the files need to reside in the simulators execution directory.

Configuring the simulator through a UI
--------------------------------------
The simulator is in essence a TCP server which you can run on your local machine or a remote machine.
The OPS-SAT spacecraft simulator comes with a client GUI application, through which you can connect to a running simulator, send commands, but more importantly, change the configuration of the simulator.

Running the UI
^^^^^^^^^^^^^^
To run the simulator GUI from Netbeans, right click the ``ESA OPS-SAT - Spacecraft Simulator`` project and select ``Run``. When prompted for the main class, select ``opssat.simulator.main.MainClient``.
This will launch the client UI.

To run the simulator UI from Eclipse, import the ``SimClient`` launch configuration, provided in ``sdk/launch-configs`` and change the variables like you did for the supervisor and the CTT.
After that, you can just run the project by executing this run configuration.

Connecting to the simulator
^^^^^^^^^^^^^^^^^^^^^^^^^^^
If the server and client are running on the same machine, you don't need to do anything as the client will automatically connect to localhost.
If you are running the simulator on a separate machine, you can enter the IP address and port (11111 by default) in the center of the window. However, it is recommended to run the server and client on the same machine if your machine has the resources to do that.

General configuration
^^^^^^^^^^^^^^^^^^^^^
After connecting to the server, you can find some configuration options, like realtime factor and start of simulation, at the top part of the window.
Furthermore, clicking the button ``Edit Header`` opens a window in which you can enter the same information as before and additionally the start date and the end date of the simulation.
By hitting the button ``Submit to server`` this information is transmitted and the server configuration is updated.

Platform configuration (WIP)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
As of now, the only configurable platform service is the camera service. To configure it, you just have to switch to the ``Camera Settings`` tab.
There you can select if you want to use a fixed image or a random image from some directory, the path to the image/directory and submit your changes and view the current settings.

.. note:: 

   If you are running the simulator on your local machine and click the ``Browse`` button, a window to your local file explorer will open.
   If you are running the simulator on a remote server, you have to make sure that you have SFTP enabled on the server, so a rudimentary SFTP file explorer can open.
