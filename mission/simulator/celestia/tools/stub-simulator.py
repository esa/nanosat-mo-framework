#!/usr/bin/env python3
# ---------------------------------------------------------------------------
# Copyright (C) 2026      European Space Agency
#                         European Space Operations Centre
#                         Darmstadt
#                         Germany
# ---------------------------------------------------------------------------
# System                : ESA NanoSat MO Framework
# ---------------------------------------------------------------------------
# Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
# ---------------------------------------------------------------------------
"""Stands in for the simulator's Celestia server.

Speaks the same line protocol as CelestiaIf, so that the Celestia side can be
worked on without starting a Supervisor, an Orekit propagator and a mission.
It flies a circle rather than an orbit: the point is to move the spacecraft
predictably, not to be right.

    ./stub-simulator.py [--port 5909] [--period 90]

It waits for Celestia to connect, greets it, and then sends a sample every
half second, waiting for the acknowledgement that the real server waits for.
"""

import argparse
import math
import socket
import sys
import time

HANDSHAKE = "connection_successful"
STOP = "connection_stop"
PROTOCOL_VERSION = "1.1"

SPACECRAFT = "OPSSAT"
ALTITUDE_KM = 6871.0  # Earth radius plus about 500 km


def sample(elapsed, period_s):
    """Position and attitude at a moment, as the real server would report them."""
    angle = 2.0 * math.pi * (elapsed % period_s) / period_s

    # A circle in the equatorial plane, tilted so it is not edge on.
    inclination = math.radians(97.5)
    x = ALTITUDE_KM * math.cos(angle)
    y = ALTITUDE_KM * math.sin(angle) * math.cos(inclination)
    z = ALTITUDE_KM * math.sin(angle) * math.sin(inclination)

    speed = 2.0 * math.pi * ALTITUDE_KM / period_s
    vx = -speed * math.sin(angle)
    vy = speed * math.cos(angle) * math.cos(inclination)
    vz = speed * math.cos(angle) * math.sin(inclination)

    # Turning slowly about one axis, so that the attitude visibly changes.
    half = angle / 2.0
    q = (math.cos(half), 0.0, 0.0, math.sin(half))

    return (x, y, z), (vx, vy, vz), q


def build_message(now, position, velocity, q):
    """The message format of CelestiaIf.buildMessage."""
    names, values, units = [], [], []

    def add(name, value, unit):
        names.append(name)
        values.append(str(value))
        units.append(unit)

    stamp = time.gmtime(now)
    add("SIM_EPOCH_TIME",
        "%d/%d/%d-%d:%d:%d" % (stamp.tm_year, stamp.tm_mon, stamp.tm_mday,
                               stamp.tm_hour, stamp.tm_min, stamp.tm_sec),
        "UTC")

    for name, value, unit in zip(("X_ICF", "Y_ICF", "Z_ICF"), position,
                                 ("km", "km", "km")):
        add(name, value, unit)
    for name, value, unit in zip(("VX_ICF", "VY_ICF", "VZ_ICF"), velocity,
                                 ("km/s", "km/s", "km/s")):
        add(name, value, unit)
    for name, value in zip(("QS_ICF", "QX_ICF", "QY_ICF", "QZ_ICF"), q):
        add(name, value, "-")

    for name in ("INFO", "ANX", "DNX", "AOS_ESOC", "LOS_ESOC"):
        add(name, "stub", "UTC")

    ids = " ".join(SPACECRAFT for _ in names)
    return ("$DATA_START$ $PROTOCOL_VERSION_%s$ %s // %s // %s // %s $DATA_END$"
            % (PROTOCOL_VERSION, ids, " ".join(names), " ".join(values),
               " ".join(units)))


def serve(port, period_s, interval_s):
    listener = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    listener.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    listener.bind(("127.0.0.1", port))
    listener.listen(1)
    print("Waiting for Celestia on 127.0.0.1:%d ..." % port, flush=True)

    while True:
        connection, address = listener.accept()
        connection.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
        print("Connected from %s:%d" % address, flush=True)
        reader = connection.makefile("r")

        try:
            connection.sendall((HANDSHAKE + "\n").encode())
            reply = reader.readline().strip()
            if not reply:
                print("No answer to the greeting, dropping.", flush=True)
                connection.close()
                continue
            print("Greeting answered with '%s'" % reply, flush=True)

            started = time.time()
            sent = 0
            while True:
                now = time.time()
                position, velocity, q = sample(now - started, period_s)
                connection.sendall((build_message(now, position, velocity, q)
                                    + "\n").encode())
                sent += 1

                reply = reader.readline().strip()
                if not reply:
                    print("Client went away after %d messages." % sent, flush=True)
                    break
                if STOP in reply:
                    print("Client asked to stop after %d messages." % sent, flush=True)
                    break
                if sent % 10 == 0:
                    print("%d messages sent, last answered with '%s'"
                          % (sent, reply), flush=True)

                time.sleep(interval_s)
        except (BrokenPipeError, ConnectionResetError):
            print("Connection lost.", flush=True)
        finally:
            connection.close()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--port", type=int, default=5909)
    parser.add_argument("--period", type=float, default=90.0,
                        help="seconds for one turn around the Earth")
    parser.add_argument("--interval", type=float, default=0.5,
                        help="seconds between messages")
    args = parser.parse_args()

    try:
        serve(args.port, args.period, args.interval)
    except KeyboardInterrupt:
        print("\nStopped.", flush=True)
        return 0
    return 0


if __name__ == "__main__":
    sys.exit(main())
