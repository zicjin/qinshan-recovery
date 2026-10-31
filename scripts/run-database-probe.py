#!/usr/bin/env python3
"""Run fixed synthetic SQL only in a new, network-none, digest-pinned container."""
import argparse, datetime, hashlib, json, os, re, secrets, subprocess, sys, tempfile, time, uuid
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
EXPECTED={'login_active':'1','login_wrong_digest':'0','login_inactive':'0','permission_tree':'people>root','personnel_page2':'13:Carol','equipment_filter':'1','consumable_stock':'965','report_aggregate':'2','no_match':'0','sequence_rollback_gap':'101','sequence_next':'102','date':'2024-02-29 23:45:12','regex':'2','lob_text':'10:alpha~beta','lob_binary':'0001FF80','transaction_rollback':'0','transaction_commit':'1'}

def command(args,**kwargs):
 return subprocess.run(args,capture_output=True,text=True,timeout=kwargs.pop('timeout',30),**kwargs)

def main():
 p=argparse.ArgumentParser(description=__doc__)
 p.add_argument('--dialect',choices=['oracle','postgresql'],required=True)
 p.add_argument('--image',required=True)
 p.add_argument('--output',type=Path,required=True)
 p.add_argument('--ready-timeout',type=int,default=600)
 a=p.parse_args()
 report={'schema':1,'dialect':a.dialect,'image_reference':a.image,'status':'not-executed','checks':[], 'scope':'synthetic disposable schema only; no LIMS application/service acceptance', 'production_access':False,'final_mainline_decision':None,'started_at':datetime.datetime.now(datetime.timezone.utc).isoformat()}
 def save():
  a.output.parent.mkdir(parents=True,exist_ok=True);a.output.write_text(json.dumps(report,indent=2)+'\n')
 if not re.fullmatch(r'[A-Za-z0-9./_-]+@sha256:[0-9a-f]{64}',a.image):
  report['reason']='image-must-be-digest-pinned';save();return 2
 try:
  result=command(['docker','image','inspect',a.image])
  if result.returncode:
   report['reason']='pinned-image-unavailable';save();return 2
  image=json.loads(result.stdout)[0]
 except (OSError,ValueError,subprocess.TimeoutExpired):
  report['reason']='docker-unavailable';save();return 2
 report['image_id']=image['Id']
 if image.get('Os')!='linux' or image.get('Architecture')!='amd64':
  report['reason']='requires-linux-amd64';save();return 2
 nonce=uuid.uuid4().hex;name='lims-dbprobe-'+nonce
 network_name='lims-dbprobe-net-'+nonce if a.dialect=='oracle' else None
 report['container_name']=name
 report['network_name']=network_name
 report['network_mode']='isolated-internal' if network_name else 'none'
 schema=ROOT/'scripts/db-slice'/f'{a.dialect}.sql'
 probes=ROOT/'scripts/db-slice'/f'{a.dialect}-probe.sql'
 report['source_commit']=command(['git','rev-parse','HEAD']).stdout.strip()
 report['source_dirty']=bool(command(['git','status','--porcelain','--',str(schema),str(probes),str(Path(__file__))]).stdout.strip())
 report['source_sha256']={str(x.relative_to(ROOT)):hashlib.sha256(x.read_bytes()).hexdigest() for x in [schema,probes,Path(__file__)]}
 password='Probe'+secrets.token_hex(16)
 created=False
 network_created=False
 def sql(text):
  if a.dialect=='oracle':
   return command(['docker','exec','-i',name,'sqlplus','-s','/ as sysdba'],input='whenever sqlerror exit failure rollback\nset pagesize 0\nset feedback off\nset heading off\nset echo off\nset verify off\nset linesize 1000\nset trimspool on\nalter session set container=FREEPDB1;\nset serveroutput on\n'+text+'\nexit\n',timeout=120)
  return command(['docker','exec','-i','-u','postgres',name,'psql','-X','-q','-t','-A','-v','ON_ERROR_STOP=1','-d','postgres'],input=text,timeout=120)
 try:
  with tempfile.TemporaryDirectory(prefix='lims-dbprobe-env-') as d:
   envfile=Path(d)/'container.env';envfile.write_text(('ORACLE_PASSWORD=' if a.dialect=='oracle' else 'POSTGRES_PASSWORD=')+password+'\n');envfile.chmod(0o600)
   if network_name:
    net=command(['docker','network','create','--internal','--driver','bridge',network_name])
    if net.returncode:
     report['reason']='internal-network-create-failed';save();return 2
    network_created=True
    verification=command(['docker','network','inspect',network_name])
    network_info=json.loads(verification.stdout)[0] if verification.returncode==0 else {}
    if not network_info.get('Internal') or network_info.get('Containers'):
     report['reason']='internal-network-verification-failed';save();return 2
    create_network=network_name
   else:
    create_network='none'
   result=command(['docker','create','--name',name,'--label','lims.dbprobe='+nonce,'--network',create_network,'--restart','no','--memory','3g','--cpus','2','--env-file',str(envfile),a.image])
  if result.returncode:
   report['reason']='container-create-failed';save();return 2
  created=True;report['container_id']=result.stdout.strip()
  if command(['docker','start',name]).returncode:
   report['reason']='container-start-failed';save();return 2
  deadline=time.monotonic()+a.ready_timeout
  ready=False
  while time.monotonic()<deadline:
   result=sql("select 'DBPROBE_READY'"+(' from dual;' if a.dialect=='oracle' else ';'))
   if result.returncode==0 and 'DBPROBE_READY' in result.stdout:
    ready=True;break
   state=json.loads(command(['docker','inspect',name]).stdout)[0]['State']
   if not state['Running']:break
   print('waiting for disposable '+a.dialect,flush=True);time.sleep(5)
  if not ready:
   report['reason']='database-not-ready';save();return 2
  inspection=json.loads(command(['docker','inspect',name]).stdout)[0]
  netinfo=json.loads(command(['docker','network','inspect',network_name]).stdout)[0] if network_name else None
  report['isolation']={'network_mode':inspection['HostConfig']['NetworkMode'],'port_bindings':inspection['HostConfig']['PortBindings'],'mounts':[{'type':m['Type'],'destination':m['Destination']} for m in inspection.get('Mounts',[])],'production_mounts':any(m.get('Type')=='bind' for m in inspection.get('Mounts',[])),'attached_networks':sorted(inspection.get('NetworkSettings',{}).get('Networks',{})),'docker_network_internal':bool(netinfo and netinfo.get('Internal')) if network_name else None,'docker_network_subnet':(netinfo.get('IPAM',{}).get('Config') or [{}])[0].get('Subnet') if netinfo else None,'endpoint_count':len((netinfo.get('Containers') or {}) if netinfo else {}),'endpoint_container_ids':sorted((netinfo.get('Containers') or {})) if netinfo else []}
  if report['isolation']['port_bindings'] or report['isolation']['production_mounts'] or (network_name and (not report['isolation']['docker_network_internal'] or report['isolation']['endpoint_count'] != 1 or report['isolation']['endpoint_container_ids'] != [report['container_id']] or report['isolation']['attached_networks'] != [network_name])) or (not network_name and report['isolation']['network_mode']!='none'):
   raise RuntimeError('isolation-mismatch')
  version=sql('select banner from v$version;' if a.dialect=='oracle' else 'select version();')
  report['database_version']=version.stdout.strip()
  result=sql(schema.read_text()+'\n'+probes.read_text())
  report['sql_exit_code']=result.returncode
  transcript=result.stdout+'\n'+result.stderr
  # Fixed SQL emits synthetic values only; secrets and unrestricted DB logs are never retained.
  markers=re.findall(r'DBPROBE\|([a-z0-9_]+)\|([^\r\n]+)',transcript)
  actual={k:v.strip() for k,v in markers}
  expected=dict(EXPECTED,duplicate_error='-1' if a.dialect=='oracle' else '23505')
  duplicate_markers=len(markers)!=len(actual)
  report['checks']=[{'name':k,'expected':v,'actual':actual.get(k),'status':'pass' if actual.get(k)==v else 'fail'} for k,v in expected.items()]
  report['duplicate_result_markers']=duplicate_markers
  report['transcript_sha256']=hashlib.sha256(transcript.encode()).hexdigest()
  report['status']='executed-pass' if result.returncode==0 and not duplicate_markers and all(c['status']=='pass' for c in report['checks']) else 'executed-fail'
  report['reason']='synthetic-schema-probes-only'
  # Only SQL error identifiers can leave the transcript; never exception messages/connection values.
  report['sql_error_codes']=sorted(set(re.findall(r'ORA-\d{5}|SQLSTATE\s+[0-9A-Z]{5}',transcript)))
  save();return 0 if report['status']=='executed-pass' else 1
 except (OSError,ValueError,KeyError,RuntimeError,subprocess.TimeoutExpired) as exc:
  report['reason']='probe-failed-'+type(exc).__name__;save();return 2
 finally:
  if created:
   cleanup=command(['docker','rm','--force','--volumes',name])
   report['cleanup']={'container_removed':cleanup.returncode==0,'anonymous_volumes_removed':cleanup.returncode==0}
  if network_created and network_name:
   network_cleanup=command(['docker','network','rm',network_name],timeout=30)
   report.setdefault('cleanup',{})['network_removed']=network_cleanup.returncode==0
  report['finished_at']=datetime.datetime.now(datetime.timezone.utc).isoformat();save()
if __name__=='__main__':sys.exit(main())
