#!/bin/sh
# =============================================================================
# Access control end-to-end driver (run as root inside the barebone container).
#
# Brings up the real barebone Supervisor under linux-userspace isolation, in the
# given access control mode, then has it install and run the permissions probe
# App exactly as a mission would: PackageManagement installs the .nmfpackage,
# acknowledging the given permissions, and AppsLauncher runs the App. The probe
# reports, for each service it calls, whether the call was let through. That
# report is printed between the ---REPORT--- and ---END--- markers, whether the
# App was installed between ---INSTALL--- and ---END---, the access control
# lines of the Supervisor's log between ---SUPERVISOR-LOG--- and ---END---, and
# a JSON dump of the Supervisor's Archive between ---ARCHIVE--- and ---END---,
# and whether calls from ground with and without the ground credential got
# through between ---GROUND--- and ---END---, for the test to read.
#
# Arguments:
#   $1  the access control mode: off, audit or enforce
#   $2  the permissions to acknowledge at install, comma-separated, or "-" for none
# =============================================================================
set -e
MODE=$1
GRANTS=$2
NMF=/nanosat-mo-framework
CLI="$NMF/security-tools/cli-tool.jar"
APP_NAME=permissions-probe
REPORT=/tmp/permissions-probe-report
# The ground credential that the filesystem was generated with: every CLI call
# below presents it, as an operator on ground would
CRED_FILE="$NMF/bootloader/ground.credential"
CRED="--credential-file $CRED_FILE"

echo "=== mode: $MODE, acknowledged: $GRANTS ==="

# java is not on a login shell's PATH nor in the environment the Supervisor
# gives a launched App, so expose it where the default PATH finds it.
JAVA_BIN=$(command -v java || echo /opt/java/openjdk/bin/java)
ln -sf "$JAVA_BIN" /usr/local/bin/java

# sudo is needed for the nmf-admin -> App user switch under linux-userspace.
command -v sudo >/dev/null 2>&1 || { apt-get update >/dev/null 2>&1; \
    apt-get install -y --no-install-recommends sudo >/dev/null 2>&1; }

cd "$NMF"
./setup_linux_userspace.sh > /tmp/setup.log 2>&1 || echo "setup exit=$?"
# Set the mode, replacing the one the filesystem was generated with
CONFIG="$NMF/bootloader/config.properties"
if grep -q "^access-control=" "$CONFIG"; then
    sed -i "s/^access-control=.*/access-control=$MODE/" "$CONFIG"
else
    echo "access-control=$MODE" >> "$CONFIG"
fi

echo "=== start the Supervisor as nmf-admin ==="
su - nmf-admin -c "cd $NMF && ./start_supervisor.sh" > /tmp/boot.log 2>&1 &
i=0
while [ $i -lt 90 ]; do [ -f "$NMF/bootloader/boot-confirmed" ] && break; i=$((i+1)); sleep 1; done
SUPLOG=$(ls -t "$NMF"/logs/supervisor/*.log 2>/dev/null | head -1)
URI=$(grep -o 'maltcp://[^ ]*-Directory' "$SUPLOG" 2>/dev/null | head -1)
echo "Directory URI = $URI"
[ -z "$URI" ] && { echo "NO URI - boot log tail:"; tail -30 /tmp/boot.log; exit 2; }

# The Supervisor lists packages by file name, so install by the .nmfpack name.
PKG=$(basename "$(ls "$NMF"/packages/$APP_NAME-*.nmfpack | head -1)")
# The Supervisor requires the checksum approved on ground, as in flight
SHA256=$(sha256sum "$NMF/packages/$PKG" | cut -d" " -f1)
GRANT_OPTION=""
[ "$GRANTS" != "-" ] && GRANT_OPTION="--grant $GRANTS"
echo "=== install $PKG $GRANT_OPTION ==="
java -jar "$CLI" software-management install "$PKG" --sha256 "$SHA256" $GRANT_OPTION $CRED -r "$URI" 2>&1 \
    | grep -i "installed\|error\|severe\|refused" | head -5 || true

echo "---INSTALL---"
if [ -f "$NMF/apps/$APP_NAME/start_app.sh" ]; then echo "INSTALLED=yes"; else echo "INSTALLED=no"; fi
echo "---END---"

echo "---REPORT---"
if [ -f "$NMF/apps/$APP_NAME/start_app.sh" ]; then
    java -jar "$CLI" apps-launcher run "$APP_NAME" $CRED -r "$URI" 2>&1 | grep -i "error\|severe" | head -5 || true
    i=0
    while [ $i -lt 60 ]; do [ -f "$REPORT" ] && break; i=$((i+1)); sleep 1; done
    cat "$REPORT" 2>/dev/null || echo "NO_REPORT"
else
    echo "NO_REPORT"
fi
echo "---END---"

# A call from ground, without and with the ground credential
# ALLOWED only on evidence that the call was answered: the package is listed
outcome() {
    if echo "$1" | grep -q -i "Authentication Failed\|Authorisation Fail\|AuthenticationFailed\|AuthorisationFail"; then echo "REFUSED";
    elif echo "$1" | grep -q "Package name: $PKG"; then echo "ALLOWED";
    else echo "ERROR: $(echo "$1" | grep -i "error\|exception\|required\|not found" | head -2 | tr '\n' ' ')"; fi
}
echo "---GROUND---"
OUT=$(java -jar "$CLI" software-management findPackage "$PKG" -p nanosat-mo-supervisor -r "$URI" 2>&1 || true)
echo "WITHOUT_CREDENTIAL=$(outcome "$OUT")"
OUT=$(java -jar "$CLI" software-management findPackage "$PKG" $CRED -p nanosat-mo-supervisor -r "$URI" 2>&1 || true)
echo "WITH_CREDENTIAL=$(outcome "$OUT")"
# As the playground's tools do: the ground credential file given as a system property
OUT=$(java -Desa.mo.nmf.accesscontrol.groundCredentialFile="$CRED_FILE" -jar "$CLI" software-management \
    findPackage "$PKG" -p nanosat-mo-supervisor -r "$URI" 2>&1 || true)
echo "WITH_GROUND_PROPERTY=$(outcome "$OUT")"
echo "---END---"

echo "---APP-LOG---"
grep -h -i "missing permission\|audit" "$NMF"/logs/app_$APP_NAME/*.log 2>/dev/null || true
echo "---END---"

# Without a report, the end of the probe's log says why
if [ ! -f "$REPORT" ]; then
    echo "---NO-REPORT-LOG---"
    tail -40 "$NMF"/logs/app_$APP_NAME/*.log 2>/dev/null || true
    echo "---END---"
fi

echo "---SUPERVISOR-LOG---"
grep -i -E "refused|audit|authoris|authentic|acknowledg" "$SUPLOG" 2>/dev/null || true
echo "---END---"

# The Supervisor's Archive is read from its database file, in the Supervisor's
# own storage, so that no MO call is needed
DB=$(find /home -path "*nanosat-mo-supervisor*" -name "comArchive*.db" 2>/dev/null | head -1)
echo "---ARCHIVE---"
if [ -n "$DB" ]; then
    java -jar "$CLI" archive dump /tmp/archive.json -l "$DB" > /tmp/archive-dump.log 2>&1 || true
    cat /tmp/archive.json 2>/dev/null || { echo "NO_DUMP"; tail -20 /tmp/archive-dump.log; }
else
    echo "NO_ARCHIVE"
fi
echo "---END---"
