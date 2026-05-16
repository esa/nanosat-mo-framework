=================================
Deploying your NMF app in the SDK
=================================

.. contents:: Table of contents
    :local:

After implementing an NMF app, it should be verified in a realistic environment.
In addition to unit testing individual methods, the recommended approach is to run the app and connect to it via the CTT.
The standard way to launch an app is through the NMF Supervisor, which is most conveniently achieved by deploying the app into the SDK. This requires changes to a small number of files.

Updating the SDK Execution Environment POM
------------------------------------------
The first file to update is ``pom.xml`` in the ``sdk/sdk-execution-environment`` folder. Add the app to the project's dependencies:

.. code-block:: xml
   :linenos:

   <dependency>
     <groupId>int.esa.nmf.sdk</groupId>
     <artifactId>sobel</artifactId>
     <version>${project.version}</version>
   </dependency>

Next, ensure that the property files required by the NMF are present in the app's execution directory.
This is configured through an execution of the ``Maven AntRun Plugin``. Add a copy task whose ``todir`` points to the app's execution folder:

.. code-block:: xml
   :linenos:

   <copy todir="${esa.nmf.sdk.assembly.outputdir}/home/sobel">
     <fileset dir="${basedir}/src/main/resources/space-common"/>
     <fileset dir="${basedir}/src/main/resources/space-app-root"/>
   </copy>

This concludes the changes required in the POM.

Updating ``build.xml``
----------------------
The next step is to update ``sdk/sdk-execution-environment/antpkg/build.xml``. This Ant script is invoked by the same plugin that copies the property files.
Conceptually, it behaves similarly to a Makefile: a top-level target is executed by the Maven AntRun Plugin and depends on several subtargets.
A new subtarget must be defined for the app, and added to the dependency list of the ``build`` target.

The subtarget is defined as follows:

.. code-block:: xml
   :linenos:

   <target name="emit-space-app-sobel">
     <ant antfile="antpkg/build_shell_script.xml">
       <property name="mainClass" value="esa.mo.nmf.apps.SobelApp"/>
       <property name="id" value="start_sobel"/>
       <property name="binDir" value="sobel"/>
     </ant>
     <ant antfile="antpkg/build_batch_script.xml">
       <property name="mainClass" value="esa.mo.nmf.apps.SobelApp"/>
       <property name="id" value="start_sobel"/>
       <property name="binDir" value="sobel"/>
     </ant>
   </target>

The target name may be any value that is not already in use; it is referenced later when declaring the dependency.
The ``id`` property must use the ``start_`` prefix to be recognised by the Supervisor.
The ``mainClass`` property must contain the fully qualified name of the app class that defines the ``main`` method.

Finally, add the new subtarget to the ``build`` target's dependencies:

.. code-block:: xml
   :linenos:

   <target name="build" depends="emit-ctt, emit-simulator-gui, emit-space-supervisor, emit-space-app-all-mc-services,
     emit-space-app-publish-clock, emit-space-app-camera, emit-space-app-benchmark, emit-space-app-payloads-test, emit-space-app-waveform, emit-space-app-sobel">
     <!--This empty target is used as the top level target. Add your app targets to the depends attribute! -->
   </target>

The configuration is now complete.

Deployment
----------
To deploy the app into the SDK:

1. Build the app by running ``mvn install`` from its root directory.
2. Build the SDK execution environment by running ``mvn install`` from the ``sdk/sdk-execution-environment`` directory.

The start scripts and property files for the app will then reside in ``sdk/sdk-execution-environment/target/nmf-sdk-XX.Y/home/sobel``.

The NMF Supervisor with simulator and the CTT can now be started, the Supervisor connected to, and the app launched and tested.

