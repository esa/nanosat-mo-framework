LOG Browser
===========
CLI tool to browse a COM archive to retrieve logs and more from it.

# Usage

## Help

Starting it with `-h` or `--help` like so:

`/home/nmf/log-browser-tool/$ ./log-browser-tool.sh -h`

Will display the following help message:

```
Usage: LogBrowser [-h] [COMMAND]
Browses a COM archive to retrieve logs and more from it.
  -h, --help   display a help message
Commands:
  dump_raw  Dumps to a JSON file the raw tables content of a local COM archive
  dump      Dumps to a JSON file the formatted content of a local or remote COM archive
  get_logs  Dumps to a LOG file an NMF app logs using the content of a local or remote COM archive.
  list      Lists the COM archive providers URIs found in a central directory
```
