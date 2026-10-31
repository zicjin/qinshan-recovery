# Equipment and consumables browser acceptance

The clean-source run used commit `369048e9ee26f032c8e7cd7a0ff98f957eae0bfa`, Java 8, a pinned Linux amd64 Oracle image, an internal one-container network, and no production access. It passed 55 checks covering equipment and consumables lists, scoped filters, no-match clearing, pagination, details, valid/depleted stock visibility, stock logs with operator and organization, disabled writes, low-permission page/API rejection, and logout race handling.

Machine-readable evidence: `inventory-browser-clean-20261020.json`. The report records `source_dirty=false`, a closed named browser session, no port bindings or bind mounts, and successful removal of the container, anonymous volumes, and internal network. No credentials are retained.

This is a bounded read-only browser acceptance. Procurement, maintenance, calibration, inventory mutation, imports/exports, attachments, reports, and other unverified flows remain outside this evidence.
