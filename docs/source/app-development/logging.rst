=======
Logging
=======

.. contents:: Table of contents
   :local:

NMF Apps use ``java.util.logging`` (``JUL``) for log output. The
framework does not impose a logging facade; an app may log directly via
``Logger.getLogger(...)``.

Log destinations
----------------

By default, log output is written to:

- **Console** — the standard output of the JVM process. The Supervisor
  captures this stream and republishes it via the
  ``AppsLauncher.monitorExecution`` PubSub operation, so connected
  consumers (such as the CTT) see it in real time.
- **File** — depending on the deployment, ``JUL`` may be configured to
  write to a log file under ``NMF_HOME/logs/``.

Configuration
-------------

Logging verbosity and handler configuration are controlled by a
``logging.properties`` file. The default location is the working
directory of the JVM; the file path may be overridden with the
``-Djava.util.logging.config.file=...`` system property when launching
the app.

A typical configuration sets the global level and overrides specific
package levels:

.. code-block:: properties

   handlers=java.util.logging.ConsoleHandler
   .level=INFO
   java.util.logging.ConsoleHandler.level=ALL
   java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
   esa.mo.level=FINE

Logging from app code
---------------------

.. code-block:: java

   private static final Logger LOGGER = Logger.getLogger(MyApp.class.getName());

   LOGGER.log(Level.INFO, "Processing image {0}", filename);
   LOGGER.log(Level.SEVERE, "Capture failed", ex);

Use parameterised messages (``{0}``, ``{1}``, …) rather than string
concatenation so that argument formatting is deferred until the message
is actually written.

Observing logs from the ground
------------------------------

A ground consumer can subscribe to an app's log output by invoking
``AppsLauncher.monitorExecution`` on the Supervisor with the target
app's identifier. The CTT exposes this through the Apps Launcher
Service tab.
