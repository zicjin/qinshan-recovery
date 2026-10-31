# Owned distribution package evidence (2026-10-20; refreshed 2026-10-21)

- Source commit: `95dbf7db43250fb3b71afee7b57033df049c7586`; worktree clean during the browser run (`source_dirty=false`).
- The integrated attachment frontend is present in the distribution. Package checks passed at `239d4e6`; runtime/build/package inputs are unchanged at `95dbf7d`, where the final browser run passed.
- Package test: `owned distribution package: PASS`. It rebuilt all owned framework, personnel, equipment, consumables, attachments, report, scheduler, and web modules serially; rejected a synthetic conflicting class; verified deterministic clean rebuild output; and required an external configuration file.
- Manifest: schema 1; original main `GikamWpjcApplication`; 40 packaged files; 48 identical dependency class duplicates; no differing duplicate classes; servlet API excluded as compile-only because embedded Tomcat supplies it.
- Browser report: [owned-distribution-browser-attachment-integration-20261020.json](owned-distribution-browser-attachment-integration-20261020.json), SHA-256 `e1b79287336ae7925e49b84765622b4a4c94d32d78e3bc1515603bb723bf8e56`.
- Browser result: `executed-pass`, 19 checks, synthetic Oracle only. The database used one internal Docker network, no host ports, no bind mounts, and cleanup removed the container, anonymous volumes, and network.
- Verified behavior: shipped `start.sh` launched the original application main with external private config; RSA login, scoped personnel list, detail, no-match clearing, name filtering, paging, disabled write tabs, logout/session invalidation, low-permission denial, clean shutdown, and credential-free audit output.

This is bounded distribution and personnel browser evidence; the refreshed report includes the integrated attachment frontend in the shipped package. Attachment, report, and scheduler browser evidence is recorded separately in their module reports and is not counted in these 19 checks. It does not close the whole framework-rebuild specification or ticket 17. Full application/function coverage, complete permission trees, writes/workflows/import/export, historical task equivalence, third-party report rendering, Redis compatibility, and complete isolated deployment remain open. Oracle is the chosen single database mainline; PostgreSQL adoption is not a pending acceptance item.

Repeat from the recovery root with the pinned local JDK and initialized historical source checkout:

```bash
export JAVA_HOME="$PWD/build/jdk1.8.0_391"
export LIMS_PROJECT_ROOT="$PWD/project"
python3 owned-distribution/tests/test-package.py
python3 owned-distribution/tests/run-browser.py --output build/owned-distribution-browser-local.json
```

Run these sequentially: module builders replace shared target directories. The browser check also requires Docker, the pinned Oracle image, and `agent-browser`; it creates and removes its own synthetic database. External private runtime configuration stays outside the package.
