#!/usr/bin/env python3
"""Observe the approved browser entry seam before implementation."""
import json, os, subprocess, sys
from pathlib import Path
root=Path(__file__).resolve().parents[3]
result=subprocess.run(['agent-browser','--session','owned-web-entry-red','open',root.joinpath('src/main/resources/static/index.html').as_uri()],capture_output=True,text=True)
print(json.dumps({'expected':'owned shell login entry','exit_code':result.returncode,'output':result.stdout,'error':result.stderr},ensure_ascii=False))
subprocess.run(['agent-browser','--session','owned-web-entry-red','close'],capture_output=True)
raise SystemExit(0 if result.returncode else 1)
