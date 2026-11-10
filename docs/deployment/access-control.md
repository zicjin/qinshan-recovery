# Request access definitions and permission panels

The finite core bootstrap includes all 188 request mappings captured from the
production configuration and three definitions each for role and user permission
types. These are technical definitions, not production account assignments.
The first-administrator command creates new installation identities and grants
separately, using private operator inputs. Later users, role memberships and
grants must be managed through the authenticated application APIs and pages.

The original request-to-menu aspect is optional:

```properties
sunway.api.check-access-permission=true
```

Only an explicit `true` enables it. The captured external production properties
and normal local runtime properties do not set this key. The bootstrap does not
change that choice; operators must review the intended deployment policy and
all relevant mappings before enabling it. This setting is separate from normal
Spring authentication, organization filters and other permission checks.

The aspect compares Spring's matched route pattern, including `{id}` placeholders,
with the stored URL, then selects mappings with a blank method or the exact HTTP
method. If several mappings match, any menu currently granted to the login role
permits the request. If no mapping matches, this aspect continues the request;
it does not establish a default-deny policy for every API. Other authorization
checks still apply. Permission changes must take effect in the existing session;
re-login must not be used to conceal an invalidation failure.

The original unauthorized application warning is HTTP 400. Its constructor
resolves `GIKAM.PERMISSION.UNAUTHORIZED` through the active locale before
serializing the message; a configured installation therefore returns translated
text rather than necessarily returning the raw key. The captured Chinese text
has two placeholders, while this original aspect supplies only the method as
one argument. That source message defect remains separate from access matching.
Preserve the actual locale/envelope contract when comparing applications; do
not infer a different permission meaning from the status code alone.

Seven captured mappings refer to menu `9302`, which is absent from the production
menu inventory. The installation preserves those mappings as source orphan
references. A grant cannot create a missing menu. A request with only those
orphan mappings remains denied when this aspect is enabled; if the same route
and method has another real menu mapping, that grant can permit it. Resolving
the original configuration inconsistency requires its own explicit disposition.

The role and user permission panels read their own definition tables.
`LASTSUSPENDEDFLAG='0'` selects active definitions; a null flag is not active.
The captured suspended organization role definition and report-line user
definition remain suspended. GRID URLs and column settings are preserved only
after finite declarative syntax and source-hash review. No production user,
role membership, organization grant or permission grant is shipped by this seed.

Apply and verify the technical bootstrap before creating installation identities
and starting the application. The full new-device installer remains partial;
see [clean-device-contract.md](clean-device-contract.md). This finite set does
not complete workflow, field-level audit, external-domain or full API closure.
