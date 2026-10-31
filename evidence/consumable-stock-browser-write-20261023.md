# Consumable stock browser write replay (2026-10-23)

The narrowed named-browser replay passed 56 checks against a synthetic Oracle
database in an internal Docker network. The replay used the explicit isolated
consumable-write mode and loopback HTTP only.

Passed behavior includes:

- stock-in increments the selected row and appends history;
- inventory replaces the selected quantity and appends history;
- receive validates the scoped recipient, decrements quantity and appends history;
- abandon marks the stock discarded and appends history;
- the material aggregate is updated after each operation; and
- the fixture is restored before cleanup.

The report records `source_dirty=false`, browser-session closure, Oracle
container removal, anonymous-volume removal and internal-network removal:
[consumable-stock-browser-write-20261023.json](consumable-stock-browser-write-20261023.json).

The full equipment/consumables replay remains broader than this write boundary;
material creation, stock deletion, complete field parity, other business
import/export and `sss2` write observation remain open.
