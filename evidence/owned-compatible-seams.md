# Package preserving migration seams

The first business migration seam is the read path already present in the recovered source. These paths are concrete source evidence; they are not claims that the owned runtime has already replaced their supplier dependencies.

| Capability | Existing contract | Existing implementation path | Owned status |
| --- | --- | --- | --- |
| Personnel page | `/secure/emc/module/mdm/emp/emps/page` | `EmcEmpControllerImpl` | candidate seam, page not owned-runtime verified |
| Personnel query | `/secure/emc/module/mdm/emp/emps` | `EmcEmpResourceImpl` -> `EmcEmpServiceImpl` -> `EmcEmpDaoImpl` -> `EmcEmpMapper` | Oracle query migration pending |
| Equipment query | `/secure/emc/module/mdm/equipt/equipts/page` and `/secure/emc/module/mdm/equipt/equipts` | `EmcEquiptControllerImpl` / `EmcEquiptResourceImpl` | candidate seam |
| Consumables query | `/secure/emc/module/item/consume/consumes/page` and `/secure/emc/module/item/consume/consumes` | `EmcConsumeResourceImpl` | candidate seam |
| Attachments | `/secure/emc/module/item/home-attachments` | `EmcHomeAttachmentResourceImpl` -> `EmcHomeAttachmentServiceImpl` | source path recorded; file path manager migration pending |
| Reports | `/secure/emc/module/bp/report/reports` | `EmcReportResourceImpl` | source path recorded; replay only |
| Tasks | `EmcOrderTaskResourceImpl` | task/result/log resource methods | disabled in isolated environment |

`owned-framework` implements verified paging and the personnel DTO's domain base classes, extension map and data annotations. `personnel-domain-contract.md` records actual DTO compilation and differential observations. The existing personnel detail query has no row-scope predicate in its SQL while list filtering uses the current-user scope; real page regression must verify both before marking data-scope behavior complete.
