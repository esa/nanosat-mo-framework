NMF Packages
============

.. contents:: Table of contents
    :local:

An **NMF Package** is the standard distribution format for deploying NMF Apps onto a spacecraft.
It is a ``.nmfpackage`` file that bundles your app's JAR together with any additional resources (libraries, models, data files) needed at runtime.
The Supervisor's **PackageManagement** service handles installation and uninstallation of NMF Packages on the target system.

Generating an NMF Package
--------------------------

NMF Packages are produced by the ``nmf-package-maven-plugin``. Add the following profile to your app's ``pom.xml``:

.. code-block:: xml
   :linenos:

    <profiles>
        <profile>
            <id>generate-nmf-package</id>
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
                                    <mainClass>your.app.MainClass</mainClass>
                                    <libs>
                                        <!-- Additional files or folders to bundle -->
                                        <!-- <lib>${basedir}/my-model</lib> -->
                                    </libs>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>

Replace ``your.app.MainClass`` with the fully qualified entry point of your app.
Add any extra files or directories you need at runtime inside ``<libs>``.

Then build the package with:

.. code-block:: bash

    mvn clean install -Pgenerate-nmf-package

The resulting ``.nmfpackage`` file will be in your project's ``target/`` directory.

Installing and Uninstalling
----------------------------

Once you have a ``.nmfpackage`` file, it can be installed on the satellite via the Supervisor's **PackageManagement** service, accessible through the Consumer Test Tool (CTT) or any ground consumer.
The Supervisor manages the full lifecycle: install, uninstall, and version tracking.

For initial hardware deployment, the ``nmf-linux-maven-plugin`` can generate the Linux filesystem layout and a ``fresh_install.sh`` script.

Mission-specific examples
--------------------------

For a worked example of integrating the plugin into a mission project, see :doc:`../phi-sat-2/packaging`.
