# Ticket 12 attachment and ERP replay runtime

This is a bounded implementation slice. The recovered business source is
supplied through LIMS_PROJECT_ROOT; the nested project submodule remains
uninitialized in this worktree.

- Historical attachment route and DTO/resource/controller contracts compile unchanged;
  the clean HTTP run also opens the original attachment page entry and verifies
  its historical template asset.
- Attachment list, detail, URL generation, and content reads re-check active flag,
  end date, and current user grant against synthetic Oracle on every request.
- Content is served only from an explicitly configured owned.attachments.root;
  traversal, symlinks, missing files, size mismatch, and unsafe metadata fail closed.
- OwnedErpReplayFileService preserves the original ErpFileService contract and
  /hj/erp/downloadFile path while requiring a source-labelled loopback transport.
- Replay journal records source, path, bounded attempts, status, and hashed
  idempotency key. Successful bytes are written atomically; failed responses do
  not replace an existing target.

Validation:

    LIMS_PROJECT_ROOT=/home/ts/qinshan/lims-recovery/project \
    JAVA_HOME=/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391 \
    bash scripts/tests/test-owned-erp-replay.sh
    ERP replay contract: PASS (18 checks; loopback fixture only)

The attachment HTTP check is run separately by
scripts/tests/test-attachment-runtime-oracle.py against disposable internal
Oracle and the actual GikamWpjcApplication; the final clean run passed 43 checks.
The JSON report is archived at attachment-runtime-oracle-20261020.json and records the source commit, recovered project commit and
historical source manifest, owned framework and attachment artifact hashes,
public dependency hashes, pinned Oracle image digest, source_dirty=false,
internal-network/no-port/no-bind isolation, and cleanup results.
This evidence does not claim full browser acceptance or ticket completion; the
separate browser integration remains the final UI acceptance boundary.
