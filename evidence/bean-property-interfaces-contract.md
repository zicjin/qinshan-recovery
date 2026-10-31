# Bean property listener/interceptor declarations

The owned framework now provides the supplier-facing `BeanPropertyListener`
and generic `BeanPropertyInterceptor` declarations. The interceptor extends the
pinned public `cglib-nodep-3.2.5` dependency; its proxy implementation remains
outside this declaration leaf.

Validation:

```text
bash scripts/tests/test-bean-property-interfaces.sh
BeanProperty interfaces differential: PASS (listener/interceptor declarations)
```
