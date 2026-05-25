==========================
NMF on the ɸ-Sat-2 mission
==========================

.. contents:: Table of contents
   :local:

The NMF is the default software framework for app developers on
`ɸ-Sat-2 <https://en.wikipedia.org/wiki/Phi-Sat-2>`_. The mission
combines a multispectral imaging payload with on-board AI inference
through the Movidius VPU.

This page describes the mission-specific aspects of deploying apps
to ɸ-Sat-2.

Packaging for ɸ-Sat-2
---------------------

The standard NMF Package mechanism (see
:doc:`../development-app/packaging`) is used; the mission-specific
addition is the inclusion of AI models and tile data when relevant.

Configure the ``nmf-package-maven-plugin`` to bundle the model and
data folders:

.. code-block:: xml

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

Build with:

.. code-block:: bash

   mvn clean install -Pgenerate-nmf-package

The resulting ``.nmfpackage`` is in the project's ``target/`` directory.

AI inference
------------

ɸ-Sat-2 runs the **ArtificialIntelligence** Platform service backed by
the ``AIMovidiusAdapter``, which targets the on-board Intel Movidius
VPU. See
:doc:`../development-app/platform-services/artificial-intelligence`
for the consumer-side API.

App chaining (cloud-tile filtering)
-----------------------------------

ɸ-Sat-2 demonstrated **app chaining** for image processing. Two apps
collaborate via the Directory Service:

1. The first app classified each tile of a captured image as cloudy or
   clear.
2. The second app subscribed to the classification output and
   processed only the clear tiles, skipping cloudy ones to save
   downlink bandwidth and compute.

This pattern is described in general terms in
:doc:`../concepts/apps-and-supervisor` and
:doc:`../development-app/app-chaining`.
