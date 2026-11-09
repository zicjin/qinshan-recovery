#!/usr/bin/env python3
"""Foreground TCP byte transport for a local runtime; loopback by default.

An explicit RFC1918 listen address enables access through that LAN interface.

This transports bytes without HTTP parsing, normalization, authentication, or
proxy-environment handling. It is neither HTTP evidence nor runtime lifecycle
approval and supplies no compatibility/business behavior. Direct bridging to a
container leaves the application's remoteAddr at the Docker gateway; it does
not establish that the application's loopback write guard succeeds.

SIGINT/SIGTERM stop accepting and close active streams. A connection timeout
bounds only connection establishment; connected streams have no idle timeout.
"""
import argparse
import ipaddress
import math
import signal
import socket
import sys
import threading

PRIVATE_NETWORKS = tuple(ipaddress.IPv4Network(cidr) for cidr in (
    "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16", "127.0.0.0/8",
))


def private_ipv4(value):
    try:
        address = ipaddress.IPv4Address(value)
    except ipaddress.AddressValueError:
        raise argparse.ArgumentTypeError("target must be a literal private IPv4 address") from None
    if not any(address in network for network in PRIVATE_NETWORKS):
        raise argparse.ArgumentTypeError("target must be RFC1918 or IPv4 loopback")
    return str(address)


def port_number(value):
    try:
        port = int(value)
    except ValueError:
        raise argparse.ArgumentTypeError("port must be an integer from 1 to 65535") from None
    if not 1 <= port <= 65535:
        raise argparse.ArgumentTypeError("port must be an integer from 1 to 65535")
    return port


def positive_timeout(value):
    try:
        timeout = float(value)
    except ValueError:
        raise argparse.ArgumentTypeError("connect timeout must be positive and finite") from None
    if not math.isfinite(timeout) or timeout <= 0:
        raise argparse.ArgumentTypeError("connect timeout must be positive and finite")
    return timeout


def shutdown(sock, how):
    try:
        sock.shutdown(how)
    except OSError:
        pass


def relay(source, destination):
    try:
        while True:
            data = source.recv(65536)
            if not data:
                # EOF ends only this direction: the peer may still respond.
                shutdown(destination, socket.SHUT_WR)
                return
            destination.sendall(data)
    except OSError:
        # A failed stream cannot be recovered; release the opposite relay too.
        shutdown(source, socket.SHUT_RDWR)
        shutdown(destination, socket.SHUT_RDWR)


def run(args):
    stopping = threading.Event()
    lock = threading.Lock()
    active = set()
    workers = set()

    def stop(_signum, _frame):
        stopping.set()

    signal.signal(signal.SIGTERM, stop)
    signal.signal(signal.SIGINT, stop)

    def serve(client):
        upstream = None
        try:
            upstream = socket.create_connection((args.target_host, args.target_port),
                                                timeout=args.connect_timeout)
            upstream.settimeout(None)
            with lock:
                active.add(upstream)
            if stopping.is_set():
                return
            response = threading.Thread(target=relay, args=(upstream, client))
            response.start()
            relay(client, upstream)
            response.join()
        except OSError:
            pass
        finally:
            for sock in (client, upstream):
                if sock is not None:
                    shutdown(sock, socket.SHUT_RDWR)
                    sock.close()
                    with lock:
                        active.discard(sock)
            with lock:
                workers.discard(threading.current_thread())

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as listener:
        listener.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        listener.bind((args.listen_host, args.listen_port))
        listener.listen(32)
        listener.settimeout(0.2)
        print(f"listening on {args.listen_host}:{args.listen_port}", flush=True)
        try:
            while not stopping.is_set():
                try:
                    client, _ = listener.accept()
                except socket.timeout:
                    continue
                client.settimeout(None)
                worker = threading.Thread(target=serve, args=(client,))
                with lock:
                    active.add(client)
                    workers.add(worker)
                worker.start()
        finally:
            stopping.set()
            listener.close()
            with lock:
                sockets = list(active)
                threads = list(workers)
            for sock in sockets:
                shutdown(sock, socket.SHUT_RDWR)
            for worker in threads:
                worker.join()
    return 0


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--listen-host", type=private_ipv4, default="127.0.0.1",
                        help="IPv4 loopback or an explicit RFC1918 interface address")
    parser.add_argument("--listen-port", required=True, type=port_number)
    parser.add_argument("--target-host", required=True, type=private_ipv4)
    parser.add_argument("--target-port", required=True, type=port_number)
    parser.add_argument("--connect-timeout", default=5.0, type=positive_timeout,
                        help="seconds for connection establishment only (default: 5)")
    args = parser.parse_args()
    try:
        return run(args)
    except OSError:
        print("transport listener failed", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
