COM Archive Tool
===========
Browses a COM archive to retrieve it's contents from the command line.

# Usage

## Help

Starting it with `-h` or `--help` like so:

`/home/nmf/com-archive-tool/$ ./com-archive-tool.sh -h`

Will display the following help message:

```
Usage: COMArchiveTool [-h] [COMMAND]
Browses a COM archive to retrieve it's contents.
  -h, --help   display a help message
Commands:
  log        Gets or lists NMF app logs using the content of a local or remote COM archive.
  parameter  Gets or lists NMF app parameters using the content of a local or remote COM archive.
  dump_raw   Dumps to a JSON file the raw tables content of a local COM archive
  dump       Dumps to a JSON file the formatted content of a local or remote COM archive
  list       Lists the COM archive providers URIs found in a central directory
```
