# sss2 page baseline

Date: 2026-10-20 (Asia/Singapore)

This is a read-only browser observation of the existing `sss2` supplier-backed
deployment. The session used an existing authorised test profile from the
browser credential store; the username and password are intentionally omitted.
No business record was inserted, changed, deleted, or submitted.

## Observed path

1. The login page rendered the LIMS login form.
2. Login succeeded and the home page rendered the resource-management menu.
3. The menu path `全面资源管理 -> 人员管理 -> 人员管理` opened the personnel page.
4. The personnel page rendered the list/filter surface with these columns:
   `序号`, `用户名`, `姓名`, `部门`, `岗位`, `状态`, `是否过滤测试`,
   `是否报告审核人`, and `是否报告签发人`.
5. A controlled username filter was submitted and the list state changed without
   a write operation.
6. The selected personnel detail surface exposed the tabs `基本信息`, `人员培训`,
   `资格证书`, `上岗证`, `可见测试`, and `附件`.

This is a behavior reference for the owned-runtime migration. It proves the
current supplier-backed page path and its visible contract only; it does not
prove that the owned framework or a supplier-free artifact serves the path.
