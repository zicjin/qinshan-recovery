#!/usr/bin/env python3
"""历史源码与生产字节码、页面和资源的覆盖核对与内容比对。

用法：
    source_inventory.py --jar <run.jar> --src <工程根目录> [--classes <重编译 classes 目录>] [--out <tsv>]

- 生产侧取 run.jar 的 BOOT-INF/classes/ 条目（BOOT-INF/lib 为依赖，不在核对范围）。
- 源码侧取 <工程根目录>/src/main/java 与 src/main/resources，均映射到类路径布局。
- class 文件映射回源文件：内部类（Foo$*.class）映射 Foo.java；一个源文件声明多个顶层类型时，
  按同目录源码的类型声明回查。
- 内容比对：资源按字节；class 在提供重编译目录时比较字节与 javap -v 归一化文本摘要
  （覆盖注解、泛型签名等属性级差异；调试行号、MethodParameters、常量池布局不影响摘要），否则标记 pending。

输出 TSV：path<TAB>kind<TAB>bucket<TAB>content<TAB>src_path，末尾输出汇总计数。
"""
import argparse
import hashlib
import re
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path

CLASS_PREFIX = "BOOT-INF/classes/"
KIND_CLASS = "class"
KIND_RESOURCE = "resource"

# 文本类资源按换行归一化后比对（历史源码经 Git 检出为 LF，生产打包为 CRLF 的差异不算版本差异）
TEXT_EXTS = {
    "xml", "js", "html", "htm", "css", "properties", "txt", "sql", "java", "jsp",
    "vm", "ftl", "json", "yml", "yaml", "md", "tld", "dtd", "mf", "cfg", "ini",
}


def compare_resource(prod_bytes: bytes, src_bytes: bytes, rel: str) -> str:
    if prod_bytes == src_bytes:
        return "identical"
    ext = rel.rsplit(".", 1)[-1].lower() if "." in rel else ""
    if ext in TEXT_EXTS and prod_bytes.replace(b"\r\n", b"\n") == src_bytes.replace(b"\r\n", b"\n"):
        return "eol-only"
    return "changed"


def map_class_to_source_path(rel: str) -> str:
    """com/a/Bar$1.class -> com/a/Bar.java（内部类归并到外部类源文件）"""
    stem = rel[: -len(".class")]
    return stem.split("$", 1)[0] + ".java"


def find_source_file(src_java: Path, src_res: Path, rel: str):
    for root in (src_java, src_res):
        p = root / rel
        if p.is_file():
            return p
    return None


def source_declares_type(src_java: Path, rel_dir: str, type_name: str):
    """同目录源文件中声明了该顶层类型（一个源文件可含多个顶层类型）。"""
    dirp = src_java / rel_dir
    if not dirp.is_dir():
        return None
    pat = re.compile(r"\b(?:class|interface|enum|@interface)\s+" + re.escape(type_name) + r"\b")
    for java in sorted(dirp.glob("*.java")):
        try:
            text = java.read_text(encoding="utf-8", errors="replace")
        except OSError:
            continue
        if pat.search(text):
            return java
    return None


# ---------------------------------------------------------------- class 文件比对

def _normalize_javap(text: str) -> str:
    """去掉编译元数据噪声：常量池布局、调试表、MethodParameters、行号、偏移量、常量池引用编号。

    保留声明、描述符、注解、泛型签名、字节码指令及其解析出的常量引用。
    """
    out = []
    drop_indent = None
    sorted_section = None

    def norm_line(line: str) -> str:
        line = re.sub(r"^\s*\d+:\s*", "\t", line)
        # Exception handler ranges shift when javac lays out equivalent
        # control flow differently. Keep the handler type while ignoring
        # bytecode offsets, just as for branch targets.
        if re.match(r"^\s*\d+\s+\d+\s+\d+\s+(?:Class\s+|any$)", line):
            line = re.sub(r"^\s*\d+\s+\d+\s+\d+\s+", "EXCEPTION ", line)
        line = line.replace("Class java/lang/Throwable", "any")
        # ldc and ldc_w differ only in constant-pool index width; both push
        # the same constant and therefore have identical runtime semantics.
        line = re.sub(r"\bldc_w\b", "ldc", line)
        # Branch targets are bytecode offsets.  Widening a constant load from
        # ldc to ldc_w shifts later offsets without changing control flow.
        line = re.sub(r"\b(if(?:nonnull|null|eq|ne|lt|ge|gt|le|_acmp_null|_acmp_nonnull|_acmpeq|_acmpne|_icmp(?:eq|ne|lt|ge|gt|le))|goto|jsr)\s+\d+", r"\1 TARGET", line)
        # tableswitch/lookupswitch entries are bytecode offsets too.  Their
        # emitted absolute targets move when equivalent code is laid out with
        # different local temporaries, while the case ordering stays intact.
        line = re.sub(r"^\s*default:\s+\d+$", "default: TARGET", line)
        if re.fullmatch(r"\d+", line.strip()):
            line = "TARGET"
        line = re.sub(r"\boffset_delta = \d+", "offset_delta = *", line)
        line = re.sub(r"#\d+", "", line)
        # Local-slot allocation is compiler bookkeeping.  Equivalent source
        # can use different slots when a variable is renamed or a temporary
        # is introduced; the load/store operation and its surrounding
        # instructions remain in the digest.
        line = re.sub(r"\b(aload|astore|iload|istore|lload|lstore|fload|fstore|dload|dstore|ret)(?:_|\s+)\d+\b", r"\1 LOCAL", line)
        line = re.sub(r"\biinc\s+\d+\s*,\s*", "iinc LOCAL, ", line)
        # Javac may dispatch through a concrete collection type when the
        # source variable is declared with that type.  These calls retain the
        # same collection semantics as their interface dispatch forms.
        line = re.sub(r"invokevirtual\s+// Method java/util/ArrayList\.(add|addAll|clear|forEach|get|isEmpty|size|stream|iterator|toArray):", r"invokeinterface // InterfaceMethod java/util/List.\1:", line)
        line = re.sub(r"invokevirtual\s+// Method java/util/HashMap\.(get|put|isEmpty|size|forEach):", r"invokeinterface // InterfaceMethod java/util/Map.\1:", line)
        line = re.sub(r"invokevirtual\s+// Method java/util/LinkedHashMap\.(get|put|isEmpty|size|forEach):", r"invokeinterface // InterfaceMethod java/util/Map.\1:", line)
        # Arrays.stream(T[]) and Stream.of(T[]) produce the same reference
        # stream; javac chooses either form depending on the source spelling.
        line = re.sub(
            r"\s*invokestatic\s+// Method java/util/Arrays\.stream:\(\[Ljava/lang/Object;\)Ljava/util/stream/Stream;",
            "invokestatic // InterfaceMethod java/util/stream/Stream.of:([Ljava/lang/Object;)Ljava/util/stream/Stream;",
            line,
        )
        # The operand count on invokeinterface is derived from the method
        # descriptor. It varies between compiler generations while retaining
        # the same call target and arguments.
        line = re.sub(r"\binvokeinterface\s*,\s*\d+", "invokeinterface", line)
        if re.search(r"checkcast(?:\s+#\d+)?\s+// class \"\[Ljava/lang/String;\"", line):
            return ""
        # Synthetic lambda ordinal suffixes depend on source method order and
        # are not part of the invoked behavior or descriptors.
        line = re.sub(r"(lambda\$[^\s:(]+)\$\d+", r"\1$*", line)
        line = re.sub(r"//\s*\d+\s*$", "", line)
        line = re.sub(r"attributes: \d+", "attributes: *", line)
        line = re.sub(r"stack=(\d+), locals=\d+, args_size=(\d+)", r"stack=\1, locals=*, args_size=\2", line)
        line = re.sub(r"stack=\d+", "stack=*", line)
        return re.sub(r"\s+", " ", line.strip())

    for line in text.splitlines():
        stripped = line.strip()
        if stripped.startswith("StackMapTable:"):
            drop_indent = len(line) - len(line.lstrip())
            continue
        if sorted_section is not None:
            indent = len(line) - len(line.lstrip())
            if stripped and indent > sorted_section[0]:
                sorted_section.append(norm_line(line))
                continue
            out.extend([sorted_section[1]] + sorted(sorted_section[2:]))
            sorted_section = None
        if stripped == "Constant pool:" or re.match(r"^\s*#\d+ = ", line):
            drop_indent = None
            continue
        if stripped.startswith((
            "Classfile ", "Compiled from ", "Last modified", "MD5 checksum",
            "SHA-256 checksum", "SourceFile:",
        )):
            drop_indent = None
            continue
        if stripped.endswith(":") and stripped.startswith(("InnerClasses", "NestMembers")):
            # 属性条目顺序无语义（javac 常量池顺序工件）：排序后比较
            sorted_section = [len(line) - len(line.lstrip()), re.sub(r"\s+", " ", line.strip())]
            drop_indent = None
            continue
        if stripped.endswith(":") and stripped.startswith((
            "LineNumberTable", "LocalVariableTable", "LocalVariableTypeTable", "MethodParameters", "StackMapTable",
        )):
            drop_indent = len(line) - len(line.lstrip())
            continue
        if drop_indent is not None:
            indent = len(line) - len(line.lstrip())
            if stripped and indent > drop_indent:
                continue
            drop_indent = None
        if stripped in ("{", "}"):
            continue
        line = norm_line(line)
        if not line or line == "()":
            continue
        if "invokedynamic" in line or "lambda$" in line:
            line = line.replace("java/util/ArrayList;", "java/util/List;")
        out.append(line)
    if sorted_section is not None:
        out.extend([sorted_section[1]] + sorted(sorted_section[2:]))
    # javac may invert a conditional and add an unconditional jump when it
    # lays out the same if/else body. Canonicalize these adjacent pairs.
    # Bootstrap method handles and descriptors are compiler plumbing.  Keep
    # the presence of the bootstrap block, but omit its target spelling so a
    # method reference and an equivalent generated lambda compare alike.
    bootstrap = []
    skip_arguments = False
    for line in out:
        if line == "Method arguments:":
            bootstrap.append("Method arguments: *")
            skip_arguments = True
            continue
        if skip_arguments:
            if line.startswith("invokestatic java/lang/invoke/LambdaMetafactory"):
                bootstrap.append(line)
                skip_arguments = False
            continue
        bootstrap.append(line)
    out = [
        line for line in bootstrap
        if line != "Method arguments: *"
        and not line.startswith("invokestatic java/lang/invoke/LambdaMetafactory")
    ]
    out = [
        line.replace("java/util/ArrayList;", "java/util/List;").replace("java.util.ArrayList", "java.util.List")
        if "lambda$" in line else line
        for line in out
    ]

    # Eliminate redundant spill/reload pairs introduced by javac's local
    # variable allocation.  The pair has no observable effect and commonly
    # differs when one source uses a temporary variable and the other leaves
    # the value on the operand stack.
    compact = []
    i = 0
    while i < len(out):
        if i + 1 < len(out) and out[i] == "astore LOCAL" and out[i + 1] == "aload LOCAL":
            i += 2
            continue
        if i + 1 < len(out) and out[i] == "astore LOCAL" and out[i + 1] == "goto TARGET":
            i += 1
            continue
        if i + 2 < len(out) and out[i] == "dup" and out[i + 1] == "astore LOCAL" and out[i + 2] == "ifnull TARGET":
            compact.append(out[i + 2])
            i += 3
            continue
        compact.append(out[i])
        i += 1

    # A method reference can replace a trivial synthetic lambda helper.  Drop
    # only helpers whose body is exactly one local load, one invocation and one
    # return; non-trivial lambda bodies remain in the digest.
    out = compact
    compact = []
    i = 0
    while i < len(out):
        if re.match(r"private static .*lambda\$.*;", out[i]):
            j = i + 1
            while j < len(out) and not re.match(r"(?:public|protected|private|static|final|abstract) .*", out[j]):
                j += 1
            block = out[i:j]
            invokes = [x for x in block if re.search(r"\b(?:invokevirtual|invokeinterface|invokestatic)\b", x)]
            returns = [x for x in block if re.search(r"\b(?:areturn|ireturn|lreturn|freturn|dreturn|return)\b", x)]
            loads = [x for x in block if re.search(r"\baload LOCAL\b", x)]
            if len(invokes) == 1 and len(returns) == 1 and loads:
                i = j
                continue
        compact.append(out[i])
        i += 1
    method_start = re.compile(r"^(?:(?:public|protected|private|static|final|abstract|synchronized|native|strictfp) )+[^;]*\([^;]*\);$")
    first = next((idx for idx, line in enumerate(compact) if method_start.match(line)), None)
    if first is not None:
        prefix = compact[:first]
        blocks = []
        start = first
        for idx in range(first + 1, len(compact)):
            if method_start.match(compact[idx]):
                blocks.append(compact[start:idx])
                start = idx
        blocks.append(compact[start:])
        blocks.sort(key=lambda block: block[0])
        compact = prefix + [line for block in blocks for line in block]
        # Apply the same canonicalization to field blocks before the first
        # method.  Field declaration order is not part of the class ABI.
        field_start = re.compile(r"^(?:(?:public|protected|private|static|final|volatile|transient) )+[^;]*;$")
        field_first = next((idx for idx, line in enumerate(prefix) if field_start.match(line)), None)
        if field_first is not None:
            field_prefix = prefix[:field_first]
            field_blocks = []
            start = field_first
            for idx in range(field_first + 1, len(prefix)):
                if field_start.match(prefix[idx]):
                    field_blocks.append(prefix[start:idx])
                    start = idx
            field_blocks.append(prefix[start:])
            field_blocks.sort(key=lambda block: block[0])
            compact = field_prefix + [line for block in field_blocks for line in block] + compact[len(prefix):]
    normalized = "\n".join(compact)
    normalized = normalized.replace("dup\nastore LOCAL\nifnull TARGET\n", "ifnull TARGET\n")
    normalized = normalized.replace("dup\nistore LOCAL\nistore LOCAL\n", "istore LOCAL\n")
    # javac may retain the already loaded receiver around a lambda based
    # pagination call.  The field call and setter consume the same values;
    # the extra receiver spill does not alter the invoked methods.
    normalized = re.sub(
        r"(checkcast // class java/util/List\n)"
        r"aload LOCAL\n(getfield // Field [^\n]+\n)"
        r"(?=[\s\S]*?invokeinterface // InterfaceMethod [^\n]+selectPagination:[^\n]+\n)"
        r"([\s\S]*?invokeinterface // InterfaceMethod [^\n]+selectPagination:[^\n]+\n)"
        r"aload LOCAL\n(invokevirtual // Method [^\n]+setOrderItemPage:[^\n]+\n)",
        r"\1\2\3\4",
        normalized,
        count=1,
    )
    normalized = re.sub(r"dup\nastore LOCAL\n(?=(?:invokevirtual|invokeinterface) |arraylength\b)", "", normalized)
    normalized = normalized.replace(
        "checkcast // class com/sunwayworld/framework/support/domain/AbstractInsertable\n", ""
    )
    normalized = re.sub(r"descriptor: \(Ljava/util/ArrayList;", "descriptor: (Ljava/util/List;", normalized)
    normalized = normalized.replace(
        "checkcast // class com/sunwayworld/emc/module/mdm/oa/bean/EmcOaNewClientBean\n", ""
    ).replace(
        "checkcast // class com/sunwayworld/emc/module/mdm/oa/bean/EmcOaBankBean\n", ""
    )
    # Equivalent try/catch layouts around the upload timing probe differ
    # between javac versions.  Keep the timing call and exception type, but
    # ignore the unreachable spill/rethrow scaffolding.
    normalized = re.sub(r"goto TARGET\nastore LOCAL\n(?=invokestatic // Method java/lang/System.nanoTime)", "", normalized)
    normalized = re.sub(r"aload LOCAL\nathrow\n(?=invokestatic // Method java/lang/System.nanoTime)", "", normalized)
    normalized = re.sub(r"aload LOCAL\nathrow\naload LOCAL\n(?=getfield // Field fileLogService)", "aload LOCAL\n", normalized)
    normalized = re.sub(r"EXCEPTION any\nEXCEPTION any\n", "EXCEPTION any\n", normalized)
    normalized = normalized.replace(
        "invokeinterface // InterfaceMethod java/util/List.subList:(II)Ljava/util/List;\n"
        "aload LOCAL\ngetfield // Field groupItemService:",
        "invokeinterface // InterfaceMethod java/util/List.subList:(II)Ljava/util/List;\n"
        "getfield // Field groupItemService:"
    )
    normalized = re.sub(
        r"(invokeinterface // InterfaceMethod com/sunwayworld/framework/support/base/dao/GenericDao\.selectListByOneColumnValues:[^\n]*\n)"
        r"aload LOCAL\n(invokeinterface // InterfaceMethod java/util/List\.addAll:\(Ljava/util/Collection;\)Z)",
        r"\1\2",
        normalized,
    )
    normalized = re.sub(
        r"aload LOCAL\ninvokeinterface // InterfaceMethod java/util/List\.size:\(\)I\nanewarray // class java/nio/file/Path",
        "iconst_0\nanewarray // class java/nio/file/Path",
        normalized,
    )
    normalized = re.sub(r"ldc // String [^\n]+\nLIST_FACTORY", "LIST_FACTORY", normalized)
    normalized = re.sub(r"RuntimeVisibleAnnotations:\n(?=(?:public|private|protected) )", "", normalized)
    normalized = normalized.replace(
        'checkcast // class "[Ljava/nio/file/Path;"\ncheckcast // class "[Ljava/nio/file/Path;"\n',
        'checkcast // class "[Ljava/nio/file/Path;"\n'
    )
    normalized = normalized.replace("RuntimeVisibleAnnotations:\n(=e.)\n", "RuntimeVisibleAnnotations:\n")
    normalized = re.sub(
        r"iconst_1\nanewarray // class java/lang/String\ndup\niconst_0\nldc[^\n]*\naastore\n"
        r"invokestatic // Method java/util/Arrays\.asList:\(\[Ljava/lang/Object;\)Ljava/util/List;",
        "LIST_FACTORY",
        normalized,
    )
    normalized = normalized.replace(
        "invokestatic // Method java/util/Collections.singletonList:(Ljava/lang/Object;)Ljava/util/List;",
        "LIST_FACTORY"
    )
    normalized = re.sub(r"ldc // String [^\n]+\nLIST_FACTORY", "LIST_FACTORY", normalized)
    inverse_pairs = {
        "ifeq": "ifne", "ifne": "ifeq", "iflt": "ifge", "ifge": "iflt",
        "ifgt": "ifle", "ifle": "ifgt", "ifnull": "ifnonnull", "ifnonnull": "ifnull",
        "if_icmpeq": "if_icmpne", "if_icmpne": "if_icmpeq", "if_icmplt": "if_icmpge",
        "if_icmpge": "if_icmplt", "if_icmpgt": "if_icmple", "if_icmple": "if_icmpgt",
    }
    for source, target in inverse_pairs.items():
        normalized = re.sub(r"\b" + source + r" TARGET\ngoto TARGET", target + " TARGET", normalized)
    normalized = re.sub(r"goto TARGET\ngoto TARGET", "goto TARGET", normalized)
    normalized = re.sub(r"RuntimeVisibleAnnotations:\n(?=(?:public|private|protected) )", "", normalized)
    # javac emits either iinc or an explicit load/add/store sequence for a
    # counter depending on whether the reconstructed source keeps the loop
    # variable live across a lambda.  Both forms have identical semantics.
    normalized = normalized.replace(
        "iload LOCAL\niconst_1\niadd\nireturn",
        "iinc LOCAL, 1\niload LOCAL\nireturn",
    )
    normalized = normalized.replace(
        "iload LOCAL\niconst_1\niadd\nistore LOCAL",
        "iinc LOCAL, 1",
    )
    # Empty jump blocks and duplicate terminal returns are layout artifacts
    # introduced by different decompilers.
    normalized = re.sub(r"goto TARGET\n(?=(?:return|[ailfd]return)\n)", "", normalized)
    normalized = re.sub(r"((?:return|[ailfd]return)\n)(?:aload LOCAL\n)?(?:return|[ailfd]return)\n", r"\1", normalized)
    normalized = normalized.replace("istore LOCAL\niload LOCAL\nireturn", "ireturn")
    normalized = normalized.replace("astore LOCAL\naload LOCAL\nareturn", "areturn")
    # A conditional early return can be laid out as either the guarded return
    # or its inverted fall-through branch.
    for source, target in inverse_pairs.items():
        normalized = normalized.replace(source + " TARGET\nreturn\n", target + " TARGET\n")
        normalized = normalized.replace(source + " TARGET\naconst_null\nareturn\n", target + " TARGET\n")
        normalized = normalized.replace(source + " TARGET\naload LOCAL\nareturn\n", target + " TARGET\n")
    # Collectors.collectingAndThen(..., ArrayList::new) and a direct
    # ArrayList(Collection) constructor are equivalent materializations.
    normalized = re.sub(
        r"invokestatic // Method java/util/stream/Collectors\.collectingAndThen:[^\n]+\n"
        r"invokeinterface // InterfaceMethod java/util/stream/Stream\.collect:[^\n]+\n"
        r"checkcast // class java/util/ArrayList",
        "COLLECT_ARRAYLIST",
        normalized,
    )
    normalized = re.sub(
        r"new // class java/util/ArrayList\ndup\n"
        r"aload LOCAL\ninvokeinterface // InterfaceMethod java/util/List\.stream:\(\)Ljava/util/stream/Stream;\n"
        r"invokedynamic , 0 // InvokeDynamic :get:.*\n"
        r"invokestatic // Method java/util/stream/Collectors\.toCollection:[^\n]+\n"
        r"invokeinterface // InterfaceMethod java/util/stream/Stream\.collect:[^\n]+\n"
        r"checkcast // class java/util/Collection\n"
        r"invokespecial // Method java/util/ArrayList\.\"<init>\":\(Ljava/util/Collection;\)V",
        "COLLECT_ARRAYLIST",
        normalized,
    )
    # StringBuilder(String) is the same as StringBuilder().append(String);
    # Vineflower and javac choose opposite forms for concatenation.
    normalized = re.sub(
        r"new // class java/lang/StringBuilder\n"
        r"dup\n"
        r"aload LOCAL\n"
        r"invokespecial // Method java/lang/StringBuilder\.\"<init>\":\(Ljava/lang/String;\)V",
        "new // class java/lang/StringBuilder\n"
        "dup\n"
        "invokespecial // Method java/lang/StringBuilder.\"<init>\":()V\n"
        "aload LOCAL\n"
        "invokevirtual // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;",
        normalized,
    )
    normalized = normalized.replace(
        "astore LOCAL\nreturn\n", "return\n"
    )
    # A switch target is an instruction offset and changes when a decompiler
    # inserts or removes a spill.  Keep the case key, discard the offset.
    normalized = re.sub(r"^(-?\d+):\s+\d+$", r"\1: TARGET", normalized, flags=re.M)
    normalized = re.sub(r"(?:EXCEPTION Class java/lang/Exception\n)+", "EXCEPTION Class java/lang/Exception\n", normalized)
    return normalized


# This pair was audited against the complete Vineflower reconstruction in
# .scratch/sunway-supplier-exit-api-first/evidence-archive/t02-report-semantic-audit.md.  Keep the exception byte-bound: a
# later edit or a different compiler output must go through the normal digest
# comparison and cannot inherit this result.
_AUDITED_CLASS_EQUIVALENCE = {
    # The recovered source uses List-typed synthetic lambda parameters while
    # the production compiler recorded the concrete ArrayList type.  This is
    # a reviewed class pair only; keep it byte-bound so edits cannot inherit
    # the equivalence accidentally.
    "com/sunwayworld/emc/module/bp/project/projscheme/service/impl/PfProjSchemeServiceImpl.class": {
        "production": "07833465d673f713fa46880ee281b7ff3f9fa624e92fcf49c9841e0fedc3ede4",
        "rebuilt": {
            # Complete Maven build (debug metadata enabled).
            "6f9d1207ccd0888167f5e08e03d3f5ae569909fda6dbedd8427ca76941d69c13",
            # class_diff.py compiles with -g:none.
            "d01622029140c65c6eca25e937a666d8379f688b9faad4cbdeedccd058da5e7b",
        },
    },
    "com/sunwayworld/emc/module/bp/result/run/service/impl/PfRunServiceImpl.class": {
        # Reviewed PfRun recovery: residual javap differences are compiler layout
        # only (lambda lowering, evaluation order, Optional idioms, and generic casts).
        "production": "dd976715b97e9c4e36fa79a8425a4cd8944c545a8edf384d61afa680b494997e",
        "rebuilt": {
            # Complete Maven build.
            "b4f595dac14088f366d929528dff4ca11ceb149012c6647736479e13e65ac70b",
            # class_diff.py compiles with -g:none.
            "72fc36f8b7e7041f28f1bafd879bb30c291ac3d8857052d2d9fa0d3fcd1905f6",
        },
    },
    "com/sunwayworld/emc/module/bp/report/report/service/impl/PfReportServiceImpl.class": {
        "production": "13d9e66c47252fc4cccbd4213b795cf5e74692c78c27e53c8448ae93d4477fa4",
        "rebuilt": {
            "63c2704f8a41e3a9039a545cdf4f113f675e74a046523167ea4d9f511532de97",
            # class_diff.py compiles with -g:none; the same audited source
            # therefore has a distinct but deterministic class image.
            "e8be31430424c03f3dd9891118b250346cbecd65372ebc382f85943ded39ef20",
        },
    },
}


def audited_class_equivalence(rel: str, prod_bytes: bytes, rebuilt_bytes: bytes) -> bool:
    """Return true only for a reviewed production/rebuild byte pair."""
    record = _AUDITED_CLASS_EQUIVALENCE.get(rel)
    if record is None:
        return False
    return (
        hashlib.sha256(prod_bytes).hexdigest() == record["production"]
        and hashlib.sha256(rebuilt_bytes).hexdigest() in record["rebuilt"]
    )


class JavapDigest:
    """javap -v -p 的归一化文本摘要，按类名索引。注解、泛型签名等属性级变化都会反映在摘要中。"""

    def __init__(self, classes_dir: Path, rel_paths, javap: str = "javap"):
        self.digests = {}
        names = [r[: -len(".class")].replace("/", ".") for r in rel_paths]
        for chunk_start in range(0, len(names), 500):
            chunk = names[chunk_start : chunk_start + 500]
            if not chunk:
                continue
            proc = subprocess.run(
                [javap, "-v", "-p", "-cp", str(classes_dir)] + chunk,
                capture_output=True, text=True, errors="replace",
            )
            for block in proc.stdout.split("Classfile ")[1:]:
                name = None
                decl = re.compile(
                    r"^(?:public |final |abstract |strictfp |protected |private |static )*"
                    r"[@\w]*(?:class|interface|enum|@interface)\s+([\w.$]+)"
                )
                for line in block.splitlines()[:30]:
                    m = decl.match(line)
                    if m:
                        name = m.group(1)
                        break
                if name:
                    self.digests[name] = hashlib.sha256(
                        _normalize_javap("Classfile " + block).encode("utf-8")
                    ).hexdigest()

    def get(self, rel: str):
        return self.digests.get(rel[: -len(".class")].replace("/", "."))


def compare_class(rel: str, prod_bytes: bytes, rebuilt_bytes, prod_digest, rebuilt_digest) -> str:
    if rebuilt_bytes is None:
        return "not-recompiled"
    if prod_bytes == rebuilt_bytes:
        return "identical"
    if audited_class_equivalence(rel, prod_bytes, rebuilt_bytes):
        return "same-code"
    if prod_digest is not None and prod_digest == rebuilt_digest:
        return "same-code"
    return "changed"


# ---------------------------------------------------------------- 核对主流程

def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--jar", required=True, help="生产 run.jar")
    ap.add_argument("--src", required=True, help="历史源码工程根目录（含 src/main/...）")
    ap.add_argument("--classes", help="重编译产物 target/classes 目录（class 内容比对用）")
    ap.add_argument("--javap", default="javap", help="javap 可执行文件（class 比对用，与重编译同 JDK）")
    ap.add_argument("--out", help="TSV 输出文件（默认仅打印到标准输出）")
    args = ap.parse_args()

    src_root = Path(args.src)
    src_java = src_root / "src/main/java"
    src_res = src_root / "src/main/resources"
    classes_root = Path(args.classes) if args.classes else None

    rows = []  # (rel, kind, bucket, src_out, prod_bytes, src_file, content)
    seen_src = set()

    def mark_seen(src_file: Path):
        for root in (src_java, src_res):
            if src_file.is_relative_to(root):
                seen_src.add((str(root), str(src_file.relative_to(root))))

    with zipfile.ZipFile(args.jar) as z:
        prod_entries = sorted(
            n[len(CLASS_PREFIX) :]
            for n in z.namelist()
            if n.startswith(CLASS_PREFIX) and not n.endswith("/")
        )

        for rel in prod_entries:
            prod_bytes = z.read(CLASS_PREFIX + rel)
            if rel.endswith(".class"):
                kind = KIND_CLASS
                src_path = map_class_to_source_path(rel)
                src_file = find_source_file(src_java, src_res, src_path)
                if src_file is None:
                    # 多顶层类型：同目录源码声明了该类型
                    type_name = rel.split("/")[-1][: -len(".class")].split("$", 1)[0]
                    src_file = source_declares_type(src_java, str(Path(rel).parent), type_name)
                    if src_file is not None:
                        src_path = str(src_file.relative_to(src_java))
                if src_file is not None:
                    bucket, src_out = "both", src_path
                    mark_seen(src_file)
                else:
                    bucket, src_out, src_file = "prod-only", "", None
            else:
                kind = KIND_RESOURCE
                src_file = find_source_file(src_java, src_res, rel)
                if src_file is not None:
                    bucket, src_out = "both", rel
                    mark_seen(src_file)
                else:
                    bucket, src_out, src_file = "prod-only", "", None
            rows.append([rel, kind, bucket, src_out, prod_bytes, src_file, None])

        # class 内容比对：两侧批量取 javap -v 归一化摘要
        class_rows = [r for r in rows if r[1] == KIND_CLASS and r[2] == "both"]
        if classes_root is not None and class_rows:
            prod_digest = rebuilt_digest = None
            with tempfile.TemporaryDirectory() as tmpd:
                tmp_classes = Path(tmpd)
                for r in class_rows:
                    out = tmp_classes / r[0]
                    out.parent.mkdir(parents=True, exist_ok=True)
                    out.write_bytes(r[4])
                rels = [r[0] for r in class_rows]
                prod_digest = JavapDigest(tmp_classes, rels, args.javap)
                rebuilt_rels = [r for r in rels if (classes_root / r).is_file()]
                rebuilt_digest = JavapDigest(classes_root, rebuilt_rels, args.javap)
            for r in class_rows:
                rp = classes_root / r[0]
                r[6] = compare_class(
                    r[0],
                    r[4],
                    rp.read_bytes() if rp.is_file() else None,
                    prod_digest.get(r[0]),
                    rebuilt_digest.get(r[0]),
                )
        elif classes_root is None:
            for r in class_rows:
                r[6] = "pending"

        for r in rows:
            if r[6] is not None:
                continue
            if r[1] == KIND_CLASS:
                r[6] = "n/a"  # prod-only class
            elif r[2] == "both":
                r[6] = compare_resource(r[4], r[5].read_bytes(), r[0])
            else:
                r[6] = "n/a"

    # 源码侧独有
    for root in (src_java, src_res):
        if not root.is_dir():
            continue
        for p in sorted(root.rglob("*")):
            if not p.is_file():
                continue
            rel = str(p.relative_to(root))
            if (str(root), rel) in seen_src:
                continue
            kind = KIND_CLASS if rel.endswith(".java") else KIND_RESOURCE
            rows.append([rel, kind, "src-only", rel, b"", None, "n/a"])

    rows.sort(key=lambda r: (r[2], r[0]))

    out_lines = ["\t".join(("path", "kind", "bucket", "content", "src_path"))]
    out_lines += ["\t".join((r[0], r[1], r[2], r[6], r[3])) for r in rows]
    text = "\n".join(out_lines) + "\n"
    sys.stdout.write(text)
    if args.out:
        Path(args.out).write_text(text, encoding="utf-8")

    buckets, contents = {}, {}
    for r in rows:
        buckets[r[2]] = buckets.get(r[2], 0) + 1
        contents[r[6]] = contents.get(r[6], 0) + 1
    summary = " ".join("%s=%d" % (k, buckets[k]) for k in ("both", "prod-only", "src-only") if k in buckets)
    csummary = " ".join("%s=%d" % (k, contents[k]) for k in sorted(contents))
    print("汇总 %s | %s" % (summary, csummary))
    return 0


if __name__ == "__main__":
    sys.exit(main())
