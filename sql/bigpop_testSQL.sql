delete from URL_DATA;

delete from REGISTER_MEMBER WHERE ID = "kincjf";
delete from CONTRIBUTOR WHERE id = "kincjf";

delete from REGISTER_MEMBER WHERE ID = "hitit";
delete from CONTRIBUTOR WHERE id = "hitit";

select * from CONTRIBUTOR;
select * from URL_DATA;
select COUNT(url) from URL_DATA;
select * from REGISTER_MEMBER;
select * from MEMBER;

insert into MEMBER(ID, PASSWORD, EMAIL) VALUES("kincjf", "8oo1187", "sinho0689@gmail.com");
insert into MEMBER(ID, PASSWORD, EMAIL) VALUES("yskim", "qwer!@34", "crescent702@naver.com");

insert into MEMBER(ID, PASSWORD, EMAIL) VALUES("hitit", "hitit113112", "hitit.chonbuk.ac.kr:8080");

update URL_DATA set HIT = (HIT + 1) where URL = "http://127.0.0.1:8080/SearchEngine/fileUploadTest.jsp";
update CONTRIBUTOR set UPLOAD_COUNT = (UPLOAD_COUNT + 1) where ID = "kincjf"