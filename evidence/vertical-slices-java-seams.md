# Actual Java migration seams for tickets 08-14

The Python fixture harness is test support, not an implementation of the LIMS
business slices. Tickets 08-14 remain incomplete until an owned Java runtime and
real page observations pass their acceptance requirements.

These signatures were inspected in the populated `project/` submodule of the
integration worktree. Paths below are relative to `project/src/main/java/`.

| Slice | Existing Java boundary | Migration implication |
| --- | --- | --- |
| 08 | `com/sunwayworld/emc/framework/security/QueryAccessFilter.java` | Existing owned guard uses current principal, current-user menu IDs and canonical protected paths. It depends on `LocalContextHelper`, `CoreMenuService` and Spring Security; preserve those contracts, deny semantics and role/data scope evidence. |
| 09 | `com/sunwayworld/emc/module/mdm/emp/emp/controller/EmcEmpController.java`; `.../resource/EmcEmpResource.java`; `.../service/EmcEmpService.java` | Preserve `/secure/emc/module/mdm/emp/emps/page` and `/secure/emc/module/mdm/emp/emps`; page controller returns existing templates, resource/service inherit generic query/detail behavior. |
| 10 | `com/sunwayworld/emc/module/mdm/equipt/equipt/controller/EmcEquiptController.java`; `.../resource/EmcEquiptResource.java` | Preserve `/secure/emc/module/mdm/equipt/equipts/page` and `/secure/emc/module/mdm/equipt/equipts`, including `Long` identifiers and bean/Page method signatures. Test method/item, plan, record, per-check and use-record flows remain separate unknown capabilities. |
| 11 | `com/sunwayworld/emc/module/item/consume/consume/controller/EmcConsumeController.java`; `.../resource/EmcConsumeResource.java` | Preserve `/secure/emc/module/item/consume/consumes/page` and `/secure/emc/module/item/consume/consumes`; stock, stock-log pagination are observable read seams. Stockin, inventory, abandon and receive are writes requiring distinct acceptance. |
| 12 | `com/sunwayworld/emc/module/item/homeattachment/resource/EmcHomeAttachmentResource.java`; `.../service/impl/EmcHomeAttachmentServiceImpl.java` | Preserve `/secure/emc/module/item/home-attachments`, permission pagination and `getDownloadUrl`. Implementation resolves `CoreFileService` association then `FilePathManager` URL through `CoreFileUtils`; dummy bytes do not establish compatibility. |
| 13 | `com/sunwayworld/emc/module/bp/report/report/resource/EmcReportResource.java` | Preserve `/secure/emc/module/bp/report/reports` and document/result/view/archive public signatures; retain third-party report authorization material boundaries. |
| 14 | `com/sunwayworld/emc/module/mdm/equipt/equiptacqfile/service/impl/EmcEquiptAcqFileServiceImpl.java` | `dcuAcquisition` is `@Profile("equipt-acq-file")`, `@Scheduled(fixedDelay = 15000)`, transactional and audited. It reads configured directories, moves/deletes files and inserts acquisition/results; keep disabled until isolated fixture directories and DB rollback are proven. |

## Next code seam

Start the personnel read path at owned implementations of the existing
`GenericResource` / `GenericService` / `MybatisDaoSupport` contracts. Its concrete
chain is:

`EmcEmpControllerImpl` -> existing template/JavaScript -> `EmcEmpResource` generic
query/detail -> `EmcEmpServiceImpl.getDao()` -> `EmcEmpDaoImpl.getMapper()` ->
`EmcEmpMapper extends GenericMapper<String>` -> `emc-emp-oracle-mapper.xml`.

The required owned shared contracts include `RestJsonWrapperBean`, `Page`,
`Pagination`, `SearchFilter`, `MatchPattern`, and global mapper append clauses.
Use actual existing DTOs, SQL, page templates and HTTP shapes when replacing
these dependencies. Do not add competing fixture-only production routes.

The first DTO prerequisite is now implemented: `EmcEmpBean` compiles against
owned domain base classes and annotations and passes 41 differential
observations (`personnel-domain-contract.md`). The resource/service/DAO and
Oracle path above remain pending; the DTO result does not promote ticket 09.

The personnel mapper's `selectByCondition` has a current-user permission clause
under `org_filter`; `selectDetail` is `WHERE A.ID = #{id}` without its own data
scope predicate. This is a verification gap, not permission to silently broaden
or redefine scope. Capture and compare actual list and detail behavior under
both authorized and restricted roles before claiming ticket 08/09 completion.
