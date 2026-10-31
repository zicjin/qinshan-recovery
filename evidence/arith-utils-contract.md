# ArithUtils contract

`ArithUtils` is a dependency-closed arithmetic leaf from the common framework.
The owned implementation preserves its public constructor and seventeen static
methods, uses decimal arithmetic for the supplier's add/subtract/multiply/
divide/round paths, and keeps the divide-by-zero error boundary.

`scripts/tests/test-arith-utils.sh` compares supplier and owned class loaders
for double, string, list, square-root, power, rounding, expression, and
divide-by-zero observations. The test uses only the archived common-framework
JAR as a read-only reference and does not load native code, license inputs, or
the application context.
