===========
App Storage
===========

.. contents:: Table of contents
   :local:

NMF provides ``AppStorage``, a utility class that returns standardised, app-specific directories for writing
files. App developers should always use these paths rather than arbitrary locations in the filesystem.

Directory layout
----------------

All directories are created under the user's home directory::

   ~/.nmf-apps/<appName>/
       cache/
       user-data/
       nmf-internal/

Directories are created automatically on first access with permissions ``rwxrwx---`` (owner and ``nmf-admin``
group). On Windows the equivalent owner-only read/write/execute permissions are applied instead.

The three directories
---------------------

``AppStorage.getAppCacheDir()``
   Ephemeral data. The system may delete files here automatically when disk space is needed. Do not rely on
   these files surviving across app restarts or relocations.

``AppStorage.getAppUserdataDir()``
   Persistent, app-specific data. Use this for any files that must survive restarts — processed images,
   recorded measurements, etc.

``AppStorage.getAppNMFInternalDir()``
   Reserved for NMF framework internals (COM archive database, instance lock file).

.. warning::

   Do not read or write files in ``nmf-internal`` from app code. Its
   contents are managed exclusively by the NMF runtime and may change
   between framework versions without notice.

Usage example
-------------

.. code-block:: java

   File cacheDir    = AppStorage.getAppCacheDir();
   File userdataDir = AppStorage.getAppUserdataDir();

   // Write a temporary work file
   File tmp = new File(cacheDir, "tile_work.bin");

   // Write a result that must persist
   File result = new File(userdataDir, "output.csv");

.. note::

   ``AppStorage`` reads the app name from the system property
   ``HelperMisc.PROP_MO_APP_NAME``. This property is set automatically
   by the NMF runtime when an app is launched by the Supervisor. Calling
   any ``AppStorage`` method outside of a properly initialised NMF app
   will throw an ``IllegalArgumentException``.
