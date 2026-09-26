=================================
Receiving updates from a provider
=================================

.. contents:: Table of contents
   :local:

A ground application usually wants to know when something happens on board: a parameter changes, an alert is
raised, an action progresses, or an App starts or stops. The NMF has no general-purpose event service for this.
Each service publishes its own updates through a PUB-SUB operation of its own, and a ground application subscribes
to the operations it needs. Updates that were missed, or that happened before the application connected, are read
back from the COM Archive.

What each service publishes
---------------------------

.. list-table::
   :header-rows: 1
   :widths: 25 25 50

   * - Service
     - Operation
     - Publishes
   * - Parameter
     - ``monitorValue``
     - The values of the parameters, periodically or when they change.
   * - Aggregation
     - ``monitorValue``
     - The values of the aggregations.
   * - Alert
     - ``monitorAlert``
     - Each alert raised by the provider.
   * - Action
     - ``monitorExecution``
     - The progress of each action execution, stage by stage.
   * - Apps Launcher
     - ``monitorEvents``
     - The lifecycle of the Apps: ``START_REQUESTED``, ``STARTED``, ``STOP_REQUESTED``, ``STOPPED``, ``KILLED``,
       ``EXITED`` and ``CRASHED``.
   * - Apps Launcher
     - ``monitorExecution``
     - The standard output of the Apps.
   * - Command Executor
     - ``monitorOutput``
     - The output and the end of each command run by the Supervisor.
   * - Heartbeat
     - ``beat``
     - A periodic signal that the provider is alive.

The Platform services publish their own data in the same way: pictures from the Camera, attitude from the
Autonomous ADCS, radio data, nearby positions from the GPS, and partition changes from the FPGA service.

Receiving parameter values
--------------------------

The simplest way to follow parameters is the listener of the Ground MO Adapter. It subscribes to the Parameter
service of the provider and calls the listener with the name and the value of each update:

.. code-block:: java

   gma.addDataReceivedListener(new SimpleDataReceivedListener() {
       @Override
       public void onDataReceived(String parameterName, Serializable data) {
           // handle the new value
       }
   });

Subscribing to a service directly
---------------------------------

Every other update is received by registering with the operation on the service's stub. The ``Subscription``
selects the updates: without filters it receives all of them, and a ``SubscriptionFilter`` narrows it down by the
subscription keys of the operation, such as the name of a parameter or of an App.

The example below receives the lifecycle events of every App from the Apps Launcher service of a Supervisor:

.. code-block:: java

   Subscription subscription = ConnectionConsumer.subscriptionWildcardRandom();
   gma.getSMServices().getAppsLauncherService().getAppsLauncherStub().monitorEventsRegister(
           subscription, new AppsLauncherAdapter() {
               @Override
               public void monitorEventsNotifyReceived(MALMessageHeader msgHeader, Identifier subscriptionId,
                       UpdateHeader updateHeader, MonitorEventsSubscriptionKeys keys,
                       AppEventType eventType, Integer exitCode, String extraInfo, Map qosProperties) {
                   // keys.getAppName() is the App, eventType what happened to it
               }
           });

The other operations work the same way, each with its own adapter and subscription keys. For example,
``monitorValueRegister`` on the Parameter stub with a filter on the ``name`` key receives only the named
parameters. The CLI tool uses exactly this in its ``parameter subscribe`` command.

A subscription stays active until it is deregistered with the matching ``...Deregister`` operation, or until the
connection is closed.

Reading past updates from the COM Archive
-----------------------------------------

By default, providers also store what they publish as COM objects in the COM Archive: ParameterValue, AggregationValue, AlertEvent
and ExecutionStatus objects in the M&C services, and AppStarted and AppStopped objects in the Apps Launcher. The
Archive service returns them by object type, domain and time range, and by related link.

The related link of an object points to the object it belongs to. For example, a ParameterValue is related to
its ParameterDefinition, and an AppStarted object to the AppDetails of its App. So the history of one parameter,
or of one App, is a query on its definition id:

.. code-block:: java

   ArchiveQuery query = new ArchiveQuery(appDetailsId);   // objects related to this App
   gma.getCOMServices().getArchiveService().getArchiveStub().query(
           Boolean.TRUE, AppsLauncherServiceInfo.APPSTARTED_OBJECT_TYPE, query, null,
           new ArchiveAdapter() {
               @Override
               public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType,
                       IdentifierList domain, ArchiveDetailsList details, HeterogeneousList bodies,
                       Map qosProperties) {
                   // details.get(i).getTimestamp() is when the App was started
               }
           });

The ``log list`` and ``log get`` commands of the :doc:`../tooling/cli` read the history of the Apps this way.
