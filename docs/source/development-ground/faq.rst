===
FAQ
===

.. contents:: Table of contents
   :local:

How do I convert between Java primitives and MAL data types?
------------------------------------------------------------

See the App Development Guide FAQ — the mapping is the same on both sides.

How do I change the transport layer?
------------------------------------

The transport is selected through ``transport.properties`` in the working
directory. For ground applications, the default is ``maltcp``. To talk to a space provider through SPP, use a
Ground MO Proxy (see :doc:`ground-mo-proxy`) rather than reconfiguring the ground application to speak SPP
directly.

The CTT does not see my app — what's wrong?
-------------------------------------------

Common causes:

- The Supervisor has not yet started its Directory Service. Wait until the Supervisor prints its Directory
  Service URI before connecting.
- The app has not yet registered. Confirm the app was started via ``AppsLauncher.runApp`` and that its process
  did not exit.
- The Directory Service URI in the CTT does not match the Supervisor's output.
- A Ground MO Proxy is in the path and has not yet synchronised — wait ten seconds and refresh.

How do I subscribe to multiple parameters?
------------------------------------------

Build a single ``Subscription`` that lists the parameter identifiers of interest, then call
``monitorValueRegister`` once with that subscription. The framework demultiplexes incoming updates back to the
adapter callback. Subscribing once is more efficient than subscribing per parameter.

Where do my consumer logs go?
-----------------------------

Ground application logging uses ``java.util.logging`` like the space side; see the App Development Guide's
logging page. By default, output goes to the console of the JVM process; configure ``logging.properties`` to
redirect to a file when needed.
