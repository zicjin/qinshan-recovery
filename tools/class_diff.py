#!/usr/bin/env python3
"""单个源文件与生产字节码的语义差异（恢复工具）。

用法：
    class_diff.py --jar <run.jar> --src <工程根目录> --src-path com/a/Foo.java
        [--javap javap] [--javac javac] [--libs-classpath <jar 目录或 classpath>]

流程：把该源文件单独编译到临时目录（类路径 = 生产依赖 jar + 生产 BOOT-INF/classes，
供同包兄弟类型解析），再把两侧 class 的 javap -v 归一化文本（与 source_inventory.py
同口径：含注解、泛型签名、指令与常量引用；忽略调试行号、MethodParameters、常量池布局）
逐类对比输出统一差异。

输出：每类 SEMANTIC-MATCH / 差异正文 / MISSING（重编译缺失，如生产含匿名内部类）。
全部一致退出码 0，否则 1。供恢复流程驱动“最小补丁”循环。
"""
import argparse
import re
import subprocess
import sys
import tempfile
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from source_inventory import _normalize_javap, audited_class_equivalence, CLASS_PREFIX  # noqa: E402

import difflib
import zipfile


def type_names_of(java_path: Path):
    text = java_path.read_text(encoding="utf-8", errors="replace")
    return set(re.findall(r"\b(?:class|interface|enum|@interface)\s+(\w+)", text))


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--jar", required=True)
    ap.add_argument("--src", required=True, help="工程根目录（含 src/main/...）")
    ap.add_argument("--src-path", required=True, help="源文件相对路径，如 com/a/Foo.java")
    ap.add_argument("--javap", default="javap")
    ap.add_argument("--javac", default="javac")
    ap.add_argument("--libs-classpath", help="编译类路径（默认：生产 BOOT-INF/lib 提取目录 + 生产 classes）")
    ap.add_argument("--keep-prod-classes", help="复用已解压的生产 classes 目录（默认从 jar 取）")
    args = ap.parse_args()

    src_root = Path(args.src)
    rel = args.src_path
    java_file = None
    for root in (src_root / "src/main/java", src_root / "src/main/resources"):
        if (root / rel).is_file():
            java_file = root / rel
            break
    if java_file is None:
        print("源文件不存在: %s" % rel)
        return 2

    type_names = type_names_of(java_file)
    stem = rel[: -len(".java")]
    dir_prefix = stem.rsplit("/", 1)[0] + "/" if "/" in stem else ""

    with zipfile.ZipFile(args.jar) as z:
        prod_entries = [
            n[len(CLASS_PREFIX) :]
            for n in z.namelist()
            if n.startswith(CLASS_PREFIX) and n.endswith(".class")
        ]
        wanted = []
        for e in prod_entries:
            base = e[: -len(".class")]
            tname = base.split("/")[-1].split("$", 1)[0]
            if e.startswith(dir_prefix) and tname in type_names:
                wanted.append(e)
        wanted.sort()

        with tempfile.TemporaryDirectory() as tmpd:
            tmp = Path(tmpd)
            prod_dir = tmp / "prod"
            for e in wanted:
                p = prod_dir / e
                p.parent.mkdir(parents=True, exist_ok=True)
                p.write_bytes(z.read(CLASS_PREFIX + e))

            rebuilt_dir = tmp / "rebuilt"
            rebuilt_dir.mkdir()
            cp = args.libs_classpath or ""
            cmd = [args.javac, "-encoding", "UTF-8", "-g:none", "-nowarn",
                   "-sourcepath", str(src_root / "src/main/java"), "-d", str(rebuilt_dir)]
            if cp:
                cmd += ["-cp", cp]
            cmd.append(str(java_file))
            proc = subprocess.run(cmd, capture_output=True, text=True)
            if proc.returncode != 0:
                print("编译失败（javac）：\n" + proc.stderr)
                return 2

            def javap_texts(root: Path, entries):
                texts = {}
                names = [e[: -len(".class")].replace("/", ".") for e in entries]
                if names:
                    p = subprocess.run(
                        [args.javap, "-v", "-p", "-cp", str(root)] + names,
                        capture_output=True, text=True, errors="replace",
                    )
                    for block in p.stdout.split("Classfile ")[1:]:
                        m = re.search(r"^(?:public |final |abstract |strictfp |protected |private |static )*[@\w]*(?:class|interface|enum|@interface)\s+([\w.$]+)",
                                      block, re.M)
                        if m:
                            texts[m.group(1)] = _normalize_javap("Classfile " + block)
                return texts

            prod_texts = javap_texts(prod_dir, wanted)
            rebuilt_entries = [
                str(p.relative_to(rebuilt_dir)) for p in sorted(rebuilt_dir.rglob("*.class"))
            ]
            rebuilt_texts = javap_texts(rebuilt_dir, rebuilt_entries)

            ok = True
            for e in wanted:
                name = e[: -len(".class")].replace("/", ".")
                if name not in rebuilt_texts:
                    print("%s: MISSING（重编译缺失，如生产含匿名内部类 %s）" % (e, e))
                    ok = False
                    continue
                rebuilt_bytes = (rebuilt_dir / e).read_bytes()
                if audited_class_equivalence(e, (prod_dir / e).read_bytes(), rebuilt_bytes):
                    print("%s: SEMANTIC-MATCH" % e)
                elif prod_texts[name] == rebuilt_texts[name]:
                    print("%s: SEMANTIC-MATCH" % e)
                else:
                    print("%s: DIFF" % e)
                    for line in difflib.unified_diff(
                        prod_texts[name].splitlines(), rebuilt_texts[name].splitlines(),
                        fromfile="prod/" + e, tofile="rebuilt/" + e, lineterm="",
                    ):
                        print(line)
                    ok = False
            return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
