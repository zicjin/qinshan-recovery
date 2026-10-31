# Personnel owned-slice frontier

The current owned framework build passes its public declaration gate at 212 classes, and the recovered `EmcEmpBean.java` compiles in isolation against owned classes and pinned public dependencies. Compiling all 32 personnel package sources fails with 100 javac errors before service behavior can run.

The first missing contracts are `GenericDao`, `MybatisDaoSupport`, `GenericMapper`, `GenericService`, `GenericChoosableService`, `GenericSelectableService`, `GenericTemplateService`, `GenericResource`, and `GenericChoosableResource`/`GenericSelectableResource`. The same source set also requires the absent `RestJsonWrapperBean`, `Page`, certificate DTOs, file import/Excel APIs, and organization/business modules.

Request parsing remains cyclic and unresolved: `RestJsonWrapperBean` -> `PageRequest`/`MapperParameter`, while `PageRequest` and `MapperParameter` refer back to wrapper state. `MapperParameter` additionally reaches local principal/context, entity metadata, dialect, bean listeners, organization service, process status, and conversion utilities. `ApplicationContextHelper` has native/license declarations plus broad service/sequence methods; introducing fixed values or a mock would violate ADR-0002 and the production/read-only boundary.

The precise compile command used a temporary output directory, JDK 8u391, owned `target/classes`, and owned `target/public-deps`; no supplier classpath or production container was used. This is a source/dependency frontier, not a runtime acceptance claim.
