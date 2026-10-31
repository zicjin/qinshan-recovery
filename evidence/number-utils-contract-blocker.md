# NumberUtils dependency closure blocker

The supplier `com.sunwayworld.framework.utils.NumberUtils` exposes one public constructor and 37 public static methods. The latest integration now restores `StringUtils`, `ConvertUtils`, `InvalidDataException`, `OutOfRangeException`, `UnexpectedException`, `ReflectionException`, `BeanException`, `RoundingRuleException`, `Persistable`, and `ConvertFailedException`; those edges no longer block the arithmetic/parse methods. A complete compatible implementation is still not dependency-closed, so no partial public facade is added.

A JDK 8 `javap -c -p` audit of the pinned supplier member (`BOOT-INF/lib/gikam-core-common-14.1.5.jar`, SHA-256 recorded in `evidence/framework-contract/jars.json`) shows these non-JDK call targets:

- `StringUtils`: `isEmpty`, `trim`, `split`, `replace`, `startsWith`, `endsWith`, `removeStart`, `removeEnd`; restored on the latest integration and no longer a closure blocker.
- `ConvertUtils.convert(Object, Class)` and `convert(Object, Class, Object)`; restored on the latest integration and no longer a closure blocker.
- `RoundingRuleException` is now restored as a standalone exception leaf; it does not close the remaining rounding service/script graph.
- `I18nHelper.getMessage`; the public supplier facade is intentionally not restored.
- `ApplicationContextHelper.getBean(CoreRoundingRuleService.class)`, `CoreRoundingRuleService.selectByIdIfPresent`, and `CoreRoundingRuleBean` accessors; these application-domain contracts remain absent. `Persistable` is present but does not provide the missing rounding service/bean behavior.
- `BshExecutor.instance/eval/set`; the supplier `rounding(String,String)` executes a persisted business script and cannot be replaced with a local arithmetic stub while preserving behavior.

The closure is material to public methods: `parseNumber` constructs `ConvertFailedException`; primitive/default and parse helpers call `ConvertUtils`; `rounding` reaches the application context, rounding-rule service, message helper, and BeanShell executor; `formatToChinese` can raise `OutOfRangeException`. Adding only NumberUtils would therefore produce a partial facade with unresolved runtime paths.

Verification command:

```sh
javap -classpath /tmp/slices-date-reference/supplier.jar -c -p \
  com.sunwayworld.framework.utils.NumberUtils \
  | rg 'com/sunwayworld/framework|class ' | sort -u
```

Result: arithmetic and parse dependencies are present on the latest integration, but `rounding(String,String)` still reaches the absent public I18n facade, application-context bean lookup, rounding service/bean model, and BeanShell executor. Those are behaviorally load-bearing and cannot be replaced with a local arithmetic stub. Production materials remain read-only; no supplier fallback or classpath masking was introduced.
