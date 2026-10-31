whenever sqlerror exit failure
alter session set container = XEPDB1;

begin
  execute immediate 'create table OWNED_FIXTURE_T_CORE_FILE (ID NUMBER PRIMARY KEY)';
exception
  when others then
    if sqlcode != -955 then raise; end if;
end;
/

declare
  v_count number;
begin
  select count(*) into v_count from user_tables where table_name = 'T_CORE_FILE';
  if v_count = 0 then
    execute immediate 'create table T_CORE_FILE (ID NUMBER PRIMARY KEY, TARGETID VARCHAR2(100), NAME VARCHAR2(200), FILEEXT VARCHAR2(20), SCOPE VARCHAR2(40), MD5NAME VARCHAR2(64), CREATEDTIME TIMESTAMP, "SIZE" NUMBER)';
    insert into OWNED_FIXTURE_T_CORE_FILE values (1);
    commit;
  end if;
end;
/
exit
