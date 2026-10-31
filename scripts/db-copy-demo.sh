#!/usr/bin/env bash
# 演示数据填充：把「可演示运行」所需的业务数据从生产只读拉入隔离副本。
#
# 用法: scripts/db-copy-demo.sh
#
# 范围（用户 2026-09-23 决定：只需项目可演示运行，无需完整项目数据）：
#   - 结构：全部对象（由 expdp metadata 导入，见 scripts/db-copy.sh）
#   - 数据：演示必需的框架/权限/菜单/组织/字典表全量；其余表每表最多 DEMO_ROWS 行；
#           与首轮验收无关的大表不取（allocated >= 20MB 的表跳过）。
#   - 行数上限与跳过清单会写入 private/t01-demo-rows.txt，作为演示数据基准记录。
# 生产侧只读：全部通过 database link 的 SELECT 拉取，不写入、不修改生产。
# 外键在填充前禁用（限量数据无法满足引用完整性），结果记录在演示基准里。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROPS="$ROOT/private/application.properties"
OUT_DIR="$ROOT/private"
DEMO_ROWS="${DEMO_ROWS:-100}"          # 一般表的每表行数上限
FULL_MAX_ROWS="${FULL_MAX_ROWS:-20000}" # 全量口径表的行数上限（防止十万行级大表拖垮慢链路）
FULL_SIZE_KB="${FULL_SIZE_KB:-256}"    # 小于该分配尺寸的表按全量口径取
SKIP_SIZE_KB="${SKIP_SIZE_KB:-307200}" # 大于该分配尺寸（300MB）的表跳过
# 登录与页面所依赖的权限、菜单、组织、字典、元数据类表按全量口径取，
# 否则限量行数会切断「用户-角色-菜单-权限」链路，演示账号看不到入口。
# 注意用词边界：NOTIFICATION/LOGIN_LOG/GROUP_ITEM 等十万行级日志表不能进全量口径。
FULL_NAME_RE='^(T_CORE_(USER|ROLE|MENU|ORG|LOGIN_CONFIG|PASSWORD|POLICY|TENANT|SEQUENCE|DICT|CUSTOM|PAGEFIELD|PERMISSION|ADMIN|SHORTCUT|APP_USER|RESOURCE|DEPT|POST)|T_LCDP_|OAUTH_|FINE_|T_EMC_(INFO_TMPL|ITEM_GROUP|CONSUME|CSM_|INDEX_TEST))'

prop() { grep -E "^$1=" "$PROPS" | tail -1 | cut -d= -f2- | tr -d '\r'; }
PROD_USER="$(prop spring.datasource.username)"
PROD_PASS="$(prop spring.datasource.password)"
PROD_CONN="$(prop spring.datasource.url | sed 's/^jdbc:oracle:thin:@//')"
ORACLE_SYS_PASSWORD="${ORACLE_SYS_PASSWORD:-Lim5Is0lated}"
SERVICE="${SERVICE:-xepdb1}"

ORA_ENV='export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH=$ORACLE_HOME/bin:$PATH'

sql_sys() {
  docker exec -i lims-oracle bash -c "$ORA_ENV; sqlplus -s / as sysdba"
}

sql_app() {
  docker exec -i lims-oracle bash -c "export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH=\$ORACLE_HOME/bin:\$PATH; sqlplus -s $PROD_USER/$PROD_PASS@//localhost/$SERVICE"
}

# 本地各表实际行数（仅列非空表）：既是演示数据基准，也是断点续传依据
dump_local_rows() {
  sql_app <<'SQL'
set serveroutput on feedback off heading off pages 0 lines 200
declare
  n number;
begin
  for t in (select table_name from user_tables order by table_name) loop
    begin
      execute immediate 'select count(*) from "'||t.table_name||'"' into n;
    exception when others then n := -1;
    end;
    if n > 0 then
      dbms_output.put_line(t.table_name||' '||n);
    end if;
  end loop;
end;
/
exit
SQL
}

echo "[demo] 0/6 创建/复用隔离业务用户（不触碰生产）"
sql_sys <<SQL
whenever sqlerror exit sql.sqlcode
alter session set container = $SERVICE;
declare
  n number;
begin
  select count(*) into n from all_users where username = upper('$PROD_USER');
  if n = 0 then
    execute immediate 'create user $PROD_USER identified by "$PROD_PASS" quota unlimited on users';
    execute immediate 'grant create session, create table, create view, create sequence, create procedure, create type, create trigger, create materialized view, create database link to $PROD_USER';
  else
    execute immediate 'alter user $PROD_USER identified by "$PROD_PASS" quota unlimited on users';
    execute immediate 'grant create session, create table, create view, create sequence, create procedure, create type, create trigger, create materialized view, create database link to $PROD_USER';
  end if;
end;
/
exit
SQL

echo "[demo] 1/6 确认到生产的 database link（结构导入重建用户后可能需要重建）"
sql_app <<SQL
whenever sqlerror continue
drop database link PRODLINK;
whenever sqlerror exit sql.sqlcode
create database link PRODLINK connect to $PROD_USER identified by "$PROD_PASS" using '$PROD_CONN';
exit
SQL

echo "[demo] 2/6 采集生产表清单与尺寸（只读）"
sql_app <<SQL > "$OUT_DIR/t01-demo-sizes.txt"
set heading off feedback off pages 0 lines 200 trimspool on
select segment_name || ' ' || trunc(sum(bytes)/1024)
  from user_segments@PRODLINK
 where segment_type = 'TABLE'
 group by segment_name
 order by segment_name;
exit
SQL

echo "[demo] 3/6 采集本地已有行数（断点续传依据）"
dump_local_rows > "$OUT_DIR/t01-demo-rows.txt"

echo "[demo] 4/6 生成演示数据填充 SQL"
SQL_FILE="$OUT_DIR/t01-demo-fill.sql"
{
  echo "set timing on feedback on heading off pages 0 lines 300"
  echo "whenever sqlerror exit sql.sqlcode"
  # 外键、触发器先禁用：限量数据不保证引用完整性，且避免日志/编号触发器拖慢
  echo "prompt == 禁用约束与触发器 =="
  sql_app <<'SQL'
set heading off feedback off pages 0 lines 300
select 'alter table "'||table_name||'" disable constraint "'||constraint_name||'";' from user_constraints where constraint_type='R';
select 'alter table "'||table_name||'" disable trigger "'||trigger_name||'";' from user_triggers;
exit
SQL
  echo "prompt == 填充业务数据 =="
  skipped=0
  resumed=0
  while read -r name kb; do
    [ -z "${name:-}" ] && continue
    if [ "$kb" -ge "$SKIP_SIZE_KB" ]; then
      echo "prompt SKIP 大表 $name (${kb}KB)"
      skipped=$((skipped + 1))
      continue
    fi
    # 断点续传：本地已有数据的表跳过（只查本地，不访问生产）
    if awk -v t="$name" '$1==t && $2+0>0 {f=1} END {exit !f}' "$OUT_DIR/t01-demo-rows.txt" 2>/dev/null; then
      echo "prompt SKIP 已有数据 $name"
      resumed=$((resumed + 1))
      continue
    fi
    # 注意：不能用 /*+ append */ 直接路径加载，它禁止访问远程表（ORA-12840）。
    # 全链路约 25ms/行（LOB 列导致逐行取数），故一律按行数封顶；逐表提交保证中断不丢已拉数据。
    echo "delete from \"$name\";"
    if [[ "$name" =~ $FULL_NAME_RE ]] || [ "$kb" -le "$FULL_SIZE_KB" ]; then
      echo "prompt LOAD 全量口径(<=${FULL_MAX_ROWS}行) $name (${kb}KB)"
      echo "insert into \"$name\" select * from \"$name\"@PRODLINK where rownum <= $FULL_MAX_ROWS;"
    else
      echo "prompt LOAD ${DEMO_ROWS}行 $name (${kb}KB)"
      echo "insert into \"$name\" select * from \"$name\"@PRODLINK where rownum <= $DEMO_ROWS;"
    fi
    echo "commit;"
  done < "$OUT_DIR/t01-demo-sizes.txt"
  echo "commit;"
  echo "exit"
} > "$SQL_FILE"
echo "[demo] 生成 $(grep -c '^insert' "$SQL_FILE") 条插入语句：跳过大表 $skipped 张、续传跳过 $resumed 张"

echo "[demo] 5/6 执行填充（生产侧只读 SELECT）"
sql_app < "$SQL_FILE" > "$OUT_DIR/t01-demo-fill.log" 2>&1
grep -cE '[0-9]+ rows? (created|merged|inserted)' "$OUT_DIR/t01-demo-fill.log" || true

echo "[demo] 6/6 记录演示数据基准（隔离副本实际行数，仅列非空表）"
dump_local_rows > "$OUT_DIR/t01-demo-rows.txt"
echo "[demo] 完成：填充日志 private/t01-demo-fill.log，行数基准 private/t01-demo-rows.txt"
