#!/usr/bin/env python3
"""Shared readiness polling for isolated owned-runtime runners.

The runners must wait for an observable condition rather than a fixed sleep.
The clock and sleeper are injectable so the retry contract can be tested
without starting Docker or waiting in real time.
"""
import time


class ReadinessTimeout(RuntimeError):
    """Raised when a readiness predicate does not become true in time."""


def wait_until(label, predicate, *, timeout, interval, clock=time.monotonic,
               sleeper=time.sleep):
    """Poll ``predicate`` until it returns a truthy value.

    Predicate exceptions are treated as a not-ready result and included in the
    timeout diagnostic.  A successful result contains elapsed milliseconds,
    attempt count and the predicate value for evidence callers.
    """
    started = clock()
    deadline = started + timeout
    attempts = 0
    last = None
    while True:
        attempts += 1
        try:
            value = predicate()
            last = value
        except Exception as failure:  # readiness probes commonly race startup
            last = type(failure).__name__ + ': ' + str(failure)
            value = False
        if value:
            return {
                'elapsed_ms': round((clock() - started) * 1000, 3),
                'attempts': attempts,
                'value': value,
            }
        remaining = deadline - clock()
        if remaining <= 0:
            raise ReadinessTimeout(
                '%s timeout after %.3fs (attempts=%d, last=%s)' %
                (label, timeout, attempts, last))
        sleeper(min(interval, remaining))


def record_phase(report, name, started, clock=time.perf_counter):
    """Record one phase duration in the report's shared timing object."""
    report.setdefault('phase_timings_ms', {})[name] = round(
        (clock() - started) * 1000, 3)

