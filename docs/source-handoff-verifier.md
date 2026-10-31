# Clean Source Handoff Verifier

`scripts/verify-source-handoff.py` is a read-only, static check for a source
tree that has already been extracted into a separate workspace. It does not
create an archive, mutate the candidate tree, invoke Git or Docker, contact a
network endpoint, read `private/` or `materials/`, or access production.

It is intended to make the source portion of AC-10 repeatable. It is not a
release mechanism and it does not turn an untrusted tree into an approved
handoff.

## What It Checks

The verifier requires the recovery build entry points, the nested `project/`
source layout, and regular files only. It rejects repository metadata,
runtime/build inputs, and common restricted configuration material:

- `.git`, `private`, `materials`, `build`, `target`, `logs`, and `.m2` path
  components;
- `application*.properties`, `bootstrap*.properties`, `.env`, `.lic`, `.pem`,
  and `.key` files;
- symbolic links, special files, and group- or other-writable source files.

It calculates deterministic SHA-256 identities from each regular file's
relative path, executable bit, and content digest. Timestamps, ownership,
archive byte layout, and absolute extraction path are intentionally excluded.
`ROOT_TREE_SHA256` covers the complete extracted root including `project/`;
`PROJECT_TREE_SHA256` independently covers the nested application source.

The structural checks cannot determine whether arbitrary source text contains
a secret or whether its history was correctly cleaned. Credential/license
rotation, authorized history cleanup, complete material delivery, and the
independent rebuild/replay evidence remain AC-10 requirements.

## Receipt Workflow

An authorized source custodian runs the calculation against the approved clean
source tree, reviews the result, and transfers the resulting three-line
receipt through the same restricted channel as the existing source identity
record. This helper prints the receipt; it never writes it or packages source:

```bash
python3 scripts/verify-source-handoff.py \
  --root /approved/clean-lims-source \
  --emit-attestation
```

Save the reviewed output outside the candidate tree as a mode-`0600`,
current-user-owned file. For example, a receiver can place it in a restricted
location supplied by the handoff process:

```text
FORMAT=lims-source-tree-v1
ROOT_TREE_SHA256=sha256:<64-lowercase-hex>
PROJECT_TREE_SHA256=sha256:<64-lowercase-hex>
```

After independently extracting the received source into a fresh directory,
verify it without building or starting anything:

```bash
python3 scripts/verify-source-handoff.py \
  --root /work/received-lims-source \
  --attestation /restricted/source-tree.attestation
```

Only a `source handoff readiness PASS` result binds the extracted file tree to
that receipt. A receiver must not generate a new receipt from the received
tree and treat it as approval; that only describes the tree they already have.
The existing `private/source-identity.env` remains the replay manifest's
archive/Git provenance record. This verifier's tree attestation complements it
by binding the extracted source contents before a build is attempted.

## Static Test

```bash
bash scripts/tests/test-source-handoff-readiness.sh
```

The test creates only temporary fixture directories. It checks successful
receipt matching and rejects changed nested source, Git metadata, restricted
runtime inputs, symlinks, weak receipt permissions, and a receipt located
inside the candidate tree.
