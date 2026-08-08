#!/bin/sh
#
# Runs Celestia from the container built by the Dockerfile beside this script,
# so that nothing has to be installed on the host.
#
# The image is built on first use. Pass --rebuild to build it again.
#
# The container is given the host network, for two reasons: the simulator's
# Celestia server listens on the host, so 127.0.0.1:5909 has to mean the same
# thing on both sides, and it makes the X display reachable without further
# arrangement.
#
set -eu

HERE=$(cd "$(dirname "$0")" && pwd)
IMAGE=nmf/celestia
PORT=${CELESTIA_PORT:-5909}

# Celestia opens a small window of its own accord, which is rarely what is
# wanted. By default it is given the size of the desktop instead, leaving the
# panels of the desktop where they are. Its own --fullscreen takes over the
# whole screen and hides them, which is a different thing and is offered here
# under the same name.
#
# Anything given to this script that it does not recognise is passed on to
# Celestia, so its own options still work.
REBUILD=0
MODE=maximised
while [ $# -gt 0 ]; do
    case "$1" in
        --rebuild)
            REBUILD=1
            shift
            ;;
        --fullscreen)
            MODE=fullscreen
            shift
            ;;
        --windowed)
            MODE=windowed
            shift
            ;;
        *)
            break
            ;;
    esac
done

if [ "$REBUILD" = "1" ] || ! docker image inspect "$IMAGE" > /dev/null 2>&1; then
    echo "Building $IMAGE ..."
    docker build -t "$IMAGE" "$HERE"
fi

if [ -z "${DISPLAY:-}" ]; then
    echo "DISPLAY is not set: there is no X display to draw on." >&2
    exit 1
fi

# The container draws on the host's X server through its socket. The host has
# to allow it, which is what this does for local connections only. It is
# undone on exit.
XHOST_ADDED=0
if command -v xhost > /dev/null 2>&1; then
    if xhost +local: > /dev/null 2>&1; then
        XHOST_ADDED=1
    fi
else
    echo "Note: xhost was not found. If Celestia cannot open the display," >&2
    echo "install x11-xserver-utils, or allow local connections by hand." >&2
fi

cleanup() {
    if [ "$XHOST_ADDED" = "1" ]; then
        xhost -local: > /dev/null 2>&1 || true
    fi
}
trap cleanup EXIT INT TERM

# Hardware rendering when the host has a card to share, and Mesa's software
# renderer when it does not. Celestia needs OpenGL either way.
GPU_ARGS=""
if [ -d /dev/dri ]; then
    GPU_ARGS="--device=/dev/dri"
else
    echo "No /dev/dri on the host: falling back to software rendering."
    GPU_ARGS="-e LIBGL_ALWAYS_SOFTWARE=1"
fi

# The X cookie, when the display is protected by one. Mounted at the same path
# it has on the host, so that the variable passed in still points at it.
XAUTH_ARGS=""
if [ -n "${XAUTHORITY:-}" ] && [ -f "${XAUTHORITY}" ]; then
    XAUTH_ARGS="-v ${XAUTHORITY}:${XAUTHORITY}:ro"
fi

# Mounted below; created here so that the mount does not make it as root.
mkdir -p "$HERE/extras"

# Celestia's settings, bookmarks and window state, kept on the host so that they
# outlive the container. It is not put in the repository, since it is state of
# this machine rather than anything to be shared. The path Celestia writes into
# is created here as well, so that the extras mount below does not land on a
# directory made by Docker and owned by root.
STATE_DIR="${XDG_DATA_HOME:-$HOME/.local/share}/nmf-celestia"
mkdir -p "$STATE_DIR/.local/share/celestia"

# A terminal is asked for only when there is one to ask for, so that the script
# still runs when it is called from something that has no TTY.
TTY_ARGS=
if [ -t 0 ]; then
    TTY_ARGS="-it"
fi

# The area the desktop leaves for windows, which is the screen less whatever
# panels are on it. Window managers publish it, so it does not have to be
# guessed at, and a window given exactly it looks maximised without having to
# ask the window manager to maximise anything.
work_area_geometry() {
    command -v xprop > /dev/null 2>&1 || return 1
    # _NET_WORKAREA(CARDINAL) = 0, 0, 3840, 1035, 0, 0, 3840, 1035, ...
    # one set of four per desktop; the first is the one in use.
    set -- $(xprop -root _NET_WORKAREA 2>/dev/null | sed 's/.*= //' | tr -d ' ' | tr ',' ' ')
    [ $# -ge 4 ] || return 1
    [ "$3" -gt 0 ] 2>/dev/null || return 1
    [ "$4" -gt 0 ] 2>/dev/null || return 1
    echo "$3x$4+$1+$2"
}

# Celestia 1.6 takes no option for the window size, so "maximised" is left to
# the window manager and only full screen can be asked for outright.
CELESTIA_ARGS="--nosplash"
case "$MODE" in
    fullscreen)
        CELESTIA_ARGS="$CELESTIA_ARGS --fullscreen"
        ;;
esac

echo "Celestia expects the simulator's Celestia server on 127.0.0.1:$PORT."
echo "Start the Supervisor Simulator with celestia=true and orekit=true first;"
echo "see README.md."

# shellcheck disable=SC2086
exec docker run --rm $TTY_ARGS \
    --name nmf-celestia \
    --network host \
    --user "$(id -u):$(id -g)" \
    -e DISPLAY \
    -e XAUTHORITY \
    -e HOME=/home/celestia \
    -e CELESTIA_PORT="$PORT" \
    -v /tmp/.X11-unix:/tmp/.X11-unix:ro \
    -v "$STATE_DIR:/home/celestia" \
    -v "$HERE/extras:/home/celestia/.local/share/celestia/extras:ro" \
    -v "$HERE/startup.cel:/home/celestia/startup.cel:ro" \
    -v "$HERE/luahook.lua:/home/celestia/luahook.lua:ro" \
    $XAUTH_ARGS \
    $GPU_ARGS \
    "$IMAGE" \
    $CELESTIA_ARGS \
    --extrasdir=/home/celestia/.local/share/celestia/extras \
    "$@"
