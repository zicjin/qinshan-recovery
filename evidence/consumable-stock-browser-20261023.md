# Consumable stock browser diagnostic (2026-10-23)

The existing equipment/consumables browser harness was extended with isolated stock controls. The replay reached the real stock page and verified the capability endpoint and all six controls. The first stock-in HTTP request was observed as successful in one replay and as HTTP 500 `GIKAM.CONSUMABLES.READ_FAILED` in another replay using the same synthetic fixture. The run is retained as diagnostic evidence and is not registered in the passing matrix.

Passed before the unstable step:

- scoped consumable list, detail and stock/log reads;
- isolated stock-write capability probe;
- visible stock operation controls;
- real stock-in request observation and Oracle aggregate update in the successful attempt.

Open failure: the page-side stock write/refresh chain is not yet deterministic enough for acceptance. No production endpoint was used; Oracle container, network and browser resources were cleaned after the replay. The existing 44-check isolated HTTP stock-write contract remains the authoritative passing write boundary.
