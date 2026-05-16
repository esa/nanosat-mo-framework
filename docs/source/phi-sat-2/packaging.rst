=====================================
Generating an NMF Package for ɸ-Sat-2
=====================================

.. contents:: Table of contents
    :local:

Once an NMF app has been implemented, it can be distributed as an NMF Package by adding the ``nmf-package-maven-plugin`` to the project and rebuilding it.

Step 1: Add the plugin to the project
-------------------------------------
Add the following profile to the project's ``pom.xml``:

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
                                    <mainClass>${assembly.mainClass}</mainClass>
                                    <libs>
                                        <lib>${basedir}/ai-model</lib>
                                        <lib>${basedir}/demo-tiles</lib>
                                    </libs>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>

Step 2: Configure the plugin
----------------------------
Set the ``<mainClass>`` element to the fully qualified name of the app's entry point (for example, ``esa.mo.nmf.apps.EdgeAIApp``).
Add or remove entries in the ``<libs>`` section to control which additional files or folders are bundled into the NMF Package.

Step 3: Generate the NMF Package
--------------------------------
Build the project with:

``mvn clean install -Pgenerate-nmf-package``

The generated NMF Package will be located in the project's ``target/`` directory.
