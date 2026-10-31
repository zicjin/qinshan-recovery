# BeanPropertyEvent contract evidence

The owned bean property event preserves the supplier's `EventObject` seam,
descriptor reference, property name, old value, new value, and serial UID.
The parentless differential covers construction, source identity, descriptor
access, value mutation, and inherited event behavior.

Validation:

```text
bash scripts/tests/test-bean-property-event.sh
BeanPropertyEvent differential: PASS (constructor, source, descriptor, values, serial UID)
```

The listener/interceptor proxy chain remains open.
