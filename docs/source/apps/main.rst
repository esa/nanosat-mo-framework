===============================
Taking a look at the main class
===============================
In this chapter we will take a look at the behavior of the main class of our Sobel app. As said before, the main purpose of the main class (**SobelApp**.java) is to setup the communication with the NMF and its services and to make sure everything is running.

Communication with the NMF
--------------------------
We need two components to handle communication with the NMF. The first component is the :java:type:`esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl` (referred to as ``connector`` in the code and this tutorial).
We can use the ``connector`` to request services from the NMF (e.g. camera and iADCS in our case) and push results to the NMF, so they are forwarded to the ground.
Together with the ``connector``, we need an ``adapter`` which handles the responses coming from the ``connector`` and pushing of requests and results to the ``connector``.
That's why we supply the ``connector`` to the ``adapter`` by calling ``adapter.setNMF(connector)`` and vice verca by calling ``connector.init(adapter)`` on startup.

Instantiating the app
---------------------
Since every important operation in this example is dispatched to the adapter and the connections are set up in the constructor of our app, we just need to create an instance of our **SobelApp** class in the main method.
:doc:`adapter`
