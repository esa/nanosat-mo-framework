==============
Worked example
==============

.. contents:: Table of contents
   :local:

This worked example implements a minimal NMF App that exposes one parameter and one action, using each of the
two M&C APIs side by side.

The app
-------

The app exposes:

- A parameter ``greeting`` of type String, initially ``"Hello, world"``, reported every 3 seconds.
- An action ``shout`` that uppercases the current ``greeting``.

Main class
----------

The main class is identical for both APIs. It instantiates the Connector, constructs the adapter, and lets the
Supervisor manage the rest of the lifecycle:

.. code-block:: java

   public class HelloApp {
       private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();

       public HelloApp() {
           connector.init(new HelloAdapter());
       }

       public static void main(final String[] args) {
           new HelloApp();
       }
   }

Adapter
-------

The adapter exposes the parameter and action. Below, the same adapter is shown with each API. The two versions
are interchangeable; pick either.

.. tabs::

   .. tab:: Listener-interface API

      Extend ``SimpleMonitorAndControlAdapter`` and override the
      explicit dispatch methods.

      .. code-block:: java

         public class HelloAdapter extends SimpleMonitorAndControlAdapter {

             private static final String P_GREETING = "greeting";
             private static final String A_SHOUT = "shout";
             private String greeting = "Hello, world";

             @Override
             public void initialRegistrations(MCRegistration r) {
                 r.setMode(RegistrationMode.DONT_UPDATE_IF_EXISTS);

                 ParameterDefinitionList defs = new ParameterDefinitionList();
                 defs.add(new ParameterDefinition(
                         new Identifier(P_GREETING),
                         "The current greeting",
                         AttributeType.STRING, "",
                         true, new Duration(3), null, null));
                 r.registerParameters(defs);

                 ActionDefinitionList actions = new ActionDefinitionList();
                 actions.add(new ActionDefinition(
                         new Identifier(A_SHOUT),
                         "Uppercase the greeting",
                         new UShort(0),
                         new ArgumentDefinitionList()));
                 r.registerActions(actions);
             }

             @Override
             public Serializable onGetValueSimple(String name) {
                 return P_GREETING.equals(name) ? greeting : null;
             }

             @Override
             public boolean onSetValueSimple(String name, Serializable value) {
                 if (P_GREETING.equals(name)) {
                     greeting = value.toString();
                     return true;
                 }
                 return false;
             }

             @Override
             public boolean actionArrivedSimple(String name, Serializable[] values, Long id) {
                 if (A_SHOUT.equals(name)) {
                     greeting = greeting.toUpperCase();
                     return true;
                 }
                 return false;
             }
         }

   .. tab:: Annotation API

      Extend ``MonitorAndControlNMFAdapter`` and annotate the field
      and method.

      .. code-block:: java

         public class HelloAdapter extends MonitorAndControlNMFAdapter {

             @Parameter(description = "The current greeting",
                        reportIntervalSeconds = 3)
             private String greeting = "Hello, world";

             @Action(description = "Uppercase the greeting")
             public UInteger shout(
                     Long actionInstanceObjId,
                     boolean reportProgress,
                     MALInteraction interaction) {
                 greeting = greeting.toUpperCase();
                 return null;  // success
             }
         }

What changes between the two versions
-------------------------------------

- **Boilerplate.** The listener version explicitly builds the ``ParameterDefinitionList`` and
  ``ActionDefinitionList``; the annotation version relies on reflection to discover annotated members.
- **Dispatch.** The listener version switches on parameter and action names in ``onGetValueSimple`` /
  ``onSetValueSimple`` / ``actionArrivedSimple``; the annotation version dispatches directly through the
  annotated field and method.
- **Externally observable behaviour.** None. The same MO objects are registered with the Directory Service,
  and a consumer cannot distinguish the two implementations.

For a more substantial worked example involving Platform service consumption and multi-stage actions, see
:doc:`sobel-example`.

.. note::

   This page uses the ``sphinx-tabs`` extension to render the two
   implementations side by side. If the extension is not installed,
   each tab renders as a separate section.
