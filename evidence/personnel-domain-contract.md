# Personnel DTO compatibility observation

Date: 2026-10-19. Scope: the actual recovered `EmcEmpBean` and its shared Java
domain contracts; ticket 09 remains open.

The unchanged source at `project` commit
`06c2bcf65fdf73a7c89b2f6faa4dce47848500f5`, path
`src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/bean/EmcEmpBean.java`, has
SHA-256 `e83c14c067c2df9da3473de8b0259fc9e786041ea36e7063d607206df52df768`.
It is compiled against owned classes and public Spring/Fastjson libraries;
no supplier framework is on this compile path.

`scripts/tests/test-personnel-domain-contract.sh` uses Java 8 and separate
classloaders to instantiate that compiled DTO against the supplier reference
and the owned implementation. The supplier common JAR is checked against
`evidence/framework-contract/jars.json` (SHA-256
`1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089`).
The reference originates in the recorded production-baseline run artifact;
only local read-only copies are used. JDK `javap` observations and the executable
Java differential provide this evidence; no REA runtime evidence was obtained.

The test passes 41 observations and compares declared public method signatures
for `BaseData`, `AbstractBaseData`, `Persistable`, `AbstractPersistable`,
`Insertable` and `AbstractInsertable`. A small test subclass exercises inherited
audit accessors, since `EmcEmpBean` overrides those accessors itself. Tests cover
default/set audit values, created time, case-insensitive extension keys, live
extension maps, merging instead of replacing, null input failures, Java 8 map
mutators, constructor order, collection value aliases and the runtime metadata
of `Id`, `Table` and `Transient`. `Id` retains the Spring Data meta-annotation.

The owned map preserves the observed default-locale lowercasing and the
three-argument constructor's insertion order, even when access order is
requested. Its map-input normalization uses a private serialization copy to
retain the observed collection alias/order behavior. Standard serializable map
inputs are covered; custom nonserializable map subclasses and supplier-specific
clone fallbacks remain unverified. Serialized-byte compatibility with old
supplier objects is not claimed. The target is source compatibility with
recompiled business classes.

Public dependencies are extracted by `scripts/prepare-owned-public-deps.py`
from existing local materials and checked against the hashes in
`evidence/t02-deps-manifest.sha256`. They are kept separately from the owned
JAR. The owned runtime classloader has no supplier common JAR. The temporary
supplier reference, DTO classes and logs are removed on exit.

This is a prerequisite for the personnel migration. It does not implement
`GenericResource`, `GenericService`, `MybatisDaoSupport`, authentication or
menu/data-scope enforcement. It does not execute the Oracle Mapper, expose
HTTP routes or validate real pages. Local Docker currently has no running
Oracle/application container; no database or production operation was made.
The list/detail data-scope discrepancy recorded in
`vertical-slices-java-seams.md` remains unresolved.
