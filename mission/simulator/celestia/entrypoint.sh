#!/bin/sh
#
# Starts Celestia inside the container.
#
# The package ships more than one front end and which of them provides the
# plain "celestia" command has changed between releases, so the one that is
# there is picked rather than assumed.
#
set -eu

if [ -z "${DISPLAY:-}" ]; then
    echo "celestia: DISPLAY is not set. The container needs an X display from" >&2
    echo "the host; run it through run.sh, which passes one in." >&2
    exit 1
fi

# Qt complains and picks a directory of its own when this is unset. Given one
# it can write to, with the permissions it expects, it stays quiet.
if [ -z "${XDG_RUNTIME_DIR:-}" ]; then
    XDG_RUNTIME_DIR=/tmp/runtime-$(id -u)
    export XDG_RUNTIME_DIR
fi
mkdir -p "${XDG_RUNTIME_DIR}" 2>/dev/null || true
chmod 700 "${XDG_RUNTIME_DIR}" 2>/dev/null || true

# Celestia reads its add-ons from the directories its configuration names, and
# finds no configuration of its own in this container, so one is made here from
# the file the package ships. Two things are changed in it: the directory the
# spacecraft and ground stations are mounted in is added, and scripts are
# allowed to reach the system, without which the module that talks to the
# simulator cannot open a socket.
#
# It is derived at startup rather than kept beside this script so that it
# follows whatever version of Celestia is installed, and so that a file
# belonging to Celestia is not carried around in this repository.
EXTRAS_DIR="${CELESTIA_EXTRAS_DIR:-${HOME}/.local/share/celestia/extras}"
PACKAGED_CONFIG=/usr/share/celestia/celestia.cfg
CONFIG="${XDG_RUNTIME_DIR}/celestia.cfg"

CONFIG_ARGS=""
if [ -f "${PACKAGED_CONFIG}" ]; then
    # The LuaHook is added after the line that opens the Configuration block.
    # Celestia names it as a file rather than as a module, which is why it is
    # used: it puts the add-on directory on the module search path before the
    # spacecraft definition asks for anything in it.
    HOOK="${CELESTIA_LUA_HOOK:-${HOME}/luahook.lua}"
    HOOK_LINE=""
    if [ -f "${HOOK}" ]; then
        HOOK_LINE="  LuaHook \"${HOOK}\""
    fi

    # The script that selects the spacecraft is named in the configuration
    # rather than on the command line: this build takes no --url.
    STARTUP="${CELESTIA_STARTUP_SCRIPT:-${HOME}/startup.cel}"
    INIT_SED="s|^\([[:space:]]*\)InitScript.*|\1InitScript \"${STARTUP}\"|"
    if [ ! -f "${STARTUP}" ]; then
        INIT_SED="s|^\([[:space:]]*\)InitScript.*|\1InitScript \"start.cel\"|"
    fi

    sed -e "s|^\([[:space:]]*\)ExtrasDirectories.*|\1ExtrasDirectories [ \"extras-standard\" \"extras\" \"${EXTRAS_DIR}\" ]|" \
        -e "s|^\([[:space:]]*\)ScriptSystemAccessPolicy.*|\1ScriptSystemAccessPolicy \"allow\"|" \
        -e "${INIT_SED}" \
        -e "0,/^{/s|^{|{\n${HOOK_LINE}|" \
        "${PACKAGED_CONFIG}" > "${CONFIG}"
    CONFIG_ARGS="--conf ${CONFIG}"
else
    echo "celestia: ${PACKAGED_CONFIG} is missing, so the add-ons will not be" >&2
    echo "loaded and the spacecraft will not appear." >&2
fi

for binary in celestia celestia-qt6 celestia-sdl; do
    if command -v "$binary" > /dev/null 2>&1; then
        # shellcheck disable=SC2086
        exec "$binary" $CONFIG_ARGS "$@"
    fi
done

echo "celestia: no Celestia binary found in the image. Tried: celestia," >&2
echo "celestia-qt6, celestia-sdl." >&2
exit 1
