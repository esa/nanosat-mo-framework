===============================
Taking a look at the main class
===============================

.. contents:: Table of contents
    :local:

In this chapter we will take a look at the behavior of the main class of our Sobel app. As said before, the main purpose of the main class (:java:type:`~esa.mo.nmf.apps.SobelApp`.java) is to setup the communication with the NMF and its services and to make sure everything is running.
As said in the previous part, be sure to copy the snapNMF class. All that left to do is replace ``SnapNMF`` by ``SobelApp`` and ``MCSnapNMFAdapter`` by ``SobelMCAdapter`` :

.. code-block:: java
   :linenos:

    public class SobelApp
    {
        private final NanoSatMOConnectorImpl connector;

        public SobelApp()
        {
            SobelMCAdapter adapter = new SobelMCAdapter();
            connector = new NanoSatMOConnectorImpl();
            adapter.setNMF(connector);
            connector.init(adapter);
        }
        /*
        Main command line entry point.
        @param args the command line arguments
        @throws java.lang.Exception If there is an error
        */
        public static void main(final String args[]) throws Exception
        {
            SobelApp demo = new SobelApp();
        }
    }


Communication with the NMF
--------------------------
We need two components to handle communication with the NMF. The first component is the :java:type:`~esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl` (referred to as ``connector`` in the code and this tutorial).
We can use the ``connector`` to request services from the NMF (e.g. camera and iADCS in our case) and push results to the NMF, so they are forwarded to the ground.
Together with the ``connector``, we need an ``adapter`` which handles the responses coming from the ``connector`` and pushing of requests and results to the ``connector``.
That's why we supply the ``connector`` to the ``adapter`` by calling ``adapter.setNMF(connector)`` and vice versa by calling ``connector.init(adapter)`` on startup.

Communication with the supervisor or another app
------------------------------------------------
Your space app can also consume services directly from the supervisor or another space app using either
forNMFSupervisor or forNMFApp methods from the SpaceMOApdapterImpl. For example to receive the GPS
defined parameters one would obtain the GPS consumer as follows :

.. code-block:: java
   :linenos:
   
   SpaceMOApdapterImpl gpsSMA = SpaceMOApdapterImpl.forNMFApp(connector.readCentralDirectoryServiceURI(), "gps");

Full examples of these can be found under space app examples.
Space-to-space-supervisor app connects to the supervisor, listens and logs some of the parameter values from it.
Space-to-space app shows how to connect to the GPS app and logs the GPS parameters as it receives them.

Instantiating the app
---------------------
Since every important operation in this example is dispatched to the adapter and the connections are set up in the constructor of our app, we just need to create an instance of our :java:type:`~esa.mo.nmf.apps.SobelApp` class in the main method.
:doc:`adapter`
