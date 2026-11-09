#!/usr/bin/env python3
"""Transport CLI and actual socket seams; no Docker, SSH, or runtime services."""
import contextlib
import os
from pathlib import Path
import select
import socket
import subprocess
import sys
import threading
import unittest

SCRIPT = Path(__file__).resolve().parents[1] / "local-runtime-transport.py"


class LocalRuntimeTransportTest(unittest.TestCase):
    @contextlib.contextmanager
    def upstream(self, exchange):
        errors = []
        with socket.socket() as server:
            server.bind(("127.0.0.1", 0))
            server.listen()
            server.settimeout(10)

            def run():
                try:
                    with server.accept()[0] as conn:
                        conn.settimeout(10)
                        exchange(conn)
                except BaseException as error:
                    errors.append(error)

            thread = threading.Thread(target=run, daemon=True)
            thread.start()
            try:
                yield server.getsockname()[1]
            finally:
                thread.join(12)
                self.assertFalse(thread.is_alive(), "upstream thread did not finish")
                if errors:
                    raise errors[0]

    def command(self, listen_port, target_port, *extra):
        return [sys.executable, str(SCRIPT), "--listen-port", str(listen_port),
                "--target-host", "127.0.0.1", "--target-port", str(target_port), *extra]

    @contextlib.contextmanager
    def transport(self, target_port, *extra):
        with socket.socket() as reservation:
            reservation.bind(("127.0.0.1", 0))
            listen_port = reservation.getsockname()[1]
        env = dict(os.environ, HTTP_PROXY="http://127.0.0.1:1",
                   HTTPS_PROXY="http://127.0.0.1:1", ALL_PROXY="http://127.0.0.1:1")
        proc = subprocess.Popen(self.command(listen_port, target_port, *extra), env=env,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        try:
            self.assertTrue(select.select([proc.stdout], [], [], 5)[0], "no startup status")
            self.assertEqual(proc.stdout.readline().strip(), f"listening on 127.0.0.1:{listen_port}")
            yield listen_port, proc
        finally:
            if proc.poll() is None:
                proc.terminate()
            stdout, stderr = proc.communicate(timeout=8)
            self.assertEqual(proc.returncode, 0, (stdout, stderr))
            self.assertEqual(stderr, "")
            self.assertEqual(stdout, "")

    @staticmethod
    def receive_all(conn):
        chunks = []
        while True:
            chunk = conn.recv(65536)
            if not chunk:
                return b"".join(chunks)
            chunks.append(chunk)

    def test_preserves_binary_bytes_without_interpreting_headers_or_proxy_environment(self):
        request = b"GET / HTTP/1.1\r\nAuthorization: opaque\r\n\r\n\x00\xff\xfe" + bytes(range(256)) * 1024
        response = b"HTTP/1.0 299 odd\n\n\xff\x00\x80" + bytes(reversed(range(256))) * 1024

        def exchange(conn):
            received = bytearray()
            while len(received) < len(request):
                data = conn.recv(65536)
                self.assertTrue(data, "request stream ended early")
                received.extend(data)
            self.assertEqual(bytes(received), request)
            conn.sendall(response)

        with self.upstream(exchange) as target_port, self.transport(target_port) as (port, _):
            with socket.create_connection(("127.0.0.1", port), timeout=5) as conn:
                conn.sendall(request)
                self.assertEqual(self.receive_all(conn), response)

    def test_client_half_close_delivers_entire_response_after_upstream_eof(self):
        request = b"request that needs EOF\x00\xff"
        response = b"response after request EOF\xfe"

        def exchange(conn):
            self.assertEqual(self.receive_all(conn), request)
            conn.sendall(response)

        with self.upstream(exchange) as target_port, self.transport(target_port) as (port, _):
            with socket.create_connection(("127.0.0.1", port), timeout=5) as conn:
                conn.sendall(request)
                conn.shutdown(socket.SHUT_WR)
                self.assertEqual(self.receive_all(conn), response)

    def test_upstream_half_close_preserves_client_write_direction(self):
        def exchange(conn):
            conn.sendall(b"ready")
            conn.shutdown(socket.SHUT_WR)
            self.assertEqual(self.receive_all(conn), b"sent after response EOF")

        with self.upstream(exchange) as target_port, self.transport(target_port) as (port, _):
            with socket.create_connection(("127.0.0.1", port), timeout=5) as conn:
                self.assertEqual(self.receive_all(conn), b"ready")
                conn.sendall(b"sent after response EOF")
                conn.shutdown(socket.SHUT_WR)

    def test_slow_response_outlives_connection_establishment_timeout(self):
        def exchange(conn):
            self.assertEqual(self.receive_all(conn), b"slow")
            threading.Event().wait(0.3)
            conn.sendall(b"still connected")

        with self.upstream(exchange) as target_port:
            with self.transport(target_port, "--connect-timeout", "0.05") as (port, _):
                with socket.create_connection(("127.0.0.1", port), timeout=5) as conn:
                    conn.sendall(b"slow")
                    conn.shutdown(socket.SHUT_WR)
                    self.assertEqual(self.receive_all(conn), b"still connected")

    def test_termination_closes_idle_connections_and_releases_listener(self):
        accepted = threading.Event()

        def exchange(conn):
            accepted.set()
            self.assertEqual(self.receive_all(conn), b"")

        with self.upstream(exchange) as target_port, self.transport(target_port) as (port, proc):
            with socket.create_connection(("127.0.0.1", port), timeout=5) as conn:
                self.assertTrue(accepted.wait(5))
                proc.terminate()
                self.assertEqual(proc.wait(timeout=3), 0)
                self.assertEqual(conn.recv(1), b"")
            with socket.socket() as replacement:
                replacement.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
                replacement.bind(("127.0.0.1", port))
                replacement.listen()

    def test_failed_upstream_connection_does_not_stop_foreground_listener(self):
        with socket.socket() as reservation:
            reservation.bind(("127.0.0.1", 0))
            target_port = reservation.getsockname()[1]
            with self.transport(target_port) as (port, proc):
                with socket.create_connection(("127.0.0.1", port), timeout=5) as conn:
                    self.assertEqual(conn.recv(1), b"")
                self.assertIsNone(proc.poll())
                reservation.listen()
                with socket.create_connection(("127.0.0.1", port), timeout=5) as conn:
                    reservation.settimeout(5)
                    with reservation.accept()[0] as upstream:
                        upstream.sendall(b"recovered")
                        upstream.shutdown(socket.SHUT_WR)
                        self.assertEqual(self.receive_all(conn), b"recovered")
                        conn.shutdown(socket.SHUT_WR)

    def test_existing_listener_conflict_fails_without_stealing_port(self):
        with socket.socket() as occupied:
            occupied.bind(("127.0.0.1", 0))
            occupied.listen()
            result = subprocess.run(self.command(occupied.getsockname()[1], 12345),
                                    capture_output=True, text=True, timeout=5)
            self.assertEqual(result.returncode, 1)
            self.assertEqual(result.stdout, "")
            self.assertEqual(result.stderr.strip(), "transport listener failed")

    def test_rejects_nonloopback_listener_nonprivate_targets_and_invalid_ports(self):
        invalid = [
            ("--listen-host", "0.0.0.0"), ("--listen-host", "192.168.1.1"),
            ("--listen-host", "::1"), ("--target-host", "8.8.8.8"),
            ("--target-host", "0.0.0.0"), ("--target-host", "169.254.1.1"),
            ("--target-host", "224.0.0.1"), ("--target-host", "240.0.0.1"),
            ("--target-host", "192.0.2.1"), ("--target-host", "::1"),
            ("--target-host", "::ffff:127.0.0.1"), ("--target-host", "localhost"),
            ("--listen-port", "0"), ("--target-port", "65536"),
            ("--listen-port", "not-a-port"), ("--connect-timeout", "nan"),
            ("--connect-timeout", "inf"), ("--connect-timeout", "0"),
        ]
        for extra in invalid:
            with self.subTest(extra=extra):
                result = subprocess.run(self.command(12345, 12346, *extra),
                                        capture_output=True, text=True, timeout=5)
                self.assertEqual(result.returncode, 2)
                self.assertEqual(result.stdout, "")
                self.assertIn("error:", result.stderr)

    def test_requires_explicit_ports_and_target(self):
        for missing in ("--listen-port", "--target-host", "--target-port"):
            command = self.command(12345, 12346)
            index = command.index(missing)
            del command[index:index + 2]
            with self.subTest(missing=missing):
                result = subprocess.run(command, capture_output=True, text=True, timeout=5)
                self.assertEqual(result.returncode, 2)
                self.assertIn(missing, result.stderr)


if __name__ == "__main__":
    unittest.main()
