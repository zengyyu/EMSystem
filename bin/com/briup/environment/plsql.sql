--使用PL/SQL建表
BEGIN
FOR i IN 1..31 LOOP
EXECUTE IMMEDIATE
'CREATE TABLE e_detail_'||TO_CHAR(i)||
'(
name varchar2(20),
srcId varchar2(5),
dstId varchar2(5),
sersorAddress varchar2(7),
count number(2),
cmd  varchar2(5),
status number(2),
data number(9,4),
gather_date date
)';
END LOOP;
END;
/
--使用PL/SQL删除表
BEGIN
FOR i IN 1..31 LOOP
EXECUTE IMMEDIATE
'DROP TABLE e_detail_'||TO_CHAR(i);
END LOOP;
END;
/
create table u(
id number(7) constraint u_id_pk primary key,
username varchar2(10),
pwd varchar2(10),
gender varchar2(10),
info varchar2(50)
);
create sequence u_id
increment by 1
start with 1;