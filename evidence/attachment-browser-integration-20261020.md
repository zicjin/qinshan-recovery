# Attachment browser acceptance

The clean-source runner at `owned-web/src/test/browser/run-attachment-browser.py` starts the packaged owned framework, attachments module, and web shell with a named `agent-browser` session and a disposable internal Oracle network. The run is recorded in `attachment-browser-integration-20261020.json`.

Commit `451424841a5ec411aaf29b6f38f6e25ae9c5a35a` was clean for the source paths checked by the runner. The run completed 23 checks, including two byte-hash checks for the copied historical attachment scripts. It verified the real login flow, the unchanged historical download-list script, filename click, and an actual downloaded `fixture.txt` whose SHA-256 is `c2a0976ffc69480813a0b9fda519716e6b5a0220ec7351159d548ca829156e48`.

The same browser session observed missing content as 404, grant revocation as 403 after URL acquisition, revoked list/get-url behavior, logout invalidation, and a user without a grant receiving an empty list and 403 content access. The Oracle container had one internal network, no host port or bind mount, and all container/network cleanup checks passed. The audit output was checked for fixture credentials.

The recovered historical assets remain byte-identical: `emc-home-attachment.js` SHA-256 `607815d6cb5437890aa2486875686ac34d550e148ee1db2ba1051476dc9d49c8`; `emc-home-attachment-download-list.js` SHA-256 `1c20988d39bcd3a3270d2e1608227cfca0ccb32f7103047b0bda3819de0d8fe9`.

This closes the browser acceptance seam for the bounded attachment read slice. Attachment management and the broader ticket capability catalog remain outside this evidence.
