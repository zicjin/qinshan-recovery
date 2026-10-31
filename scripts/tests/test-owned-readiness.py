#!/usr/bin/env python3
"""Fast contract tests for shared isolated-runner readiness polling."""
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'scripts'))
from owned_readiness import ReadinessTimeout, record_phase, wait_until


class FakeClock:
    def __init__(self):
        self.value = 0.0
        self.sleeps = []

    def now(self):
        return self.value

    def sleep(self, duration):
        self.sleeps.append(duration)
        self.value += duration


def main():
    clock = FakeClock()
    attempts = []

    def probe():
        attempts.append(clock.value)
        return len(attempts) == 3

    result = wait_until('synthetic data plane', probe, timeout=10,
                        interval=2, clock=clock.now, sleeper=clock.sleep)
    if result['attempts'] != 3 or result['elapsed_ms'] != 4000.0:
        raise SystemExit('successful readiness timing/attempt contract failed')
    if clock.sleeps != [2, 2]:
        raise SystemExit('readiness did not use bounded retry intervals')

    timeout_clock = FakeClock()
    try:
        wait_until('never ready', lambda: False, timeout=3, interval=2,
                   clock=timeout_clock.now, sleeper=timeout_clock.sleep)
    except ReadinessTimeout as failure:
        if 'never ready timeout' not in str(failure) or timeout_clock.sleeps != [2, 1]:
            raise SystemExit('timeout diagnostic or deadline clamp failed')
    else:
        raise SystemExit('readiness timeout did not fail closed')

    report = {}
    record_phase(report, 'probe', 1.0, clock=lambda: 1.125)
    if report.get('phase_timings_ms') != {'probe': 125.0}:
        raise SystemExit('phase timing record contract failed')
    print('owned readiness helper: PASS (retry, timeout, bounded interval and phase timing)')


if __name__ == '__main__':
    main()
