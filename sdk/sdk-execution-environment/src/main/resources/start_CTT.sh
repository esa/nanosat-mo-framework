#!/bin/sh
#
# Starts the NMF Consumer Test Tool (CTT).
# The CTT is a ground GUI for monitoring and controlling NMF Apps via MO services.
# The NMF Supervisor must be running before connecting the CTT.
#
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$SCRIPT_DIR/home/nmf/consumer-test-tool" || exit 1
exec ./consumer-test-tool.sh "$@"
