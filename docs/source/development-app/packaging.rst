=========
Packaging
=========

.. contents:: Table of contents
   :local:

NMF Apps are distributed as **NMF Packages** — ``.nmfpackage`` ZIP archives produced by the
``nmf-package-maven-plugin``. For the package format and lifecycle, see :doc:`../concepts/packages`. This page
covers the build-side configuration.

Adding the plugin to your app
-----------------------------

Add the following to the app's ``pom.xml``:

.. code-block:: xml

   <build>
     <plugins>
       <plugin>
         <groupId>int.esa.nmf.core</groupId>
         <artifactId>nmf-package-maven-plugin</artifactId>
         <executions>
           <execution>
             <phase>package</phase>
             <goals>
               <goal>generate-nmf-package</goal>
             </goals>
             <configuration>
               <mainClass>esa.mo.nmf.apps.MyApp</mainClass>
               <libs>
                 <!-- Additional files or folders to bundle. -->
                 <!-- <lib>${basedir}/calibration-data</lib> -->
               </libs>
             </configuration>
           </execution>
         </executions>
       </plugin>
     </plugins>
   </build>

Configuration
-------------

- ``<mainClass>`` — the fully qualified name of the app's entry point (the class containing
  ``main(String[])``).
- ``<libs>`` — paths to additional files or directories to bundle with the JAR. Use this for models, lookup
  tables, calibration data, or any resource the app needs at runtime that is not on the classpath.

Building a package
------------------

The package is built as part of the normal build:

.. code-block:: bash

   mvn clean install

The resulting ``<app-name>-<version>.nmfpackage`` is produced in the project's ``target/`` directory. Inspect
it with any ZIP utility.

The metadata file
-----------------

Every package contains ``package-metadata.properties`` at its root with package name, version, type, creation
timestamp, and one entry per bundled file with its installed path and CRC. The Supervisor's
``PackageManagement`` service verifies these CRCs before installing.

See :doc:`../concepts/packages` for the metadata schema and the in-archive directory layout.

Distribution
------------

The ``.nmfpackage`` file is the deliverable; how it reaches the spacecraft is mission-specific. See
:doc:`../mission-integration/index` for mission-specific guidance.

Linux deployment artefacts
--------------------------

For initial hardware deployment, the ``nmf-linux-maven-plugin`` generates a Linux filesystem layout and a
``fresh_install.sh`` script. This is used to seed a new spacecraft host with an NMF installation before any
apps are installed via ``PackageManagement``. The plugin is configured similarly to the package plugin; see
the mission-specific deployment pages for examples.
