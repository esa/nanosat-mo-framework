===============================================
Testing your app in an OPS-SAT-like environment
===============================================

.. contents:: Table of contents
    :local:

Testing your app with the NMF SDK is the fastest way to confirm if all functional features work.
However, there might be some problems with respect to the behaviour on a real satellite with a space link between your ground software (CTT during development) and your app.
To find these problems early on, it is recommended to test your app in a semi-authentic test setup.

Getting Ground MO Proxy for OPS-SAT
-----------------------------------
Ground MO Proxy is an application running in the ground segment during operation of the nanosatellite.
Its main purpose is to transform MALTCP packets (which you send over your network) into MALSPP packets which can be sent over a space link.
Apart from that, the Ground MO Proxy provides a directory service which is synchronized to the one of the supervisors on the satellite.
The easy way to imagine it is: It takes your requests to the apps, forwards them to the spacecraft and from there, they are distributed accordingly.

If you followed the previous chapter and already packaged your app for deployment on OPS-SAT, you already are in possession of the code for the Ground MO Proxy for OPS-SAT.
You just need to enter the root directory of the ``nmf-mission-opssat`` repository (make sure you checked out the branch ``dev``) and run ``mvn install -Pground``.
This will add two more things to your ``home/nmf/`` folder in ``opssat-package/nmf/target/nmf-ops-sat-VERSION/``.
The supervisor with simulator with which you are already familiar from the SDK and the Ground MO Proxy for OPS-SAT.

Preparing the folders for tests
-------------------------------
By default your app and the NMF are packaged separately. The reason for this is that your app and the NMF will never be installed together, so it makes no sense to make the packages unnecessarily large.
To make the OPS-SAT NMF supervisor find your app, you should put it into ``opssat-package/nmf/target/home/expXYZ/`` where you replace XYZ with your experimenter ID.
You can copy this folder from ``opssat-package/experiment/target/experiment-package/home/``.

Note that by default the application's ``provider.properties`` will contain property ``helpertools.configurations.provider.app.user``.
For stand-alone tests it is recommended to remove it, unless necessary users are created in the testing system.

Starting tests
--------------
Now that we are set up, it is time to start testing. This section covers the general startup and how to connect your app.

Starting the NMF
""""""""""""""""
The first thing you should do is start the Ground MO Proxy.
For this, open a shell in the folder ``opssat-package/nmf/target/nmf-ops-sat-VERSION/home/nmf/ground-mo-proxy`` and execute the ``ground-mo-proxy.sh`` script.
The warning stating that we should check the link to the spacecraft is completely natural at this point, since we did not start the supervisor yet. Therefore, there is no one the Ground MO Proxy could synchronize with.
The next thing to do is to start the supervisor. You should wait with this step until the Ground MO Proxy started its directory service.
This is important, as we will not be able to connect to the Ground MO Proxy through the CTT/EUD4MO as long as the directory service is not initialized properly.
You can see that the directory service is ready when you can spot a URI of the form ``maltcp://some.ip.right.here:somePort/ground-mo-proxy-Directory``.

Note that the CTT built together with the SDK is universal and does not have to come in mission flavor (as long as there is a Ground MO Proxy in between).

We have two choices concerning the start of the supervisor:

* If you want to check if your app starts up and you can set some parameters, the OPS-SAT supervisor is fine and will save you a lot of time. Although note that it will fail to initialise payload interfaces and thus platform services will not be functional. The OPS-SAT supervisor path is ``opssat-package/nmf/target/nmf-ops-sat-VERSION/home/nmf/supervisor/``
* If you want to test your app with the platform services, you can start the OPS-SAT hybrid supervisor with simulator.
  The supervisor with simulator takes significantly more time to startup since it has to initialize the Orekit library which is used for orbit and attitude propagation.
  The OPS-SAT supervisor with simulator path is ``opssat-package/nmf/target/nmf-ops-sat-VERSION/home/nmf/supervisor-sim/``
* In order to configure the hybrid simulator, you can modify the ``platformsim.properties`` file in the supervisor-sim workdir.
  Each of the adapters can be configured to either use a real or a simulated payload implementation. Look into the file for more configuration options.

Starting and connecting to your app
"""""""""""""""""""""""""""""""""""
Now that the supervisor and Ground MO Proxy are running, you are able to connect to the Ground MO Proxy directory service by starting the CTT and entering the Ground MO Proxy directory service URI in the ``Communication Settings`` tab.
After that you can connect to the supervisor which will show up in the ``Providers List``.
Now the final steps are identical to the testing of your app in the SDK. Visit the ``Application Launcher`` tab in the supervisor and start your app.
Now you can revisit the ``Communication Settings`` tab and ``Fetch Information``. Your app should now also show up in the ``Providers List``.

.. note::

   The Ground MO Proxy might take a moment to synchronize its directory entries with the supervisor. If the app does not show up immediately after clicking the "Fetch Information" button wait 10 seconds and try again.

After connecting to your app, you are free to test your app like you did with the SDK.
