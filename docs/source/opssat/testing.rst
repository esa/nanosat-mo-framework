===============================================
Testing your app in an OPS-SAT-like environment
===============================================

.. contents:: Table of contents
    :local:

.. warning::
   The `NMF Mission OPS-SAT repository <https://github.com/esa/nmf-mission-ops-sat>`_ may no longer be actively maintained.
   The instructions below are kept for reference but may be outdated.

Testing an app with the NMF SDK is the fastest way to confirm that its functional features behave as expected.
However, certain issues may only manifest on a real satellite, where a space link connects the ground software (the CTT during development) to the app.
To detect such issues early, it is recommended to test the app in a semi-authentic environment.

Obtaining the Ground MO Proxy for OPS-SAT
-----------------------------------------
The Ground MO Proxy is an application that runs in the ground segment during nanosatellite operations.
Its main purpose is to translate MALTCP packets (sent over the local network) into MALSPP packets, which can be transmitted over the space link.
The Ground MO Proxy also provides a Directory Service that is synchronised with the one exposed by the on-board Supervisors.
In summary, the Ground MO Proxy receives requests destined for the on-board apps, forwards them to the spacecraft, and routes them to the appropriate destination.

If the previous chapter has been followed and the app is already packaged for deployment on OPS-SAT, the Ground MO Proxy source code is already available locally.
From the root of the ``nmf-mission-opssat`` repository (with the latest version branch checked out, e.g. ``v5.0``), run ``mvn install -Pground``.
This adds two components to the ``home/nmf/`` directory under ``opssat-package/nmf/target/nmf-ops-sat-VERSION/``: the Supervisor with simulator (already familiar from the SDK) and the Ground MO Proxy for OPS-SAT.

Preparing the test directories
------------------------------
By default, the app and the NMF are packaged separately, because the two are never installed together; this avoids producing unnecessarily large packages.
To make the OPS-SAT NMF Supervisor discover the app, place it into ``opssat-package/nmf/target/home/expXYZ/``, where ``XYZ`` is replaced with the experimenter ID.
This folder can be copied from ``opssat-package/experiment/target/experiment-package/home/``.

By default, the app's ``provider.properties`` contains the ``helpertools.configurations.provider.app.user`` property.
For stand-alone testing, this property should be removed unless the corresponding users have been created in the test system.
For packaging targeting the satellite EM FlatSat or FM Flight Model, the property must be present.

Running tests
-------------
With the environment prepared, testing can begin. This section describes the standard startup procedure and how to connect to the app.

Starting the NMF
""""""""""""""""
First, start the Ground MO Proxy.
Open a shell in ``opssat-package/nmf/target/nmf-ops-sat-VERSION/home/nmf/ground-mo-proxy`` and execute the ``ground-mo-proxy.sh`` script.
The warning regarding the link to the spacecraft is expected at this stage, as the Supervisor has not yet been started and the Ground MO Proxy therefore has nothing to synchronise with.
Next, start the Supervisor — but only after the Ground MO Proxy's Directory Service has been initialised, since the CTT (or EUD4MO) cannot connect to the Ground MO Proxy until then.
The Directory Service is ready when a URI of the form ``maltcp://<host>:<port>/ground-mo-proxy-Directory`` is printed.

The CTT distributed with the SDK is universal and does not need a mission-specific variant, provided that a Ground MO Proxy is in place.

Two Supervisor configurations are available:

* For verifying that the app starts and that parameters can be set, the standard OPS-SAT Supervisor is sufficient and faster. Note, however, that it does not initialise the payload interfaces, and the Platform services will therefore be non-functional. The OPS-SAT Supervisor path is ``opssat-package/nmf/target/nmf-ops-sat-VERSION/home/nmf/supervisor/``.
* To exercise the Platform services, start the OPS-SAT hybrid Supervisor with simulator.
  The Supervisor with simulator takes significantly longer to start, as it must initialise the Orekit library used for orbit and attitude propagation.
  The OPS-SAT Supervisor with simulator path is ``opssat-package/nmf/target/nmf-ops-sat-VERSION/home/nmf/supervisor-sim/``.
* The hybrid simulator can be configured by editing ``platformsim.properties`` in the ``supervisor-sim`` working directory. Each adapter can be configured to use either a real or a simulated payload implementation; additional configuration options are documented in the file.

Starting and connecting to the app
""""""""""""""""""""""""""""""""""
With both the Supervisor and the Ground MO Proxy running, the CTT can be started and pointed at the Ground MO Proxy by entering its Directory Service URI in the ``Communication Settings`` tab.
The Supervisor will then appear in the ``Providers List`` and can be connected to.
The subsequent steps mirror those used for testing in the SDK: open the ``Application Launcher`` tab in the Supervisor and start the app.
Return to the ``Communication Settings`` tab and click ``Fetch Information``; the app will now appear in the ``Providers List``.

.. note::

   The Ground MO Proxy may take a short time to synchronise its Directory entries with the Supervisor. If the app does not appear immediately after clicking ``Fetch Information``, wait approximately ten seconds and retry.

Once connected, the app can be tested using the same procedure as in the SDK.
