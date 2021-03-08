LOG Browser
===========
CLI tool to browse a COM archive to retrieve logs and more from it.

# Usage
## Help
```
/home/nmf/log-browser-tool/$ ./log-browser-tool.sh -h
Usage: LogBrowser [-h] [COMMAND]
Browses a COM archive to retrieve logs and more from it.
  -h, --help   display a help message
Commands:
  dump      Dumps to a JSON file the formatted content of a COM archive provider
  dump_raw  Dumps to a JSON file the raw tables content of a SQLite COM archive
  get_logs  Dumps to a LOG file the logs of an NMF app using the content of a COM archive provider
  list      Lists the COM archive providers names found in a central directory
```
