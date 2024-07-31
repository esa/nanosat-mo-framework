=================================
Generate your NMF Package for ɸ-Sat-2
=================================

.. contents:: Table of contents
    :local:

Now that you finished implementing your NMF app, you want to generate an NMF Package to distribute your App.
In order to do that, you will need to add the nmf-package-maven-plugin to your project and compile it!

Step 1: Add the plugin to the project
--------------------------
Add the following profile to your project (you can copy-paste directly from here):

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
--------------------------
Modify the ``<mainClass>entry-point</mainClass>`` configuration to the entry point of your App (example: esa.mo.nmf.apps.EdgeAIApp).
Also, add or remove any additional files/folders that you want to be bundled with your NMF Package by changing the ``<libs>`` section of the plugin as presented above.

Step 3: Generate the NMF Package
--------------------------
Build your project with: ``mvn clean install –Pgenerate-nmf-package``

Your NMF Package will be in the target folder, please check if it is there!
