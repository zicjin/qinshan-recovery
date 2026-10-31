# Private framework classfile contract evidence

Regenerate from the read-only recovered artifact with:

```sh
scripts/generate-framework-contract-evidence.sh
scripts/tests/test-framework-contract-inventory.sh
```

`jars.json` is the selection authority: the 16 framework jars comprise 15
`gikam-core-*` jars and `lims2-report-template-*`. Both embedded `private-sdk`
copies are additionally inventoried, producing 18 jar records. The two SDK
copies have identical SHA-256 hashes and remain separate records because their
classpath locations differ. Counts include duplicate class occurrences across
jars; they must not be read as unique class identities.

`classes.jsonl` contains each classfile's identity, SHA-256, access bits, classfile
version, superclass, interfaces, field descriptors, public/protected method
descriptors, native method declarations, and symbolic constant-pool class/member
references. It does not contain method bytecode or arbitrary string constants.
`resources.jsonl` contains only entry names, sizes, and hashes; resource contents,
credentials, configuration values, and authorization materials are not copied.

`native-declarations.jsonl` records 48 `ACC_NATIVE` declarations in the selected
jars. `native-exports.jsonl` records the existing library's `nm -D --defined-only`
symbol names. `native-correspondence.jsonl` compares JNI-mangled short and long
names with those declarations. A match establishes a static declaration/symbol
relationship, not runtime invocation. Unmatched exports, all dynamic call paths,
native initialization order, data effects, and behavior remain unknown until
isolated runtime observation. This inventory does not assign implementation or
deletion classifications without the separate removal/architecture decisions.

`summary.json` binds the inventory to artifact hashes. Generated files are
ordered by jar path and entry path; output uses source basenames rather than
machine-specific absolute paths. The compiled Java fixture verifies a known
protected native declaration, public method descriptor, and public field via
the parser before a full artifact scan.
