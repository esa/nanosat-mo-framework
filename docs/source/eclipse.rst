===================================
Importing the NMF into Eclipse IDE
===================================

If you want to use another IDE than Netbeans, Eclipse is the way to go (for now). There are still some minor issues, so if you encounter any problems, feel free to post an issue on GitHub.
Recommended Eclipse version is 2019-03 although other versions should work as well.

In Eclipse, click `File -> Import... -> Maven -> Existing Maven Projects -> Next`. You can now use the button **Browse** to navigate to your NMF root directory, uncheck **Add project(s) to working set** and click **Finish**.
No import errors should occur in Eclipse 2019-03. If some occur, they most likely have the form 'Plugin execution not covered by lifecycle configuration' for some Maven project inside the NMF. It is easy to fix these errors yourself, but we would prefer it if you could let us know on the OPS-SAT experimenter platform or GitHub, so we can fix it for other people as well. Any other import errors should be reported on the experimenter platform.

Fixing the 'Plugin execution not covered by lifecycle configuration' error
--------------------------------------------------------------------------
When developing apps with Maven you may want to invoke some plugins inside your pom.xml. Occasionally, Eclipse's m2e plugin may not be able to understand when to call a certain plugin. In this case, you can add a configuration for the m2e lifecycle mapping plugin to your pom.xml. You can orientate yourself at the following example. You need one **pluginExecution** for each plugin that is not covered. The bold lines are the ones you need to change to fit your scenario.

.. literalinclude:: m2e.xml
    :language: xml
    :linenos:
    :emphasize-lines: 12-14,16,25-27,29

Setting up the supervisor with simulator
----------------------------------------
1. Click `File -> Import... -> Run/Debug -> Launch Configurations -> Next`.
2. Use the **Browse...** button to navigate to your **nanosat-mo-framework** folder, then navigate to **sdk** and select the **launch-configs** folder.
3. Import the **SupervisorSimulator.launch** file.
4. Right-click any imported project in your workspace and select `Run As -> Run Configurations...`.
5. Select `Maven Build -> SupervisorSimulator` (the configuration you just imported) and change the parameters ``exec.executable`` to point to your JDK java executable and ``exec.workingdir`` to point to the execution directory of the supervisor with simulator. Switch to the **Environment** tab and add the variable **JAVA_HOME** and set its value to your JDK installation directory.
6. In the **Common** tab check the options **Run** and **Debug** in the **Display in favorites menu** panel.
7. Apply your settings and **Run** the supervisor. The already familiar output should appear on the Eclipse console.

Setting up the CTT
------------------
1. Click `File -> Import... -> Run/Debug -> Launch Configurations -> Next`.
2. Use the **Browse...** button to navigate to your **nanosat-mo-framework** folder, then navigate to **sdk** and select the **launch-configs** folder.
3. Import the **CTT.launch** file.
4. Right-click any imported project in your workspace and select `Run As -> Run Configurations...`.
5. Select `Maven Build -> CTT` (the configuration you just imported) and change the parameters ``exec.executable`` to point to your JDK java executable and ``exec.workingdir`` to point to the execution directory of the Consumer Test Tool. Switch to the **Environment** tab and add the variable **JAVA_HOME** and set its value to your JDK installation directory.
6. In the **Common** tab check the options **Run** and **Debug** in the **Display in favorites menu** panel.
7. Apply your settings and **Run** the CTT. Its output should appear on the Eclipse console.

Troubleshooting
---------------
Maven and Eclipse are not always running smoothly together. Here are a few points to look for if you encounter any problems:

1. If a Maven build fails, try to refresh your workspace by selecting all imported projects and pressing the ``F5`` key.
2. To kill the supervisor, it is not enough to use the red stop button next to the Eclipse console. You have to kill the process manually, e.g. by using Windows Task Manager.
3. Any workspace errors apart from the "Plugin execution not covered..." can be ignored.
4. If you have any problems, write an issue on the OPS-SAT community platform!

You are now able to start the supervisor with simulator and the CTT from Eclipse by selecting the respective launch configuration from the drop-down menu next to the green **Run as...** button in the top toolbar of Eclipse. You can now look at :doc:`apps/apps`.
