=============================
Developing your first NMF App
=============================

.. toctree::
   :maxdepth: 1
   :caption: Contents

   apps
   main
   adapter
   parameters
   actions
   packaging
   build

Introduction
------------
Now that you are set and ready to go, it is time for you to develop your first NMF App. In this app, we will use the camera to take some pictures of the earth and apply a sobel filter to the taken image.
The idea is to take an image and keep the user updated about the state of the sobel calculation. For this, we will first use the iADCS service to point the camera to the ground and then use the camera service to take the picture. After that, we use plain Java to calculate the filtered image. On OPS-SAT, this calculation could be delegated to the FPGA and hardware-accelerated to have faster computations.

Creating the project
--------------------
The recommended way to create a new NMF App project is to copy one of the examples (preferably the one which resembles your planned application the most) and rename the folder to start with.
So in our case, we will copy the camera example project folder and rename it to something more fitting, e.g. 'sobel'.
To make our life easier when we import the project into our workspace, we should edit the app's POM to give it a unique name and artifact ID.
So in the ``pom.xml`` inside the folder **sobel** change the field ``artifactId`` from the value 'camera' to 'sobel'. You should also change the values of the ``name`` and ``description`` tags to relate to our example and put your name into the ``author`` tags. 
If you need to use OreKit in your App and don't want to use custom orekit-data, than you sould alsow add the following dependency:
.. code-block:: xml
	:linenos:

	<dependency>
	  <groupId>int.esa.nmf.sdk</groupId>
	  <artifactId>orekit-resources</artifactId>
	  <version>2.0.0-SNAPSHOT</version>
	  <type>jar</type>
	</dependency>

Now we can import the project either into Netbeans or into Eclipse, by using the same methods as we used to import the NMF before. Just for good measure, we should rename the files and classes to have a better representation of our app. Let's change the name of the main class (currently :java:type:`~esa.mo.nmf.apps.SnapNMF`) to :java:type:`~esa.mo.nmf.apps.SobelApp`. Also change the name of the adapter class (:java:type:`~esa.mo.nmf.apps.MCSnapNMFAdapter`) to :java:type:`~esa.mo.nmf.apps.SobelMCAdapter`.

Nice job! You have set up your environment and project to develop your first NMF App. Let's start to take this example app apart and rebuild it to fit our needs!

Why two classes?
----------------
Try to apply good software engineering practices to your apps. In this case, we apply separation of concerns to keep our code structured and keep an overview. The purpose of :java:type:`~esa.mo.nmf.apps.SobelApp` is solely to instantiate the app and all necessary connections to the NMF. The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter` is responsible for the communication between the different services which are provided by the NMF. We can leave the main class as it is. But let's take a deeper look anyways in the next chapter (:doc:`main`).
