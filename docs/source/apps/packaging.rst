======================
Packaging your NMF app
======================
Now that you finished implementing your NMF app, you want to make sure it works properly.
Apart from unit tests over some methods, one way to test is to just run your app and connect to it through the CTT.
The recommended way of running an app is through the NMF supervisor. 
The easiest way to achieve that, is to package your app with the SDK. For this we have to look at several files.

.. contents:: Packaging requirements

SDK Package POM
---------------
The first file we have to change is the ``pom.xml`` in the folder ``sdk/sdk-package``. First, add your app to the dependencies.

.. code-block:: xml
   :linenos:

   <dependency>
     <groupId>int.esa.nmf.sdk.examples.space</groupId>
     <artifactId>sobel</artifactId>
     <version>${project.version}</version>
   </dependency>

After that, we have to make sure that the different properties files needed by the NMF are present in the app's execution directory.
This is done in an execution of the ``Maven Antrun Plugin``. Add a copy task with the execution folder of your app as the *todir*.

.. code-block:: xml
   :linenos:

   <copy todir="${esa.nmf.sdk.assembly.outputdir}/home/sobel">
     <fileset dir="${basedir}/src/main/resources/space-common"/>
     <fileset dir="${basedir}/src/main/resources/space-app-root"/>
   </copy>

That is all you need to do here! Easy, right?

Build.xml
---------
The next step is to update the ``sdk/sdk-package/antpkg/build.xml``. This is an Ant script which is called by the same plugin that copies the properties files.
In principle, it works like a Makefile in C. We have a top level target which is execution through the Maven Antrun Plugin and this target depends on several subtargets.
Our task in this file is to create such a subtarget for our app and add this target to the dependency list of *build*.

The subtarget should look like this:

.. code-block:: xml
   :linenos:

   <target name="emit-space-app-sobel">
     <ant antfile="antpkg/build_shell_script.xml">
       <property name="mainClass" value="esa.mo.nmf.apps.SobelApp"/>
       <property name="id" value="start_sobel"/>
       <property name="nmf_home" value="`cd ../nmf > /dev/null; pwd`"/>
       <property name="nmf_lib" value="`cd ../nmf/lib > /dev/null; pwd`"/>
       <property name="binDir" value="sobel"/>
     </ant>
     <ant antfile="antpkg/build_batch_script.xml">
       <property name="mainClass" value="esa.mo.nmf.apps.SobelApp"/>
       <property name="id" value="start_sobel"/>
       <property name="nmf_home" value="%cd%\..\nmf"/>
       <property name="nmf_lib" value="`cd ../nmf/lib > /dev/null; pwd`"/>
       <property name="binDir" value="sobel"/>
     </ant>
   </target>

Note that the target name can be anything which is not already in use. We just use this name later to add the dependency.
The ``id`` property's value has to have the prefix "start_", so it can be recognised by the supervisor.
The property ``mainClass`` contains the fully qualified name for the class in our app containing the ``main`` methods.

The last thing left to do is to add the subtarget to the dependencies:

.. code-block:: xml
   :linenos:

   <target name="build" depends="emit-ctt, emit-simulator-gui, emit-space-supervisor, emit-space-app-all-mc-services,
     emit-space-app-publish-clock, emit-space-app-camera, emit-space-app-benchmark, emit-space-app-payloads-test, emit-space-app-waveform, emit-space-app-sobel">
     <!--This empty target is used as the top level target. Add your app targets to the depends attribute! -->
   </target>

Now the last thing left to do is :doc:`build`!
