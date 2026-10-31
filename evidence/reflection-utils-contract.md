# ReflectionUtils recovery evidence

The owned implementation covers the 31 outer public methods and the public constructor/getter/mutation/equality surfaces of `MethodWrapper`, `FieldWrapper`, and `ConcreteMethod`, plus the package-private `InternalCache` maps. The differential exercises method and field discovery, modifier filters, annotations, generic signatures, invocation, static and instance fields, wrappers, interface traversal, and error boundaries through parentless supplier/owned class loaders.

`getConcreteMethodByAnnotation` uses the owned `OwnedApplicationContext` bridge and returns an immutable list after a real child-loader `StaticApplicationContext` registers the fixture bean; this path is exercised in the differential. The supplier cold call reaches native `ApplicationContextHelper.getApplicationContext`; the test records that linkage result separately and does not treat it as an ordinary behavioral match. No supplier context facade or native fallback is added.
