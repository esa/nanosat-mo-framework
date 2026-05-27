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

Every package also contains a ``package-metadata.properties`` file at the archive root. It declares the
package name, version, type, creation timestamp, metadata schema version, and one entry per included file with
its installed path and CRC checksum:

.. code-block:: properties

   info.name=my-app
   info.version=1.0.0
   info.type=app
   info.creation-timestamp=2026-05-16T10:00:00Z
   info.metadata-version=4
   zipped.file.count=3
   zipped.file.path.0=apps/my-app/my-app-1.0.0.jar
   zipped.file.crc.0=2847...
   zipped.file.path.1=apps/my-app/start_my_app.sh
   zipped.file.crc.1=9831...
   zipped.file.path.2=apps/my-app/provider.properties
   zipped.file.crc.2=4502...

The CRCs are checked at install time so the Supervisor can verify the package has not been corrupted in
transit.

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

**Install.** The Supervisor's ``PackageManagement`` service handles installation. It verifies the metadata
file, checks each entry's CRC against the value declared in metadata, and extracts files to their target
locations under ``NMF_HOME``.

**Run.** Once installed, an app is started via ``AppsLauncher.runApp`` (see :doc:`lifecycle`).

**Uninstall.** ``PackageManagement.uninstall`` removes the installed files and the package registration.
``upgrade`` performs uninstall + install of a newer version in one step.

Difference from a plain JAR
---------------------------

A bare JAR contains compiled classes and resources. An NMF Package adds:

- A **metadata descriptor** with versioning, type, and CRC information.
- A **deployment layout** that places files in the correct directories (``apps/``, ``jars-shared-libraries/``,
  etc.) without manual filesystem operations.
- A **manifest of contents** that the Supervisor can verify before trusting the package.
- The ability to bundle **non-JAR artefacts** alongside the JAR — start scripts, property files, ML models,
  lookup tables, calibration data.

These features let ``PackageManagement`` install, upgrade, and remove software on a spacecraft
transactionally, which a bare JAR cannot support.

References
----------

- :doc:`mo-architecture` — the ``PackageManagement`` service in the SM category.
- App Development Guide — how to configure the Maven plugin to produce a package for your app.
