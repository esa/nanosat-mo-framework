#!/bin/sh
# =============================================================================
# Security baseline tamper end-to-end driver (run as root in the barebone
# container).
#
# Boots the real barebone Supervisor under linux-userspace isolation after
# writing the given access-control value into config.properties, which may
# differ from the posture the filesystem was generated with
# (security-baseline.properties). It then prints the security-posture lines of
# the Boot Report and of the Supervisor log, so the test can check that a
# downgrade is warned about from both places.
#
# Arguments:
#   $1  the access-control value to write into config.properties (e.g. off, enforce)
# =============================================================================
set -e
WANT=$1
NMF=/nanosat-mo-framework

# java is not on a login shell's PATH; expose it where the default PATH finds it.
JAVA_BIN=$(command -v java || echo /opt/java/openjdk/bin/java)
ln -sf "$JAVA_BIN" /usr/local/bin/java

# sudo is needed for the nmf-admin -> App user switch under linux-userspace.
command -v sudo >/dev/null 2>&1 || { apt-get update >/dev/null 2>&1; \
    apt-get install -y --no-install-recommends sudo >/dev/null 2>&1; }

cd "$NMF"
./setup_linux_userspace.sh > /tmp/setup.log 2>&1 || echo "setup exit=$?"

echo "---BASELINE---"
cat "$NMF/bootloader/security-baseline.properties" 2>/dev/null || echo "NO_BASELINE"
echo "---END---"

# Write the requested access-control value into config.properties, as a tamperer
# with write access to the bootloader directory would, leaving the root-locked
# security-baseline.properties untouched.
CONFIG="$NMF/bootloader/config.properties"
sed -i "s/^access-control=.*/access-control=$WANT/" "$CONFIG"
echo "---CONFIG---"
grep "^access-control=" "$CONFIG" || true
echo "---END---"

echo "=== start the Supervisor as nmf-admin ==="
su - nmf-admin -c "cd $NMF && ./start_supervisor.sh" > /tmp/boot.log 2>&1 &
i=0
while [ $i -lt 90 ]; do [ -f "$NMF/bootloader/boot-confirmed" ] && break; i=$((i+1)); sleep 1; done

echo "---BOOT---"
if [ -f "$NMF/bootloader/boot-confirmed" ]; then echo "CONFIRMED"; else echo "NOT_CONFIRMED"; tail -30 /tmp/boot.log; fi
echo "---END---"

echo "---BOOT-REPORT---"
grep -i "security posture" "$NMF"/logs/bootloader/bootloader_*.log 2>/dev/null || true
echo "---END---"

SUPLOG=$(ls -t "$NMF"/logs/supervisor/*.log 2>/dev/null | head -1)
echo "---SUPERVISOR-LOG---"
grep -i "security posture\|not enforced" "$SUPLOG" 2>/dev/null || true
echo "---END---"
