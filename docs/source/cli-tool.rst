CLI Tool
================================================

.. contents:: Table of contents
    :local:

Archive Commands
-----------------
* dump_raw::

    Usage: esa.mo.nmf.clitool.CLITool archive dump_raw [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>] <jsonFile>
    Dumps to a JSON file the raw tables content of a local COM archive
          <jsonFile>                  target JSON file
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* dump::

    Usage: esa.mo.nmf.clitool.CLITool archive dump [-h] [-d=<domainId>] [-e=<endTime>] [-l=<databaseFile>] [-p=<providerName>]
                                                   [-r=<providerURI>] [-s=<startTime>] [-t=<comType>] <jsonFile>
    Dumps to a JSON file the formatted content of a local or remote COM archive
          <jsonFile>                  target JSON file
      -d, --domain=<domainId>         Restricts the dump to objects in a specific domain
                                        - format: key1.key2.[...].keyN.
                                        - example: esa.NMF_SDK.nanosat-mo-supervisor
      -e, --end=<endTime>             Restricts the dump to objects created before the given time. If this option is provided without the
                                        -s option, returns the single object that has the closest timestamp to, but not greater than
                                        <endTime>
                                        - format: "yyyy-MM-dd HH:mm:ss.SSS"
                                        - example: "2021-03-05 12:05:45.271"
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory
      -s, --start=<startTime>         Restricts the dump to objects created after the given time
                                        - format: "yyyy-MM-dd HH:mm:ss.SSS"
                                        - example: "2021-03-04 08:37:58.482"
      -t, --type=<comType>            Restricts the dump to objects that are instances of <comType>
                                        - format: areaNumber.serviceNumber.areaVersion.objectNumber.
                                        - examples (0=wildcard): 4.2.1.1, 4.2.1.0

* list::

    Usage: esa.mo.nmf.clitool.CLITool archive list [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>] <centralDirectoryURI>
    Lists the COM archive providers URIs found in a central directory
          <centralDirectoryURI>       URI of the central directory to use
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* backup_and_clean::

    Usage: esa.mo.nmf.clitool.CLITool archive backup_and_clean [-h] [-l=<databaseFile>] [-o=<filename>] [-p=<providerName>]
                                                               [-r=<providerURI>] <domainId>
    Backups the data for a specific provider
          <domainId>                  Restricts the dump to objects in a specific domain
                                        - format: key1.key2.[...].keyN.
                                        - example: esa.NMF_SDK.nanosat-mo-supervisor
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -o, --output=<filename>         target file name
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

Logs Commands
-------------
* list::

    Usage: esa.mo.nmf.clitool.CLITool log list [-h] [-d=<domainId>] [-e=<endTime>] [-l=<databaseFile>] [-p=<providerName>]
                                               [-r=<providerURI>] [-s=<startTime>]
    Lists NMF apps having logs in the content of a local or remote COM archive.
      -d, --domain=<domainId>         Restricts the list to NMF apps in a specific domain
                                        - default: search for app in all domains
                                        - format: key1.key2.[...].keyN.
                                        - example: esa.NMF_SDK.nanosat-mo-supervisor
      -e, --end=<endTime>             Restricts the list to NMF apps having logs logged before the given time. If this option is provided
                                        without the -s option, returns the single object that has the closest timestamp to, but not greater
                                        than <endTime>
                                        - format: "yyyy-MM-dd HH:mm:ss.SSS"
                                        - example: "2021-03-05 12:05:45.271"
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory
      -s, --start=<startTime>         Restricts the list to NMF apps having logs logged after the given time
                                        - format: "yyyy-MM-dd HH:mm:ss.SSS"
                                        - example: "2021-03-04 08:37:58.482"

* get::

    Usage: esa.mo.nmf.clitool.CLITool log get [-ht] [-d=<domainId>] [-e=<endTime>] [-l=<databaseFile>] [-p=<providerName>]
                                              [-r=<providerURI>] [-s=<startTime>] <appName> <logFile>
    Dumps to a LOG file an NMF app logs using the content of a local or remote COM archive.
          <appName>                   Name of the NMF app we want the logs for
          <logFile>                   target LOG file
      -d, --domain=<domainId>         Domain of the NMF app we want the logs for
                                        - default: search for app in all domains
                                        - format: key1.key2.[...].keyN.
                                        - example: esa.NMF_SDK.nanosat-mo-supervisor
      -e, --end=<endTime>             Restricts the dump to logs logged before the given time. If this option is provided without the -s
                                        option, returns the single object that has the closest timestamp to, but not greater than <endTime>
                                        - format: "yyyy-MM-dd HH:mm:ss.SSS"
                                        - example: "2021-03-05 12:05:45.271"
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory
      -s, --start=<startTime>         Restricts the dump to logs logged after the given time
                                        - format: "yyyy-MM-dd HH:mm:ss.SSS"
                                        - example: "2021-03-04 08:37:58.482"
      -t, --timestamped               If specified additional timestamp will be added to each line

MC Commands
-----------

Parameters
^^^^^^^^^^
* subscribe::

    Usage: esa.mo.nmf.clitool.CLITool parameter subscribe [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
                                                          [<parameterNames>...]
    Subscribes to specified parameters
          [<parameterNames>...]       Names of the parameters to subscribe to. If non are specified subscribe to all.
                                       - examples: param1 or param1 param2
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* enable::

    Usage: esa.mo.nmf.clitool.CLITool parameter enable [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>] [<parameterNames>...]
    Enables generation of specified parameters
          [<parameterNames>...]       Names of the parameters to enable. If non are specified enable all
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* disable::

    Usage: esa.mo.nmf.clitool.CLITool parameter disable [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
                                                        [<parameterNames>...]
    Disables generation of specified parameters
          [<parameterNames>...]       Names of the parameters to disable. If non are specified disable all
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* get::

    Usage: esa.mo.nmf.clitool.CLITool parameter get [-hj] [-d=<domainId>] [-e=<endTime>] [-l=<databaseFile>] [-p=<providerName>]
                                                    [-r=<providerURI>] [-s=<startTime>] <filename> [<parameterNames>...]
    Dumps to a file MO parameters samples from COM archive.
          <filename>                  Target file for the parameters samples
          [<parameterNames>...]       Names of the parameters to retrieve
                                       - examples: param1 or param1 param2
      -d, --domain=<domainId>         Restricts the dump to parameters in a specific domain
                                        - format: key1.key2.[...].keyN.
                                        - example: esa.NMF_SDK.nanosat-mo-supervisor
      -e, --end=<endTime>             Restricts the dump to parameters generated before the given time. If this option is provided without
                                        the -s option, returns the single object that has the closest timestamp to, but not greater than
                                        <endTime>
                                        - format: "yyyy-MM-dd HH:mm:ss.SSS"
                                        - example: "2021-03-05 12:05:45.271"
      -h, --help                      display a help message
      -j, --json                      If specified output will be in JSON format
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory
      -s, --start=<startTime>         Restricts the dump to parameters generated after the given time
                                        - format: "yyyy-MM-dd HH:mm:ss.SSS"
                                        - example: "2021-03-04 08:37:58.482"

* list::

    Usage: esa.mo.nmf.clitool.CLITool parameter list [-h] [-d=<domainId>] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
    Lists available parameters in a COM archive.
      -d, --domain=<domainId>         Restricts the dump to objects in a specific domain
                                        - format: key1.key2.[...].keyN.
                                        - example: esa.NMF_SDK.nanosat-mo-supervisor
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

Aggregations
^^^^^^^^^^^^
* subscribe::

    Usage: esa.mo.nmf.clitool.CLITool aggregation subscribe [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
                                                            [<parameterNames>...]
    Subscribes to specified aggregations
          [<parameterNames>...]       Names of the aggregations to subscribe to. If non are specified subscribe to all.
                                       - examples: aggregation1 or aggregation1 aggregation2
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* enable::

    Usage: esa.mo.nmf.clitool.CLITool aggregation enable [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
                                                         [<aggregationNames>...]
    Enables generation of specified aggregations
          [<aggregationNames>...]     Names of the aggregations to enable. If non are specified enable all
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* disable::

    Usage: esa.mo.nmf.clitool.CLITool aggregation disable [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
                                                          [<aggregationNames>...]
    Disables generation of specified aggregations
          [<aggregationNames>...]     Names of the aggregations to disable. If non are specified disable all
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory


Platform Commands
-----------------

GPS
^^^
* get-nmea-sentence::

    Usage: esa.mo.nmf.clitool.CLITool gps get-nmea-sentence [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
                                                            <sentenceIdentifier>
    Gets the NMEA sentence
          <sentenceIdentifier>        Identifier of the sentence
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

ADCS
^^^^
* get-status::

    Usage: esa.mo.nmf.clitool.CLITool adcs get-status [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
    Gets the provider status
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

Camera
^^^^^^
* take-picture::

    Usage: esa.mo.nmf.clitool.CLITool camera take-picture [-h] [-exp=<exposureTime>] [-fmt=<format>] [-gb=<gainBlue>] [-gg=<gainGreen>]
                                                          [-gr=<gainRed>] [-l=<databaseFile>] [-o=<outputFile>] [-p=<providerName>]
                                                          [-r=<providerURI>] -res=<resolution>
    Take a picture from the camera
      -exp, --exposure=<exposureTime> Exposure time of the picture
      -fmt, --format=<format>         Format of the image
      -gb,  --gain-blue=<gainBlue>    Gain of the blue channel
      -gg,  --gain-green=<gainGreen>  Gain of the green channel
      -gr,  --gain-red=<gainRed>      Gain of the red channel
      -h,   --help                    display a help message
      -l,   --local=<databaseFile>    Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -o,   --output=<outputFile>     Name of the output file without the extension.
      -p,   --provider=<providerName> Name of the provider we want to connect to
      -r,   --remote=<providerURI>    Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory
      -res, --resolution=<resolution> Resolution of the image in format widthxheigh. For example 1920x1080

Software Management Commands
----------------------------
Apps Launcher
^^^^^^^^^^^^^
* subscribe::

    Usage: esa.mo.nmf.clitool.CLITool apps-launcher subscribe [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
                                                              [<appNames>...]
    Subscribes to app's stdout
          [<appNames>...]             Names of the apps to subscribe to. If non are specified subscribe to all.
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* run::

    Usage: esa.mo.nmf.clitool.CLITool apps-launcher run [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>] <appName>
    Runs the specified provider app
          <appName>                   Name of the app to run.
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* stop::

    Usage: esa.mo.nmf.clitool.CLITool apps-launcher stop [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>] <appName>
    Stops the specified provider app
          <appName>                   Name of the app to stop.
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

* kill::

    Usage: esa.mo.nmf.clitool.CLITool apps-launcher kill [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>] <appName>
    Kills the specified provider app
          <appName>                   Name of the app to kill.
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory

Heartbeat
^^^^^^^^^
* subscribe::

    Usage: esa.mo.nmf.clitool.CLITool heartbeat subscribe [-h] [-l=<databaseFile>] [-p=<providerName>] [-r=<providerURI>]
    Subscribes to provider's heartbeat
      -h, --help                      display a help message
      -l, --local=<databaseFile>      Local SQLite database file
                                        - example: ../nanosat-mo-supervisor-sim/comArchive.db
      -p, --provider=<providerName>   Name of the provider we want to connect to
      -r, --remote=<providerURI>      Provider URI
                                        - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory
