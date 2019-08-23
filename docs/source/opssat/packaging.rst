============================================
Packaging your app for deployment on OPS-SAT
============================================
Packaging your app for testing on the ground is slightly different from testing your app on a satellite or a flat-sat.
In this tutorial you will learn how to package your app for deployment on OPS-SAT or get it ready for flat-sat tests.

Getting the right files
-----------------------
This packaging process is almost completely automated for you. You only need to clone a repository, change some configuration files and run maven. So, let's jump into it!

1. Clone the `NMF Mission OPS-SAT repository <https://github.com/esa/nmf-mission-ops-sat>`_.
2. Checkout the ``dev`` branch to get the latest version.
3. Open the ``pom.xml`` file in the ``opssat-package`` folder.
4. In the exp profile, edit your experimenter ID and the Maven information for your app. Make sure that ``expVersion`` matches the version defined in your app's POM.

.. code-block:: xml
   :linenos:
   :emphasize-lines: 4,5,9,10,11

   <id>exp</id>
   <properties>
     <isExp>true</isExp>
     <expId>exp1337</expId>
     <expVersion>2.0.0-SNAPSHOT</expVersion>
   </properties>
   <dependencies>
     <dependency>
       <groupId>int.esa.nmf.sdk.examples.space</groupId>
       <artifactId>publish-clock</artifactId>
       <version>${expVersion}</version>
     </dependency>
   </dependencies>

.. note::

   If your app requires additional dependencies, you can add them in the same manner as the app itself. Just add it as a ``<dependency>`` inside the ``exp`` profile.

5. Open the file ``copy.xml`` in the ``opssat-package`` folder. In the target ``copyExp`` edit the filter for ``MAIN_CLASS_NAME``. You can also add additional copy tasks to package additional files that your app requires. These copy tasks will be executed by the ``Maven AntRun Plugin``.

.. code-block:: xml
   :linenos:
   :emphasize-lines: 11

   <target name="copyExp">
     <exec executable="expr" outputproperty="expApid">
       <arg value="1024"/>
       <arg value="+"/>
       <arg value="${expId}"/>
     </exec>
     <copy todir="${esa.nmf.mission.opssat.assembly.outputdir}/experimenter-package/home/exp${expId}/">
       <fileset dir="${basedir}/src/main/resources/space-common"/>
       <fileset dir="${basedir}/src/main/resources/space-app-root"/>
       <filterset>
         <filter token="MAIN_CLASS_NAME" value="esa.mo.nmf.apps.PayloadsTestApp"/>
         <filter token="APID" value="${expApid}"/>
         <filter token="NMF_HOME" value="`cd ../nmf > /dev/null; pwd`"/>
         <filter token="NMF_LIB" value="`cd ../nmf/lib > /dev/null; pwd`/*"/>
       </filterset>
       <firstmatchmapper>
         <globmapper from="startscript.sh" to="start_exp${expId}.sh"/>
         <globmapper from="*" to="*"/>
       </firstmatchmapper>
     </copy>
     <chmod dir="${esa.nmf.mission.opssat.assembly.outputdir}" perm="ugo+rx" includes="**/*.sh"/>
   </target>

6. Invoke ``mvn clean install -Pexp``. In the folder ``target/nmf-opssat-VERSION/experimenter-package/`` you will find the directory structure that you need to package your app as an IPK for OPS-SAT. 
