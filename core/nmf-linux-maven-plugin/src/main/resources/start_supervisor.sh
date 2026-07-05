#!/bin/sh
# =============================================================================
# NMF Bootloader — starts the NanoSat MO Supervisor.
#
# Implements the NMF Bootloader Specification (see the NMF documentation,
# "NMF Bootloader Specification"). The Nominal Sequence:
#   1. Initialisation      — resolve the NMF root, start the Boot Report
#   2. Self-tests          — baseline-independent environment checks
#   3. Baseline selection  — choose the software baseline to run
#   4. Integrity test      — JAR checksums + Java runtime launch check
#   5. Execution           — start the Supervisor from the selected baseline
#
# This script is static: it carries no version, mission or configuration
# values. All variability lives in the bootloader/ directory:
#   bootloader/baseline-primary.properties
#   bootloader/baseline-secondary.properties
#   bootloader/baseline-factory.properties
#   bootloader/config.properties
#   bootloader/state.properties       (written by this script)
# Boot Reports are written to logs/bootloader/, one ISO-dated file per day.
# =============================================================================

# --- Built-in defaults (overridden by bootloader/config.properties) ----------
MAX_REPORT_FILE_SIZE_KB=100
MIN_FREE_DISK_KB=10240
APPS_ISOLATION=none
SCHEMA_VERSION=1

# =============================================================================
# Helpers
# =============================================================================

# get_prop <key> <file> — prints the value of a plain "key=value" line, or
# nothing if absent. Full Java properties syntax (spaces, escapes) is not
# supported. Example: get_prop nmf-version bootloader/baseline-primary.properties
#                     prints "5.0-SNAPSHOT"
get_prop() {
    [ -r "$2" ] || return 1
    sed -n "s/^$1=//p" "$2" | head -n 1 | tr -d '\r'
}

# report <text> — one Boot Report line, written immediately (BAA.03) and
# echoed to stdout. Suppressed (file only) once the daily size cap is hit.
report() {
    _line="$(date "$TS_FMT") $1"
    echo "BOOTLOADER: $_line"
    if [ "$REPORT_ENABLED" = "true" ]; then
        echo "$_line" >> "$REPORT_FILE"
    fi
}

# =============================================================================
# Step 1 — Initialisation
# =============================================================================
initialisation() {
    mkdir -p "$REPORT_DIR" "$NMF_HOME/logs/supervisor"

    # Load the configuration overrides (non-critical: defaults apply if absent)
    CONFIG_STATUS="using built-in defaults"
    if [ -r "$CONFIG_FILE" ]; then
        _v=$(get_prop max-report-file-size-kb "$CONFIG_FILE")
        [ -n "$_v" ] && MAX_REPORT_FILE_SIZE_KB=$_v
        _v=$(get_prop min-free-disk-kb "$CONFIG_FILE")
        [ -n "$_v" ] && MIN_FREE_DISK_KB=$_v
        _v=$(get_prop apps-isolation "$CONFIG_FILE")
        [ -n "$_v" ] && APPS_ISOLATION=$_v
        CONFIG_STATUS="loaded from $CONFIG_FILE"
    fi

    # Daily report size cap (BAA.06): suppress after a single notice
    REPORT_ENABLED=true
    if [ -f "$REPORT_FILE" ] \
            && [ "$(wc -c < "$REPORT_FILE")" -gt "$((MAX_REPORT_FILE_SIZE_KB * 1024))" ]; then
        if ! grep -q "REPORTS SUPPRESSED" "$REPORT_FILE"; then
            echo "$(date "$TS_FMT") !!! MaxReportFileSize (${MAX_REPORT_FILE_SIZE_KB} KB)" \
                 "exceeded - further reports of today are SUPPRESSED" >> "$REPORT_FILE"
        fi
        REPORT_ENABLED=false
    fi

    if [ "$REPORT_ENABLED" = "true" ]; then
        echo "=== BOOT REPORT START $(date "$TS_FMT")$(date +%z) ===" >> "$REPORT_FILE"
    fi
    report "INITIALISATION nmf-home: $NMF_HOME"
    report "INITIALISATION config: $CONFIG_STATUS"

    # Cold/warm restart detection via the kernel boot identifier
    BOOT_ID=$(cat /proc/sys/kernel/random/boot_id 2>/dev/null || echo "unknown")
    LAST_BOOT_ID=$(get_prop boot-id "$STATE_FILE")
    if [ "$BOOT_ID" != "unknown" ] && [ "$BOOT_ID" = "$LAST_BOOT_ID" ]; then
        RESTART_TYPE=warm
    else
        RESTART_TYPE=cold
    fi
    report "INITIALISATION restart-type: $RESTART_TYPE"

    # Persist the state (write to a temporary file, then atomic rename)
    if mkdir -p "$BOOT_DIR" 2>/dev/null \
            && echo "boot-id=$BOOT_ID" > "$STATE_FILE.tmp" 2>/dev/null \
            && mv "$STATE_FILE.tmp" "$STATE_FILE" 2>/dev/null; then
        :
    else
        report "INITIALISATION state: FAIL - could not persist $STATE_FILE"
    fi
}

# =============================================================================
# Step 2 — Self-tests (baseline-independent; recorded, never aborts: BEF.04)
# =============================================================================
self_tests() {
    for _dir in bootloader etc jars-nmf jars-mission logs; do
        if [ -d "$NMF_HOME/$_dir" ]; then
            report "SELF-TESTS directory $_dir: OK"
        else
            report "SELF-TESTS directory $_dir: FAIL - not found"
        fi
    done
    if [ -w "$NMF_HOME/logs" ]; then
        report "SELF-TESTS logs writable: OK"
    else
        report "SELF-TESTS logs writable: FAIL"
    fi

    _free_kb=$(df -kP "$NMF_HOME" 2>/dev/null | awk 'NR==2 {print $4}')
    if [ -n "$_free_kb" ] && [ "$_free_kb" -ge "$MIN_FREE_DISK_KB" ]; then
        report "SELF-TESTS free-disk: OK (${_free_kb} KB)"
    else
        report "SELF-TESTS free-disk: FAIL (${_free_kb:-unknown} KB < ${MIN_FREE_DISK_KB} KB)"
    fi
}

# =============================================================================
# Step 3 — Baseline selection (when a baseline file is unreadable or
# incomplete, it degrades file-level: primary -> secondary -> factory)
# =============================================================================
baseline_selection() {
    SELECTED_ROLE=""
    for _role in primary secondary factory; do
        _file=$BOOT_DIR/baseline-$_role.properties
        _nmf=$(get_prop nmf-version "$_file")
        _mission=$(get_prop mission-version "$_file")
        _java=$(get_prop java "$_file")
        _main=$(get_prop main-class "$_file")

        if [ -n "$_nmf" ] && [ -n "$_mission" ]; then
            report "BASELINE-SELECTION $_role: nmf=$_nmf mission=$_mission java=${_java:-?}"
        else
            report "BASELINE-SELECTION $_role: FAIL - unreadable or incomplete: $_file"
        fi

        if [ -z "$SELECTED_ROLE" ] && [ -n "$_nmf" ] && [ -n "$_mission" ] \
                && [ -n "$_java" ] && [ -n "$_main" ]; then
            SELECTED_ROLE=$_role
            NMF_VERSION=$_nmf
            MISSION_VERSION=$_mission
            JAVA_SPEC=$_java
            MAIN_CLASS=$_main
            _schema=$(get_prop schema-version "$_file")
            if [ "$_schema" != "$SCHEMA_VERSION" ]; then
                report "BASELINE-SELECTION schema-version: MISMATCH" \
                       "('${_schema:-absent}', expected $SCHEMA_VERSION)"
            fi
        fi
    done

    if [ -z "$SELECTED_ROLE" ]; then
        report "BASELINE-SELECTION selected: FAIL - no usable baseline file"
        report "=== BOOT ABORTED ==="
        exit 1
    fi
    report "BASELINE-SELECTION selected: $SELECTED_ROLE"
}

# =============================================================================
# Step 4 — Integrity test (recorded, never aborts: BEF.04)
# =============================================================================
integrity_test() {
    for _jardir in "jars-nmf/$NMF_VERSION" "jars-mission/$MISSION_VERSION"; do
        if [ ! -f "$NMF_HOME/$_jardir/SHA256SUMS" ]; then
            report "INTEGRITY-TEST $_jardir: FAIL - SHA256SUMS missing"
        elif (cd "$NMF_HOME/$_jardir" && sha256sum -c SHA256SUMS > /dev/null 2>&1); then
            report "INTEGRITY-TEST $_jardir: OK"
        else
            report "INTEGRITY-TEST $_jardir: FAIL - checksum mismatch"
        fi
    done

    # Resolve the Java runtime of the selected baseline
    case "$JAVA_SPEC" in
        system) JAVA_CMD=java ;;
        /*)     JAVA_CMD=$JAVA_SPEC ;;
        *)      JAVA_CMD=$NMF_HOME/$JAVA_SPEC ;;
    esac
    if "$JAVA_CMD" -version > /dev/null 2>&1; then
        report "INTEGRITY-TEST java-runtime ($JAVA_CMD): OK"
    else
        report "INTEGRITY-TEST java-runtime ($JAVA_CMD): FAIL - does not execute"
    fi
}

# =============================================================================
# Step 5 — Execution
# =============================================================================
execution() {
    SUPERVISOR_LOG=$NMF_HOME/logs/supervisor/supervisor_$(date +%F).log
    CLASSPATH="$NMF_HOME/jars-mission/$MISSION_VERSION/*:$NMF_HOME/jars-nmf/$NMF_VERSION/*"

    : >> "$SUPERVISOR_LOG"
    "$JAVA_CMD" \
        -Xms16M \
        -Djava.util.logging.config.file="$NMF_HOME/etc/logging.properties" \
        -Desa.mo.nmf.packagemanager.appsIsolation="$APPS_ISOLATION" \
        -classpath "$CLASSPATH" \
        "$MAIN_CLASS" \
        >> "$SUPERVISOR_LOG" 2>&1 &
    JVM_PID=$!
    report "EXECUTION supervisor started: pid=$JVM_PID main-class=$MAIN_CLASS"
    report "=== BOOT REPORT END ==="

    # Non-critical console duplication of the supervisor log (BAA.02)
    tail -n 0 -f "$SUPERVISOR_LOG" &
    TAIL_PID=$!
}

# =============================================================================
# Nominal Sequence
# =============================================================================
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
NMF_HOME=$SCRIPT_DIR

# Report timestamps: ISO date and time with milliseconds. The %3N precision
# needs GNU date; on other implementations (e.g. busybox) fall back to seconds.
if date +%3N 2>/dev/null | grep -q "^[0-9][0-9][0-9]\$"; then
    TS_FMT="+%Y-%m-%d %H:%M:%S.%3N"
else
    TS_FMT="+%Y-%m-%d %H:%M:%S"
fi
BOOT_DIR=$NMF_HOME/bootloader
CONFIG_FILE=$BOOT_DIR/config.properties
STATE_FILE=$BOOT_DIR/state.properties
REPORT_DIR=$NMF_HOME/logs/bootloader
REPORT_FILE=$REPORT_DIR/bootloader_$(date +%F).log
JVM_PID=""
TAIL_PID=""

initialisation
self_tests
baseline_selection
integrity_test
execution

# Terminate the Supervisor and the log duplication when this script is stopped
cleanup() {
    trap '' TERM INT
    [ -n "$TAIL_PID" ] && kill "$TAIL_PID" 2>/dev/null
    if [ -n "$JVM_PID" ]; then
        kill "$JVM_PID" 2>/dev/null
        wait "$JVM_PID" 2>/dev/null
    fi
    exit 143
}
trap cleanup TERM INT

wait "$JVM_PID"
JVM_EXIT=$?
kill "$TAIL_PID" 2>/dev/null
echo "BOOTLOADER: supervisor exited with code $JVM_EXIT"
exit "$JVM_EXIT"
