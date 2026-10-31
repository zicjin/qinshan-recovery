# Consumables import/export contract audit (2026-10-21)

This is a static contract audit. It does not claim that consumables import or export is implemented or dynamically verified.

## Proven original contract

The recovered consumables page calls the original file seam from `project` submodule commit `19737081a9ad4eda0b3ca0b2548ff1ae761edbf3`:

- `project/src/main/resources/static/emc/module/item/consume/consumes/emc-consume-edit-list.js:626-633` calls `GET /core/module/item/files/{templateCode}/action/download-template/{service}` with both path values `emcConsumeExportTemplate`, then downloads the returned URI.
- `project/src/main/resources/static/emc/module/item/consume/consumes/emc-consume-edit-list.js:636-643` calls `POST /core/module/item/files/{id}/action/import/emcConsumeImportFileFunction` and identifies the table as `T_EMC_CONSUME`.
- The recovered importer is `project/src/main/java/com/sunwayworld/emc/module/item/consume/consume/io/EmcConsumeImportFileFunction.java`. It reads an Excel workbook with the historical `(sheet=1, row=2)` offset, matches existing rows by `productNo` or blank-product rows by `mtlName`, inserts or updates `T_EMC_CONSUME`, creates or updates `T_EMC_CSM_STOCK`, creates `添加` or `盘存` history, and uses the historical sequence/identity helpers.
- The recovered exporter is `project/src/main/java/com/sunwayworld/emc/module/item/consume/consume/io/EmcConsumeExportTemplate.java`; its service key is `emcConsumeExportTemplate` and its displayed title is `耗材导入模板`.
- The recovered workbook is `project/src/main/java/com/sunwayworld/emc/module/item/consume/consume/io/templates/emcConsumeExportTemplate.xls`. Its first sheet has title row `耗材导入模版`, header row `序号 | 货号 | 材料名称 | 规格型号 | 库存 | 存放位置 | 供应商`, and cell comments `emcMtl`, `productNo`, `mtlName`, `specification`, `stockQty`, `location`, `vendorid` respectively. The workbook SHA-256 is `804b05227f8d2cf9e72f2faf37fac4c2c4f298ab38bfb901d1117836717edc0f`.

The original private file interface is also present in the fixed contract reference `gikam-core-common-14.1.5.jar` (SHA-256 `1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089`). Its `CoreFileResource` contract confirms `POST /core/module/item/files` upload, `POST /core/module/item/files/{id}/action/import/{service}`, and the deprecated `GET /core/module/item/files/{templateCode}/action/download-template/{service}` route.

## Owned-runtime boundary

The current owned modules do not contain a reusable `CoreFileResource`, `CoreFileService`, `CoreFileManager`, upload store, or Excel import controller. The owned web adapter explicitly leaves `Gikam.importExcel`, `Gikam.getText`, and `Gikam.emcDownloadFile` unsupported (`owned-web/src/main/resources/static/owned-web/owned-gikam.js:253`). The owned query guard only classifies the three business query domains; it does not provide the core file protocol.

Implementing this capability would therefore require a new owned file seam (multipart upload, temporary file identity/lifecycle, template download URL, workbook parser, permission and loopback policy, and Oracle transaction/audit integration). No such seam is present in the current branch, and no dynamic import/export evidence was produced in this audit.

## Status decision

Keep ticket 11 import/export as `unknown` / open. The original route and workbook schema are proven statically, but the owned runtime contract is not implemented and must not be represented as an isolated HTTP pass. Production was not contacted and no credentials were retained.
