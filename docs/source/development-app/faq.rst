===
FAQ
===

.. contents:: Table of contents
   :local:

How do I convert between Java primitives and MAL data types?
------------------------------------------------------------

The MAL data types live in ``org.ccsds.moims.mo.mal.structures``. The
mapping is straightforward for primitives:

============ ==============
Java         MAL
============ ==============
``boolean``  ``Boolean``
``byte``     ``Octet`` / ``UOctet``
``short``    ``Short`` / ``UShort``
``int``      ``Integer`` / ``UInteger``
``long``     ``Long`` / ``ULong``
``float``    ``Float``
``double``   ``Double``
``String``   ``String`` / ``Identifier``
``byte[]``   ``Blob``
============ ==============

The ``U*`` variants are unsigned; choose them when the value cannot be
negative.

How do I drop the COM Archive database on startup?
--------------------------------------------------

The COM Archive is backed by a SQLite database (typically
``comArchive.db`` in the Supervisor's working directory). Deleting the
file before starting the Supervisor causes a fresh archive to be
created.

How do I change the transport layer?
------------------------------------

The MAL transport is selected through ``transport.properties`` in the
Supervisor's working directory. The default is ``maltcp`` over TCP/IP.
Alternative bindings (e.g. ``malspp`` over Space Packet Protocol for
real space links) are configured through the same file; see the
mission-specific deployment pages for examples.

How do I add an external dependency to my app?
----------------------------------------------

Declare the dependency in the app's ``pom.xml`` as you would for any
Maven project. To bundle the dependency JAR into the NMF Package, add
it under ``<libs>`` in the ``nmf-package-maven-plugin`` configuration
(see :doc:`packaging`). Alternatively, deploy it once as a separate
``dependency``-type NMF Package shared across apps.

How do I run an app under a debugger?
-------------------------------------

When using NetBeans or Eclipse, start the app's main class through the
IDE's run configuration with the correct working directory and VM
options (specifically ``-Desa.mo.nmf.centralDirectoryURI=...``). See
the Tooling section for IDE setup.

For attach-mode debugging of an app launched by the Supervisor, the
Supervisor's start scripts can be modified to include the standard JVM
debug agent arguments.

Where do my app's logs go?
--------------------------

App stdout is captured by the Supervisor and republished through
``AppsLauncher.monitorExecution``. The CTT's Apps Launcher Service tab
displays this stream in real time. See :doc:`logging` for verbosity
configuration.

How do I make a parameter periodic?
-----------------------------------

Set ``generationEnabled = true`` and ``reportIntervalSeconds`` to a
non-zero value when registering (listener API) or in the ``@Parameter``
annotation (annotation API). The framework will then poll the parameter
and publish updates at that interval.
