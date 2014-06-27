[ 북마크 기반 참여형 검색 시스템 - BigPop]

wiki 형식의 북마크를 이용한 참여형 검색 시스템


/*** 특징 ***/
1. 북마크 데이터를 기반으로 검색 데이터를 수집함.
2. url별 hit수를 기반으로 검색 순위 결정
hit - 사용자(Contributor)가 등록한 URL이 기존에 존재하는 경우 [hit]수를 1씩 증가시킨 값

Scoring example ) 
Title 가중치 
Query가 Title에서 발견 될 경우 가중치 1.5배 부여

Example
Lucene Scoring : 0.75
Title에서 발견 : 1.5배 => (0.75*1.5)
Hit : 10 -> (0.1*10)

최종
(0.75*1.5) + (0.1*10) => 2.125


/*** current development ***/
chrome bookmark 이용 가능
기본기능(indexing, searching 가능)


/*** 이용방법 ***/

/* indexing */
1. indexing 할 bookmark를 선택한다
(현재 chrome(json)만 가능)
북마크 위치 - 
C:\Users\[yourAccount]\AppData\Local\Google\Chrome\User Data\[googleAccount]
ex - C:\Users\MSI32\AppData\Local\Google\Chrome\User Data\Profile 1
2. contributor의 이름을 적는다.(회원으로 등록되어 있지 않으면 등록이 되지 않음)
(회원가입이 구현되어 있지 않기 때문에, 테스트 회원 데이터를 넣을 것, 추후 개발 예정)
3. indexing 완료, index 완료 정보 출력

/* searching */
1. 검색 키워드를 입력한다(일반 포털검색과 동일)
2. 검색결과에 대한 데이터 확인
3. url 클릭시 해당 사이트로 이동함


/*** 설치방법 (Linux, Tomcat 기준)***/
1. Web Server(tomcat등), DBMS(mysql등)설치

2. 프로젝트 내의 WebContent 폴더의 이름을 원하는 이름으로 바꾼다

3. 이전의 WebContent 폴더를 webapps 폴더에 올린다

4. config.properties 파일의 데이터를 확인한다.
(필요한 경우 저장 데이터의 폴더를 수정하고 경로 변경 가능, 현재 절대경로로 지정됨)

5. dbms run, sql/ddl.sql을  dbms에 추가한다.
(table 구조상 MEMBER에 등록되어 있지 않으면 북마크 등록이 되지 않음, table(MEMBER)에 테스트 데이터를 넣어준다)
(bigpop_testSQL.sql 참조)

5. Web Server run

6. http://[(ip|domain):port]/[folderName]/dev/index.html) 접속
(example - http://hitit.jbnu.ac.kr:8080/SearchEngine/dev/index.html)


/*** 폴더구조 ***/

/** dev_rule **/
code표, indexField표

/** doc **/
API Docs(JavaDoc)

/** sql **/
DB Table 생성 파일, Mysql Workbench 파일

/** src **/
Server Side Source

/** WebContent **/
Web Server Load file

/* index data repository - data (in WebContent)*/
검색엔진 구동에 필요한 파일 저장
--------------------------------
/data/bookmark		// 브라우저별 bookmark 저장
/data/bookmark/chrome		// 사용자의 chrome bookmark 저장
--------------------------------
/data/docs		// 수집한 text들을 저장
/data/docs/content		// crawling 한 text(HTML Tag, white space 제거) 저장
/data/docs/html		// crawling 한 HTML문서 저장
--------------------------------
/data/indexs		// index된 정보 저장
/data/root		// crawl한 정보 저장 : crawler4j

/* dev (in WebContent)*/
html, css, js파일이 저장된 폴더(UI)

/* index (in WebContent)*/
Bookmark indexing 상태를 반환하는 jsp 파일

/* search (in WebContent)*/
Query에 대한 Searching 결과를 반환하는 jsp 파일

/* test (in WebContent)*/
각종 테스트에 사용된 파일들
(jsp, shell, html, jQuery Mobile등)

/* META-INF (in WebContent)*/
DB Connection 설정(DBCP)

/* WEB-INF (in WebContent)*/
Servlet 설정, Library 파일

config.properties(in WebContent) - 폴더경로관련 정보 저장
(Windows 버전과 Linux 버전이 있음, 필요한 버전에따라 파일이름을 위에 있는 이름으로 바꿔서 사용할것)


/*** 참고사항 ***/
DB DDL 관련 sql만 추가할 것
DML sql을 추가하면 DB와 연동이 되지 않는다
(DB와 검색엔진이 따로 놀 수 있음)


/*** library version ***/
/* Server Side*/
Web Server - apache tomcat 7.0x
검색엔진 - Lucene 4.6.0
크롤러 - crawler4j 3.5
DBMS - mysql 5.5.32
etc - dbcp, cos(MultipartRequest), json, gson(json data 관련 lib)

/* Client Side */
jQuery 1.11, jQuery Mobile 1.4
etc - jquery.form, jquery mobile bootstrap(theme)

/* IDE */
eclipse Juno, Mysql Workbench, jdk 1.7

/* Runtime */
jre 1.7


/*** 추후 개발 사항 ***/
1. export chrome 북마크 파일(html)에 대한 index이 가능하도록 개발
(parser 개발)

2. 북마크 검색 결과와 검색엔진(google) 검색 결과를 동시에 알려주기

3. 내가 어떤 검색키워드를 등록했는지 알려주기
(북마크 데이터중 폴더이름으로 구분할 수 있을 것 같다, 폴더 이름을 바탕으로 URL별로 그룹핑이 가능할 것 같음)

4. 회원가입, 회원정보관리(올린 북마크 데이터에 대한 조회, 타 회원 기여도 보기), 랭킹 시스템등(Contributor 정도에 따른 계급부여)

5. index page와 search result page 분리
