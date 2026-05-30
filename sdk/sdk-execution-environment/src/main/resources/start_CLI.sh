#!/bin/sh
#
# Starts the NMF CLI Tool.
# A command-line interface to NMF services (directory lookup, parameter get/set,
# action execution, archive queries).
#
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$SCRIPT_DIR/home/nmf/cli-tool" || exit 1
exec ./cli-tool.sh "$@"
