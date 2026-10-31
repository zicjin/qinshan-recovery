# Exception leaf contracts

The owned exception leaves now cover `ReflectionException`, `BeanException`,
`ProcessException`, `ConcurrentException`, `FormulaException`, `TimeException`,
`UnauthorizedAccessException`, `MaskImplementationException`,
`CompileException`, `CronException`, `EncryptException`, `PasswordException`,
`ScriptException`, `TaskException`, `ThreadException`,
`ResolverArgumentException`, `UnsupportedOperationSystemException`,
`FastDfsException`, `FtpException`, `CheckedException`, `JdbcException`,
`NotUniqueResultJdbcException`, `UnsupportedDataTypeJdbcException`, and
`NoIdPropertyJdbcException`. Parameterized JDBC mismatch constructors are also
covered by explicit differential calls. Each directly delegates to the
matching `RuntimeException` constructor, keeps the supplier's fixed
`serialVersionUID`, and carries `@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)`.
No reflection utility, bean descriptor, application context, or localization
dependency is introduced.

The parentless differential test loads the hash-pinned supplier common JAR and owned classes with separate class loaders. It compares null and non-null causes/messages, Unicode and empty messages, cause/message accessors, response-status annotations, serialVersionUID values, serialized bytes, and deserialized class/message results. The exception-leaf test passes. The owned build declaration gate and supplier-runtime residue scan also pass.
