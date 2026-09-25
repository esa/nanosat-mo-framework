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
"""Listens in on what the simulator is sending to Celestia.

Listens for the simulator exactly as Celestia does, answers every message so
the simulator keeps sending, and prints the attitude and position it is given. Nothing is drawn: this is for finding out whether the
simulator is still changing the attitude, which cannot be told apart from
Celestia failing to apply it by looking at Celestia.

The simulator dials one Celestia, and this takes the port Celestia would take,
so run this instead of Celestia, not alongside it.

    ./tap-simulator.py

It prints a line whenever the attitude changes, and says so when it stops
changing, which is the thing worth knowing.
"""

import argparse
import socket
import sys
import time

ACK = "connection_alive"
HANDSHAKE = "connection_successful"


def parse(message):
    """Pulls the parameters out of a message, as the Lua client does."""
    if "$DATA_START$" not in message:
        return None
    sections = message.split(" //")
    if len(sections) < 3:
        return None
    names = sections[1].split()
    values = sections[2].split()
    return dict(zip(names, values))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=5909)
    parser.add_argument("--quiet-after", type=float, default=5.0,
                        help="seconds of an unchanging attitude before saying so")
    args = parser.parse_args()

    # Celestia is the server, so standing in for it means listening and letting
    # the simulator dial in - the same way round as the real thing.
    listener = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    listener.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    listener.bind((args.host, args.port))
    listener.listen(1)
    print("Waiting for the simulator on %s:%d ..." % (args.host, args.port), flush=True)

    connection, address = listener.accept()
    connection.settimeout(20)
    print("Simulator connected from %s:%d" % address, flush=True)
    reader = connection.makefile("r")

    greeting = reader.readline().strip()
    if HANDSHAKE not in greeting:
        print("Unexpected greeting: %r" % greeting)
        return 1
    print("Connected. Greeting: %s" % greeting, flush=True)
    connection.sendall((ACK + "\n").encode())

    previous = None
    last_change = time.time()
    reported_stuck = False
    count = 0

    while True:
        line = reader.readline()
        if not line:
            print("The simulator closed the connection after %d messages." % count, flush=True)
            return 0
        count += 1
        connection.sendall((ACK + "\n").encode())

        parameters = parse(line.strip())
        if parameters is None:
            continue

        attitude = tuple(parameters.get(name) for name in
                         ("QS_ICF", "QX_ICF", "QY_ICF", "QZ_ICF"))
        if any(value is None for value in attitude):
            print("[%5d] attitude missing from the message" % count, flush=True)
            continue

        unparsable = [value for value in attitude
                      if value.lower() in ("nan", "-nan", "inf", "-inf")]
        if unparsable:
            print("[%5d] attitude is not a number: %s   <-- Celestia would freeze here"
                  % (count, " ".join(attitude)), flush=True)
            continue

        if attitude != previous:
            print("[%5d] %s  q = %s" % (count, parameters.get("SIM_EPOCH_TIME", "?"),
                                        " ".join(attitude)), flush=True)
            previous = attitude
            last_change = time.time()
            reported_stuck = False
        elif not reported_stuck and time.time() - last_change > args.quiet_after:
            print("[%5d] attitude unchanged for %.0f s, still receiving messages"
                  % (count, args.quiet_after), flush=True)
            reported_stuck = True


if __name__ == "__main__":
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        print("\nStopped.")
    except OSError as err:
        print("Could not take the port (%s). Is Celestia already running?" % err)
        sys.exit(1)
