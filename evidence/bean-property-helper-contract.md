# BeanPropertyHelper and proxy contract evidence

The owned framework now provides descriptor-list caching, case-insensitive
descriptor lookup, validity checks, and the CGLIB property listener proxy.
`BeanPropertyInterceptorImpl` records changed setter values as
`BeanPropertyEvent` instances, respects `java.beans.Transient`, handles the
null-property assignment flag, and exposes the original bean and descriptor
list.

Validation uses parentless supplier/owned loaders with a child-loaded fixture:

```text
bash scripts/tests/test-bean-property-helper.sh
BeanPropertyHelper differential: PASS (descriptor list/lookup, validity, transient filtering, null assignment and change-event proxy)
```

The test uses the pinned `cglib-nodep-3.2.5` public dependency. Full LIMS
application bootstrap and business proxy usage remain open.
