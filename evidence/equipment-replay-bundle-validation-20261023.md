# Equipment replay bundle validation (2026-10-23)

```text
bash scripts/tests/test-replay-bundle-manifest.sh
browser equipment replay bundle: PASS (source, fixture, evidence, Oracle and cleanup hashes)
replay bundle manifest: PASS (pinned source/image, commands, evidence and cleanup boundary)
```

The validator now checks the browser bundle's Git source object, fixture SHA-256, evidence status and source identity, 48-check count, production isolation, Oracle error set, baseline projection hash, and cleanup flags. It also retains the existing equipment use and period-check bundle checks.

Final working-tree validation:

```text
git status --short --branch
## integration/sunway-framework-rebuild...origin/integration/sunway-framework-rebuild [ahead 31]
final evidence integrity: PASS
vertical slice matrix: PASS (39 cases; evidence and cleanup references valid)
vertical slice matrix negative controls: PASS (route, role, mode, syntax and cleanup)
```
