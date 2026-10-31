#!/usr/bin/env python3
"""Read-only contract inventory for the private jars embedded in a run jar.

The output contains classfile declarations and resource hashes only.  It never
extracts or writes supplier files and does not retain resource contents.
"""
from __future__ import annotations
import argparse, hashlib, io, json, struct, subprocess, zipfile
from pathlib import Path

ACC_PUBLIC, ACC_PROTECTED, ACC_NATIVE = 0x0001, 0x0004, 0x0100

class Reader:
    def __init__(self, data): self.d, self.i = data, 0
    def u1(self): v=self.d[self.i]; self.i+=1; return v
    def u2(self): v=struct.unpack_from('>H',self.d,self.i)[0]; self.i+=2; return v
    def u4(self): v=struct.unpack_from('>I',self.d,self.i)[0]; self.i+=4; return v
    def skip(self,n): self.i += n

def cp_parse(r):
    cp=[None]; n=r.u2(); i=1
    while i<n:
        tag=r.u1()
        if tag==1:
            ln=r.u2(); cp.append(('Utf8',r.d[r.i:r.i+ln].decode('utf-8','replace'))); r.i += ln
        elif tag in (3,4): r.skip(4); cp.append(('Number',None))
        elif tag in (5,6): r.skip(8); cp.append(('Number',None)); cp.append(None); i+=1
        elif tag in (7,8,16,19,20): cp.append((tag,r.u2()))
        elif tag in (9,10,11,12,17,18): cp.append((tag,r.u2(),r.u2()))
        elif tag==15: cp.append((tag,r.u1(),r.u2()))
        else: raise ValueError('unknown constant-pool tag %d'%tag)
        i+=1
    return cp

def utf(cp, idx):
    x=cp[idx]
    if x and x[0]=='Utf8': return x[1]
    if x and x[0]==7: return utf(cp,x[1])
    return None

def class_info(data):
    r=Reader(data); magic=r.u4()
    if magic != 0xCAFEBABE: raise ValueError('not a classfile')
    minor, major, = r.u2(), r.u2(); cp=cp_parse(r); access=r.u2(); this=r.u2(); sup=r.u2()
    interfaces=[utf(cp,r.u2()) for _ in range(r.u2())]
    def members(kind):
        out=[]; natives=[]
        for _ in range(r.u2()):
            ac,n,d=r.u2(),utf(cp,r.u2()),utf(cp,r.u2()); attrs=[]
            for __ in range(r.u2()):
                r.u2(); ln=r.u4(); r.skip(ln)
            item={'name':n,'descriptor':d,'access':ac}
            if kind=='method' and ac & ACC_NATIVE: natives.append({'name':n,'descriptor':d,'access':ac})
            if kind=='method' and ac & (ACC_PUBLIC|ACC_PROTECTED): out.append(item)
            elif kind=='field': out.append(item)
        return out,natives
    fields,_=members('field'); methods,natives=members('method')
    # Skip class attributes, but retain no bodies or arbitrary attribute data.
    for _ in range(r.u2()): r.u2(); r.skip(r.u4())
    refs=[]
    for x in cp:
        if not x: continue
        if x[0]==7: refs.append(utf(cp,x[1]))
        elif x[0] in (9,10,11): refs.append({'owner':utf(cp,cp[x[1]][1]),'name':utf(cp,cp[x[2]][1]),'descriptor':utf(cp,cp[x[2]][2])})
    return {'major':major,'minor':minor,'access':access,'name':utf(cp,this),'super':utf(cp,sup) if sup else None,
            'interfaces':interfaces,'fields':fields,'public_protected_methods':methods,'native_methods':natives,
            'constant_pool_references':[json.loads(x) for x in sorted(set(json.dumps(x,sort_keys=True) for x in refs))]}

def sha(data): return hashlib.sha256(data).hexdigest()

def inventory(run_jar, native, out):
    out.mkdir(parents=True, exist_ok=True); jars=[]
    with zipfile.ZipFile(run_jar) as outer:
        for name in outer.namelist():
            base=Path(name).name
            if name.endswith('.jar') and (base.startswith('gikam-') or base.startswith('private-sdk') or base.startswith('lims2-report-template-')):
                jars.append((name,outer.read(name)))
    jars.sort(key=lambda x:x[0])
    classes=[]; resources=[]; manifest=[]; native_declarations=[]
    for name, blob in jars:
        manifest.append({'jar':name,'sha256':sha(blob),'bytes':len(blob)})
        with zipfile.ZipFile(io.BytesIO(blob)) as z:
            for entry in sorted(z.infolist(),key=lambda x:x.filename):
                if entry.is_dir(): continue
                data=z.read(entry.filename)
                if entry.filename.endswith('.class'):
                    item=class_info(data); item.update({'jar':name,'entry':entry.filename,'sha256':sha(data),'bytes':len(data)})
                    native_declarations.extend({'jar':name,'entry':entry.filename,'class':item['name'],**x} for x in item['native_methods'])
                    classes.append(item)
                else: resources.append({'jar':name,'entry':entry.filename,'sha256':sha(data),'bytes':len(data)})
    native_item=None
    if native and native.exists():
        exports=[]
        try:
            raw=subprocess.check_output(['nm','-D','--defined-only','--format=posix',str(native)],text=True,stderr=subprocess.DEVNULL)
            exports=sorted(line.split()[0] for line in raw.splitlines() if line.strip())
        except (OSError,subprocess.CalledProcessError):
            exports=[]
        native_item={'path':native.name,'sha256':sha(native.read_bytes()),'bytes':native.stat().st_size,'exports':exports,
                     'java_export_count':sum(x.startswith('Java_') for x in exports),'declaration_count':len(native_declarations)}
        (out/'native-declarations.jsonl').write_text(''.join(json.dumps(x,ensure_ascii=False,sort_keys=True)+'\n' for x in native_declarations))
        (out/'native-exports.jsonl').write_text(''.join(json.dumps({'symbol':x},ensure_ascii=False)+'\n' for x in exports))
        def mangle(value):
            return ''.join('_1' if c=='_' else '_2' if c==';' else '_3' if c=='[' else '_' if c=='/' else c if c.isascii() and c.isalnum() else '_0%04x'%ord(c) for c in value)
        matches=[]
        for symbol in exports:
            if not symbol.startswith('Java_'): continue
            declarations=[]
            for d in native_declarations:
                short='Java_'+mangle(d['class'])+'_'+mangle(d['name'])
                long=short+'__'+mangle(d['descriptor'][1:d['descriptor'].index(')')])
                if symbol in (short,long): declarations.append(d)
            matches.append({'symbol':symbol,'declarations':declarations,'status':'declaration-matched' if declarations else 'unmatched-in-selected-jars','runtime_observation':'unknown'})
        (out/'native-correspondence.jsonl').write_text(''.join(json.dumps(x,ensure_ascii=False,sort_keys=True)+'\n' for x in matches))
    (out/'jars.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n')
    (out/'classes.jsonl').write_text(''.join(json.dumps(x,ensure_ascii=False,sort_keys=True)+'\n' for x in classes))
    (out/'resources.jsonl').write_text(''.join(json.dumps(x,ensure_ascii=False,sort_keys=True)+'\n' for x in resources))
    summary={'run_jar':run_jar.name,'run_jar_sha256':sha(run_jar.read_bytes()),'jars':len(jars),'classes':len(classes),'resources':len(resources),'native':native_item,'native_declarations':len(native_declarations)}
    (out/'summary.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2)+'\n')
    return summary

def main(argv=None):
    p=argparse.ArgumentParser(); p.add_argument('--run-jar',type=Path,required=True); p.add_argument('--native',type=Path); p.add_argument('--output-dir',type=Path,required=True)
    a=p.parse_args(argv); print(json.dumps(inventory(a.run_jar,a.native,a.output_dir),ensure_ascii=False,sort_keys=True)); return 0
if __name__=='__main__': raise SystemExit(main())
