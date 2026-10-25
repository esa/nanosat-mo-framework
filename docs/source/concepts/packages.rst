============
NMF Packages
============

.. contents:: Table of contents
   :local:

An **NMF Package** is the deployment unit used to distribute software to a spacecraft. It is a ZIP archive
with the ``.nmfpackage`` extension that bundles an app's JAR (or an NMF update) together with the metadata
required by the Supervisor's ``PackageManagement`` service to install and verify it.

Package types
-------------

Six package types are supported, distinguished by the ``info.type`` field in the package metadata:

- ``app`` — an NMF App. The package contains the app's JAR, any additional libraries, and resources required
  at runtime.
- ``dependency`` — a shared library, installed once and shared by multiple apps.
- ``java`` — a Java runtime update.
- ``mission`` — an update to the mission-specific NMF layer.
- ``nmf`` — an update to the NMF Core itself.
- ``delta`` — a partial update relative to an existing package.

Most users will only create ``app`` packages. The other types are used for system maintenance.

Package structure
-----------------

A package is a ZIP archive whose entries are placed under deployment directories that match where the files
will land once installed:

============================== =================================================
Package type                   In-archive directory
============================== =================================================
``app``                        ``apps/<package-name>/...``
``dependency``                 ``jars-shared-libraries/...``
``java``                       ``java/...``
============================== =================================================

Every package also contains a ``package-metadata.properties`` file at the archive root.
It declares the package name, version, type, creation timestamp, metadata schema version,
and one entry per included file with its installed path and SHA-256 checksum:

.. code-block:: properties

   info.name=my-app
   info.version=1.0.0
   info.type=app
   info.creation-timestamp=2026-05-16T10:00:00Z
   info.metadata-version=6
   zipped.file.count=3
   zipped.file.path.0=apps/my-app/my-app-1.0.0.jar
   zipped.file.sha256.0=9f86d081884c7d65...
   zipped.file.path.1=apps/my-app/start_my_app.sh
   zipped.file.sha256.1=60303ae22b998861...
   zipped.file.path.2=apps/my-app/provider.properties
   zipped.file.sha256.2=fd61a03af4f77d87...

The checksums are checked at install time,
so the Supervisor installs only the files that the metadata lists, unchanged.
They do not prove who made the package: whoever makes a package also writes its metadata.

Packages written before metadata version 6 list a CRC-32 per file (``zipped.file.crc.<n>``) instead.
They are still accepted and checked by their CRC-32,
which catches accidental corruption but not a deliberate change.

Package lifecycle
-----------------

.. mermaid::

    flowchart LR
        Build[Build<br/>Maven plugin]
        Dist[Distribute<br/>.nmfpackage file]
        Install[Install<br/>PackageManagement]
        Run[Run<br/>AppsLauncher]
        Uninstall[Uninstall<br/>PackageManagement]
        Build --> Dist --> Install --> Run
        Run --> Uninstall

**Build.** Packages are produced by the ``nmf-package-maven-plugin`` during the app's Maven build. See the App
Development Guide for plugin configuration.

**Distribute.** The resulting ``<name>-<version>.nmfpackage`` file is delivered to the spacecraft through
whatever mission-specific distribution path applies (file uplink, ground network, etc.).

**Install.** The Supervisor's ``PackageManagement`` service handles installation.
It verifies the metadata file, checks each entry's SHA-256 against the value declared in metadata,
and extracts files to their target locations under ``NMF_HOME``.
An install or update can also carry the SHA-256 that the package file is expected to have
(see `Checking the package file against ground`_ below).

**Run.** Once installed, an app is started via ``AppsLauncher.runApp`` (see :doc:`lifecycle`).

**Uninstall.** ``PackageManagement.uninstall`` removes the installed files and the package registration.
``update`` performs uninstall + install of a newer version in one step.

Checking the package file against ground
----------------------------------------

The checksums inside a package only prove that its files are the ones its metadata lists:
whoever makes a package also writes its metadata.
To make sure that the Supervisor installs exactly the package file that was approved on ground,
the ``install`` and ``update`` requests carry, for each package file, the SHA-256 it is expected to have:

1. On ground, compute the SHA-256 of the ``.nmfpack`` file, for example with ``sha256sum``.
2. Send it with the request, for example ``software-management install <package> --sha256 <checksum>``
   with the CLI.
3. The Supervisor computes the SHA-256 of the package file it holds and refuses the request if they differ.

The ``checkPackageIntegrity`` operation returns the SHA-256 of a package file on board,
so that ground can also compare it before deciding to install.

The Consumer Test Tool works this way:
on install and update, it shows the SHA-256 of each package file on board
and asks the operator to confirm that it is the one of the package file approved on ground.
It sends the confirmed checksum with the request,
so that the Supervisor refuses a package file that changed since.
The confirmation protects only if the operator compares the two.

A checksum that is sent is always checked.
Whether one is *required* is chosen by the mission when it generates its filesystem,
with the ``requirePackageChecksum`` parameter of the ``nmf-linux-maven-plugin``:

.. code-block:: xml

   <configuration>
     <requirePackageChecksum>false</requirePackageChecksum>
   </configuration>

It is ``true`` by default, so that a request without a checksum is refused.
A filesystem for development or simulation may set it to ``false``,
as the SDK playground environment and the testbeds do.
A Supervisor started without the setting, for example from an IDE, also requires a checksum.
The choice is recorded as ``require-package-checksum`` in ``bootloader/config.properties``,
which the bootloader passes to the Supervisor at startup.

The checksum protects the package file, not the request:
the requests that reach the Supervisor still have to be authenticated,
by the mission's link security or by access control.

Difference from a plain JAR
---------------------------

A bare JAR contains compiled classes and resources. An NMF Package adds:

- A **metadata descriptor** with versioning, type, and a SHA-256 checksum for each file.
- A **deployment layout** that places files in the correct directories (``apps/``, ``jars-shared-libraries/``,
  etc.) without manual filesystem operations.
- A **manifest of contents** that the Supervisor checks before installing the package.
- The ability to bundle **non-JAR artefacts** alongside the JAR — start scripts, property files, ML models,
  lookup tables, calibration data.

These features let ``PackageManagement`` install, update, and remove software on a spacecraft
transactionally, which a bare JAR cannot support.

References
----------

- :doc:`mo-architecture` — the ``PackageManagement`` service in the SM category.
- App Development Guide — how to configure the Maven plugin to produce a package for your app.
