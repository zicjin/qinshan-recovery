whenever sqlerror exit failure
alter session set container = XEPDB1;

declare
  v_count number;
begin
  select count(*) into v_count from user_tables where table_name = 'OWNED_FIXTURE_T_CORE_FILE';
  if v_count > 0 then
    execute immediate 'drop table T_CORE_FILE cascade constraints';
    execute immediate 'drop table OWNED_FIXTURE_T_CORE_FILE purge';
  end if;
end;
/

exit
