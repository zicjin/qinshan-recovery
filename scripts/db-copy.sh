#!/usr/bin/env bash
# 把登录与首轮验收所需的结构、配置和业务数据复制到隔离副本。
#
# 用法: scripts/db-copy.sh [stage]
#   stage = stage-a（默认）：全部对象结构 + 除大表外的全部业务数据
#   stage = verify            ：逐表核对隔离副本与生产的行数
#
# 生产侧只读：expdp 通过 database link 对生产执行 SELECT，不写入、不修改生产对象。
# 复制范围：排除 9 张与首轮验收无关的大表（订单/检测结果/日志/设备采集文件类），
# 清单见 EXCLUDE_TABLES，范围说明记录在 evidence/t01-material-integrity.md。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROPS="$ROOT/private/application.properties"
OUT_DIR="$ROOT/private"
STAGE="${1:-stage-a}"

# 生产配置为 CRLF 行尾，取值时去掉行尾 \r，避免混入 SQL*Plus 命令行与 DB link 口令
prop() { grep -E "^$1=" "$PROPS" | tail -1 | cut -d= -f2- | tr -d '\r'; }

PROD_USER="$(prop spring.datasource.username)"
PROD_PASS="$(prop spring.datasource.password)"
PROD_URL="$(prop spring.datasource.url)"
PROD_CONN="${PROD_URL#jdbc:oracle:thin:@}"
: "${ORACLE_SYS_PASSWORD:?请通过受限环境变量设置 ORACLE_SYS_PASSWORD}"
# 首轮验收之外的大表（>=300MB，订单/检测结果/日志/设备采集文件类）
# 注意：expdp 的 EXCLUDE 只认逐表子句 exclude=TABLE:"='NAME'"，IN 列表形式会被静默忽略。
EXCLUDE_TABLES="T_EMC_ORDER_ITEM_GROUP,T_EMC_ORDER_TASK_RESULT,T_EMC_REPORT_RESULT,T_CORE_AT_RECORD_LINE,T_CORE_FILE,T_EMC_ORDER_TASK_LOG,T_EMC_ORDER_TASK,T_CORE_LOG,T_EMC_EQUIPT_ACQ_FILE"
EXCLUDE_CLAUSES="$(printf "exclude=TABLE:\\\"='%s'\\\"\n" ${EXCLUDE_TABLES//,/ })"

sql_sys() {
  docker exec -i lims-oracle bash -c '
    export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH="$ORACLE_HOME/bin:$PATH"
    sqlplus -s / as sysdba
  '
}

sql_system() {
  docker exec -i \
    -e "LIMS_ORACLE_SYS_PASSWORD=$ORACLE_SYS_PASSWORD" \
    -e "LIMS_SERVICE=$SERVICE" \
    lims-oracle bash -c '
      export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH="$ORACLE_HOME/bin:$PATH"
      sqlplus -s "system/$LIMS_ORACLE_SYS_PASSWORD@//localhost/$LIMS_SERVICE"
    '
}

sql_app() {
  docker exec -i \
    -e "LIMS_APP_USER=$PROD_USER" \
    -e "LIMS_APP_PASSWORD=$PROD_PASS" \
    -e "LIMS_SERVICE=$SERVICE" \
    lims-oracle bash -c '
      export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH="$ORACLE_HOME/bin:$PATH"
      sqlplus -s "$LIMS_APP_USER/$LIMS_APP_PASSWORD@//localhost/$LIMS_SERVICE"
    '
}

dp() { # expdp|impdp <parfile 内容> <日志文件>
  local tool="$1" par="$2" log="$3"
  case "$tool" in expdp|impdp) ;; *) echo "不支持的数据泵工具: $tool" >&2; return 1 ;; esac
  printf '%s\n' "$par" > /tmp/t01-dp.par
  docker cp /tmp/t01-dp.par lims-oracle:/tmp/t01-dp.par
  docker exec -i \
    -e "LIMS_ORACLE_SYS_PASSWORD=$ORACLE_SYS_PASSWORD" \
    -e "LIMS_SERVICE=$SERVICE" \
    lims-oracle bash -c '
      export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH="$ORACLE_HOME/bin:$PATH"
      "$1" "system/$LIMS_ORACLE_SYS_PASSWORD@//localhost/$LIMS_SERVICE" parfile=/tmp/t01-dp.par
    ' bash "$tool" > "$log" 2>&1
  # expdp/impdp 作业出错时仍可能返回 0，必须按日志判定
  grep -q 'successfully completed' "$log" || { tail -20 "$log" >&2; return 1; }
}

# 用户库服务名：gvenzl 镜像为 XEPDB1；非 CDB 时为 xe
SERVICE="$(sql_sys <<'SQL' | tr -d '[:space:]'
set heading off feedback off pages 0
select lower(name) from v$pdbs where open_mode = 'READ WRITE';
SQL
)"
[ -n "$SERVICE" ] || SERVICE=xe
case "$SERVICE" in
  *) : ;;
esac
echo "[db-copy] 本地服务名: $SERVICE   生产连接串: $PROD_CONN"

if [ "$STAGE" = "verify" ]; then
  echo "[db-copy] 逐表核对行数（隔离副本 vs 生产，仅通过 DB link 读生产）"
  sql_app > "$OUT_DIR/t01-copy-verify.txt" <<SQL
set serveroutput on heading off feedback off
declare
  n number; m number; checked number := 0;
begin
  for t in (select table_name from user_tables order by table_name) loop
    execute immediate 'select count(*) from "'||t.table_name||'"' into n;
    begin
      execute immediate 'select count(*) from "'||t.table_name||'"@PRODLINK' into m;
    exception when others then m := -1;
    end;
    checked := checked + 1;
    if n != m then
      dbms_output.put_line('MISMATCH '||t.table_name||' isolated='||n||' prod='||m);
    end if;
  end loop;
  dbms_output.put_line('CHECKED_TABLES='||checked);
end;
/
exit
SQL
  echo "[db-copy] 核对结果写入 private/t01-copy-verify.txt（无 MISMATCH 行即逐表一致）"
  exit 0
fi

echo "[db-copy] 1/5 创建隔离副本用户 $PROD_USER（已存在则复用）"
sql_sys <<SQL
whenever sqlerror continue
alter session set container = $SERVICE;
whenever sqlerror exit sql.sqlcode
declare
  n number;
begin
  select count(*) into n from all_users where username = upper('$PROD_USER');
  if n = 0 then
    execute immediate 'create user $PROD_USER identified by "$PROD_PASS" quota unlimited on users';
    execute immediate 'grant create session, create table, create view, create sequence, create procedure, create type, create trigger, create materialized view, create database link to $PROD_USER';
  else
    execute immediate 'alter user $PROD_USER identified by "$PROD_PASS" quota unlimited on users';
  end if;
end;
/
exit
SQL

echo "[db-copy] 2/5 建立到生产的 database link（expdp 用 SYSTEM 域，核对用业务用户域）"
sql_system <<SQL
whenever sqlerror continue
drop database link PRODLINK;
whenever sqlerror exit sql.sqlcode
create database link PRODLINK connect to $PROD_USER identified by "$PROD_PASS" using '$PROD_CONN';
select count(*) from dual@PRODLINK;
exit
SQL
sql_app <<SQL
whenever sqlerror continue
drop database link PRODLINK;
whenever sqlerror exit sql.sqlcode
create database link PRODLINK connect to $PROD_USER identified by "$PROD_PASS" using '$PROD_CONN';
exit
SQL

echo "[db-copy] 3/5 导出生产结构（metadata：全部对象，排除数据库链路）"
dp expdp "
network_link=PRODLINK
schemas=$PROD_USER
content=metadata_only
exclude=DB_LINK
reuse_dumpfiles=y
dumpfile=t01_meta.dmp
logfile=t01_meta.log" "$OUT_DIR/t01-expdp-meta.log"

echo "[db-copy] 4/5 导出生产数据（data：排除大表）"
dp expdp "
network_link=PRODLINK
schemas=$PROD_USER
content=data_only
reuse_dumpfiles=y
dumpfile=t01_data.dmp
logfile=t01_data.log
$EXCLUDE_CLAUSES" "$OUT_DIR/t01-expdp-data.log"

echo "[db-copy] 5/5 导入隔离副本"
dp impdp "
schemas=$PROD_USER
dumpfile=t01_meta.dmp
logfile=t01_imp_meta.log" "$OUT_DIR/t01-impdp-meta.log"
dp impdp "
schemas=$PROD_USER
dumpfile=t01_data.dmp
logfile=t01_imp_data.log" "$OUT_DIR/t01-impdp-data.log"

echo "[db-copy] 完成。下一步: scripts/db-copy.sh verify 核对行数；scripts/stack-isolate.sh 切断出网通道。"
