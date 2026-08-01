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
#   6. Confirmation        — wait for the Supervisor's confirmation marker
#
# One boot attempt per invocation: on a failed attempt the fallback state is
# updated (primary -> secondary -> factory after boot-max-attempts failures)
# and the script exits non-zero. Restart policy belongs to the caller (e.g. a
# systemd service with Restart=); a confirmed boot resets the fallback state.
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
BOOT_CONFIRM_TIMEOUT_S=60
BOOT_MAX_ATTEMPTS=2
PROMOTION_SOAK_S=60
APPS_ISOLATION=none
SCHEMA_VERSION=1

# =============================================================================
# Helpers
# =============================================================================

# get_prop <key> <file> — prints the value of a plain "key=value" line, or
# nothing if absent. Full Java properties syntax (spaces, escapes) is not
# supported. Example: get_prop nmf-version bootloader/baseline-primary.properties
#                     prints "x.y"
get_prop() {
    [ -r "$2" ] || return 1
    sed -n "s/^$1=//p" "$2" | head -n 1 | tr -d '\r'
}

# report <text> — one Boot Report line, written immediately (BAA.03) and
# echoed to stdout. The line starts with a timestamp followed by the BOOTLOADER
# source tag, so it aligns with the java.util.logging output of the Supervisor.
# Suppressed (file only) once the daily size cap is hit.
report() {
    _line="$(date "$TS_FMT") BOOTLOADER $1"
    echo "$_line"
    if [ "$REPORT_ENABLED" = "true" ]; then
        echo "$_line" >> "$REPORT_FILE"
    fi
}

# record <text> — like report() but written to the Boot Report file only, with
# no console echo. For nominal-success details that would only be console noise
# but are still worth keeping in the forensic record.
record() {
    if [ "$REPORT_ENABLED" = "true" ]; then
        echo "$(date "$TS_FMT") BOOTLOADER $1" >> "$REPORT_FILE"
    fi
}

# write_state <rung> <failed-attempts> — persists the runtime state atomically
write_state() {
    {
        echo "boot-id=$BOOT_ID"
        echo "rung=$1"
        echo "failed-attempts=$2"
    } > "$STATE_FILE.tmp" 2>/dev/null && mv "$STATE_FILE.tmp" "$STATE_FILE" 2>/dev/null
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
        _v=$(get_prop boot-confirm-timeout-s "$CONFIG_FILE")
        [ -n "$_v" ] && BOOT_CONFIRM_TIMEOUT_S=$_v
        _v=$(get_prop boot-max-attempts "$CONFIG_FILE")
        [ -n "$_v" ] && BOOT_MAX_ATTEMPTS=$_v
        _v=$(get_prop promotion-soak-s "$CONFIG_FILE")
        [ -n "$_v" ] && PROMOTION_SOAK_S=$_v
        CONFIG_STATUS="$CONFIG_FILE"
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
        _now=$(date "$TS_FMT")
        echo "======================================================================" >> "$REPORT_FILE"
        echo "$_now BOOTLOADER Boot started at: $_now$(date +%z)" >> "$REPORT_FILE"
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

    # Read the fallback state of the ladder (REC.03)
    RUNG=$(get_prop rung "$STATE_FILE")
    case "$RUNG" in
        primary|secondary|factory) ;;
        *) RUNG=primary ;;
    esac
    ATTEMPTS=$(get_prop failed-attempts "$STATE_FILE")
    case "$ATTEMPTS" in
        ''|*[!0-9]*) ATTEMPTS=0 ;;
    esac
    report "INITIALISATION fallback-state: rung=$RUNG failed-attempts=$ATTEMPTS"

    # Persist the state (write to a temporary file, then atomic rename)
    mkdir -p "$BOOT_DIR" 2>/dev/null
    if ! write_state "$RUNG" "$ATTEMPTS"; then
        report "INITIALISATION FAIL state - could not persist $STATE_FILE"
    fi
}

# =============================================================================
# Step 2 — Self-tests (baseline-independent; recorded, never aborts: BEF.04)
# =============================================================================
self_tests() {
    for _dir in bootloader etc jars-nmf jars-mission logs; do
        if [ -d "$NMF_HOME/$_dir" ]; then
            report "SELF-TESTS OK directory $_dir"
        else
            report "SELF-TESTS FAIL directory $_dir - not found"
        fi
    done
    if [ -w "$NMF_HOME/logs" ]; then
        report "SELF-TESTS OK logs writable"
    else
        report "SELF-TESTS FAIL logs writable"
    fi

    _free_kb=$(df -kP "$NMF_HOME" 2>/dev/null | awk 'NR==2 {print $4}')
    if [ -n "$_free_kb" ] && [ "$_free_kb" -ge "$MIN_FREE_DISK_KB" ]; then
        report "SELF-TESTS OK free-disk (${_free_kb} KB)"
    else
        report "SELF-TESTS FAIL free-disk (${_free_kb:-unknown} KB < ${MIN_FREE_DISK_KB} KB)"
    fi
}

# =============================================================================
# Step 3 — Baseline selection (when a baseline file is unreadable or
# incomplete, it degrades file-level: primary -> secondary -> factory)
# =============================================================================
baseline_selection() {
    SELECTED_ROLE=""
    _at_rung=""
    for _role in primary secondary factory; do
        [ "$_role" = "$RUNG" ] && _at_rung=yes
        _file=$BOOT_DIR/baseline-$_role.properties
        _nmf=$(get_prop nmf-version "$_file")
        _mission=$(get_prop mission-version "$_file")
        _java=$(get_prop java "$_file")
        _main=$(get_prop main-class "$_file")

        if [ -n "$_nmf" ] && [ -n "$_mission" ]; then
            report "BASELINE-SELECTION $_role: nmf=$_nmf mission=$_mission java=${_java:-?}"
        else
            report "BASELINE-SELECTION FAIL $_role - unreadable or incomplete: $_file"
        fi

        if [ -n "$_at_rung" ] && [ -z "$SELECTED_ROLE" ] && [ -n "$_nmf" ] \
                && [ -n "$_mission" ] && [ -n "$_java" ] && [ -n "$_main" ]; then
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
        report "BASELINE-SELECTION FAIL - no usable baseline file"
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
            report "INTEGRITY-TEST FAIL $_jardir - SHA256SUMS missing"
        elif (cd "$NMF_HOME/$_jardir" && sha256sum -c SHA256SUMS > /dev/null 2>&1); then
            report "INTEGRITY-TEST OK $_jardir"
        else
            report "INTEGRITY-TEST FAIL $_jardir - checksum mismatch"
        fi
    done

    # Resolve the Java runtime of the selected baseline
    case "$JAVA_SPEC" in
        system) JAVA_CMD=java ;;
        /*)     JAVA_CMD=$JAVA_SPEC ;;
        *)      JAVA_CMD=$NMF_HOME/$JAVA_SPEC ;;
    esac
    if "$JAVA_CMD" -version > /dev/null 2>&1; then
        report "INTEGRITY-TEST OK java-runtime ($JAVA_CMD)"
    else
        report "INTEGRITY-TEST FAIL java-runtime ($JAVA_CMD) - does not execute"
    fi
}

# =============================================================================
# Step 5 — Execution
# =============================================================================
execution() {
    SUPERVISOR_LOG=$NMF_HOME/logs/supervisor/supervisor_$(date +%F).log
    CLASSPATH="$NMF_HOME/jars-mission/$MISSION_VERSION/*:$NMF_HOME/jars-nmf/$NMF_VERSION/*"

    rm -f "$MARKER_FILE"

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

    # Non-critical console duplication of the supervisor log (BAA.02)
    tail -n 0 -f "$SUPERVISOR_LOG" &
    TAIL_PID=$!
}

# =============================================================================
# Step 6 — Confirmation (REC.01-03): wait for the Supervisor's confirmation
# marker. On failure, update the fallback state and exit non-zero; the next
# invocation of this script applies the ladder.
# =============================================================================

# Record a failed boot attempt and terminate this invocation
boot_attempt_failed() {
    kill "$TAIL_PID" 2>/dev/null
    ATTEMPTS=$((ATTEMPTS + 1))
    if [ "$ATTEMPTS" -ge "$BOOT_MAX_ATTEMPTS" ] && [ "$RUNG" != "factory" ]; then
        case "$RUNG" in
            primary)   RUNG=secondary ;;
            secondary) RUNG=factory ;;
        esac
        ATTEMPTS=0
        report "CONFIRMATION fallback: rung advanced to $RUNG"
    fi
    write_state "$RUNG" "$ATTEMPTS"
    report "CONFIRMATION fallback-state: rung=$RUNG failed-attempts=$ATTEMPTS"
    exit 1
}

# promote_after_soak — after a confirmed boot, set the secondary baseline to
# the baseline just booted (the last known-good), but only once it has stayed
# up for PROMOTION_SOAK_S. This is a promotion of the running baseline, not a
# rotation of the previous primary, so re-pointing the primary before the next
# boot never pushes an un-booted version into the secondary; and the soak keeps
# the previous known-good in the secondary until the new one proves it does not
# confirm-then-crash. Skips silently when there is nothing to promote (the
# secondary baseline was booted, or it already matches the running baseline).
promote_after_soak() {
    _selected=$BOOT_DIR/baseline-$SELECTED_ROLE.properties
    _secondary=$BOOT_DIR/baseline-secondary.properties
    if [ "$SELECTED_ROLE" = "secondary" ] || cmp -s "$_selected" "$_secondary"; then
        return
    fi

    report "CONFIRMATION baseline (nmf=$NMF_VERSION mission=$MISSION_VERSION) will be promoted to secondary in ${PROMOTION_SOAK_S}s, unless the Supervisor exits first"

    _soak=0
    while [ "$_soak" -lt "$PROMOTION_SOAK_S" ]; do
        if ! kill -0 "$JVM_PID" 2>/dev/null; then
            report "CONFIRMATION promotion aborted - Supervisor exited during the ${PROMOTION_SOAK_S}s soak; the secondary baseline is left unchanged"
            return
        fi
        sleep 1
        _soak=$((_soak + 1))
    done

    if cp "$_selected" "$_secondary.tmp" && mv "$_secondary.tmp" "$_secondary"; then
        report "CONFIRMATION promoted $SELECTED_ROLE baseline to secondary (last known-good, survived ${PROMOTION_SOAK_S}s soak)"
    else
        report "CONFIRMATION FAIL - could not promote baseline to secondary"
    fi
}

confirmation() {
    _elapsed=0
    while [ "$_elapsed" -lt "$BOOT_CONFIRM_TIMEOUT_S" ]; do
        if [ -f "$MARKER_FILE" ]; then
            # A nominal, confirmed boot is silent on the console; the timing is
            # still kept in the Boot Report file for forensics.
            record "CONFIRMATION confirmed after ${_elapsed}s"
            # A confirmed boot resets the fallback state: the next start
            # tries the primary baseline again (self-healing)
            write_state primary 0
            # Promote the running baseline to secondary only after it survives a
            # soak: confirmation proves the services started, not that the
            # baseline is stable. Soaking first keeps the previous known-good in
            # the secondary until the new one has proven it stays up.
            promote_after_soak
            return 0
        fi
        if ! kill -0 "$JVM_PID" 2>/dev/null; then
            wait "$JVM_PID" 2>/dev/null
            _code=$?
            report "CONFIRMATION FAIL - supervisor exited (code $_code) before confirming"
            boot_attempt_failed
        fi
        sleep 1
        _elapsed=$((_elapsed + 1))
    done

    report "CONFIRMATION FAIL - no confirmation within ${BOOT_CONFIRM_TIMEOUT_S}s"
    kill "$JVM_PID" 2>/dev/null
    wait "$JVM_PID" 2>/dev/null
    boot_attempt_failed
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
MARKER_FILE=$BOOT_DIR/boot-confirmed
REPORT_DIR=$NMF_HOME/logs/bootloader
REPORT_FILE=$REPORT_DIR/bootloader_$(date +%F).log
JVM_PID=""
TAIL_PID=""

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

initialisation
self_tests
baseline_selection
integrity_test
execution
confirmation

wait "$JVM_PID"
JVM_EXIT=$?
kill "$TAIL_PID" 2>/dev/null
echo "$(date "$TS_FMT") BOOTLOADER EXECUTION supervisor exited with code $JVM_EXIT"

# Exit code 90 is an intentional restart requested by the Supervisor (e.g. to
# apply a newly activated baseline), as opposed to a clean shutdown (0) or a
# crash (any other non-zero code that the fallback ladder counts). Re-execute
# the bootloader in place: it re-runs the full nominal sequence and boots the
# current primary baseline. Because the previous boot was confirmed, this is a
# fresh attempt with the full confirmation window and fallback protection.
# NOTE: keep in sync with Deployment.EXIT_RESTART.
if [ "$JVM_EXIT" -eq 90 ]; then
    report "EXECUTION restart requested (exit 90) - re-executing the bootloader"
    exec "$0"
fi
exit "$JVM_EXIT"
