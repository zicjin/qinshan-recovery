#!/usr/bin/env python3
"""资源/页面恢复：按差异清单把生产内容恢复进历史源码树。

用法：
    recover_resources.py --jar <run.jar> --src <工程根目录> --inventory <t02-source-inventory.tsv> [--apply]

规则（对应 evidence/t02-source-inventory.md 第 5 节处置结论）：
- both/changed   以生产内容为准更新；文本类资源换行归一为 LF 后入库
- prod-only      从生产包补入源码树：com/** 进 src/main/java，其余进 src/main/resources
- 二进制按字节原样；both/eol-only 与 both/identical 不动（已判定可复用）

默认干跑打印将执行的动作，--apply 实际写入。
"""
import argparse
import csv
import sys
import zipfile
from pathlib import Path

TEXT_EXTS = {
    "xml", "js", "html", "htm", "css", "properties", "txt", "sql", "java", "jsp",
    "vm", "ftl", "json", "yml", "yaml", "md", "tld", "dtd", "mf", "cfg", "ini",
}


def normalize_eol(data: bytes, rel: str) -> bytes:
    ext = rel.rsplit(".", 1)[-1].lower() if "." in rel else ""
    if ext in TEXT_EXTS:
        return data.replace(b"\r\n", b"\n")
    return data


def target_path(src_java: Path, src_res: Path, rel: str) -> Path:
    if rel.startswith("com/"):
        return src_java / rel
    return src_res / rel


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--jar", required=True)
    ap.add_argument("--src", required=True)
    ap.add_argument("--inventory", required=True, help="source_inventory.py 生成的 TSV")
    ap.add_argument("--apply", action="store_true", help="实际写入（默认干跑）")
    args = ap.parse_args()

    src_root = Path(args.src)
    src_java = src_root / "src/main/java"
    src_res = src_root / "src/main/resources"

    todo = []
    with open(args.inventory, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f, delimiter="\t"):
            if row["kind"] != "resource":
                continue
            if row["bucket"] == "prod-only" or (
                row["bucket"] == "both" and row["content"] == "changed"
            ):
                todo.append((row["path"], row["bucket"], row["content"]))

    with zipfile.ZipFile(args.jar) as z:
        for rel, bucket, content in todo:
            data = normalize_eol(z.read("BOOT-INF/classes/" + rel), rel)
            dest = target_path(src_java, src_res, rel)
            action = "更新" if bucket == "both" else "补入"
            print("%s %s -> %s" % (action, rel, dest.relative_to(src_root)))
            if args.apply:
                dest.parent.mkdir(parents=True, exist_ok=True)
                dest.write_bytes(data)

    print("资源恢复%s：%d 项（changed/prod-only）" % ("已执行" if args.apply else "（干跑）", len(todo)))
    return 0


if __name__ == "__main__":
    sys.exit(main())
