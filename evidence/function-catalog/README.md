# Static source-backed function catalog

`recovered-source.json` is generated from the authoritative nested source repository at `lims-recovery/project`. It is a deterministic lexical index of business Java declarations, Spring mapping annotations, templates, service/mapper interfaces, and MyBatis mapper XML.

The committed full-source run contains:

- 5,131 source files with SHA-256 path manifests;
- 1,912 mapping declarations and 1,544 method-level mapping records;
- 661 template resources;
- 659 service/mapper interfaces;
- 550 mapper XML files with namespace, statement, line, hash, and lexical table candidates.

Every declaration/resource includes a repository-relative source path, line, and SHA-256. Candidate routes derived by composing declared type and method annotations are marked as candidates; unresolved inheritance and annotation expressions are retained in explicit gap fields. Mapper table names are lexical candidates and are not treated as executed SQL facts.

Dynamic behavior remains `unknown` in this artifact. It does not read production configuration, credentials, database rows, private materials, or make network connections. It does not establish menu visibility, authorization, page reachability, write behavior, or owned-runtime equivalence.

Regenerate from the pinned source checkout:

```bash
python3 lims-recovery/tools/function_catalog/extract.py \
  --source-root lims-recovery/project \
  --output lims-recovery/evidence/function-catalog/recovered-source.json
```

Build the full candidate capability inventory from that index:

```bash
python3 lims-recovery/tools/function_catalog/build_full_catalog.py \
  --source-index lims-recovery/evidence/function-catalog/recovered-source.json \
  --source-root lims-recovery/project \
  --output lims-recovery/evidence/function-catalog/full-candidate-capabilities.json
python3 lims-recovery/tools/validate_full_catalog.py \
  lims-recovery/evidence/function-catalog/full-candidate-capabilities.json \
  --source-index lims-recovery/evidence/function-catalog/recovered-source.json
```

The full inventory currently contains 3,418 static records: 1,544 endpoint
declarations, 661 templates, 550 mapper XML files, 659 service/mapper
interfaces, and four `@Scheduled` declarations. It also records 625 literal
controller-to-template references, 914 candidate resource/domain/operation
groups, exact unassigned indices, and redacted lexical integration markers.
These are source candidates. Menus, permissions, service/table joins, route
reachability, writes, and owned-runtime equivalence remain `unknown` unless
the separate owned-runtime evidence says otherwise. The 30-row
`function-coverage-catalog.tsv` remains the dynamic evidence ledger; its
bounded equipment, consumables, and report browser rows do not promote this
static inventory.

Run fixture and CLI checks:

```bash
python3 lims-recovery/scripts/tests/test-static-function-catalog.py
```
