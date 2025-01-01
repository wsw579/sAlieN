GIT 사용하기

git의 주소는 : https://github.com/tdoypwls/sAIes_CRM 입니다.
권한이 없는경우 윤동의에게 요청해주세요.

intelliJ github 연동하기
https://velog.io/@zerokick/IntelliJ-IntelliJ-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-GitHub-%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0


git flow는 브렌치로 메인(main), 개발(develop), 기능(feature)로 개발됩니다.

feature 브렌치는 각자 기능을 관리하면서 개발하는 곳입니다.
develop은. feature 브랜치에서 작성한 기능을 합치고 테스트해보는 곳입니다.
main은 develop이 완전히 안정된 배포가 가능한 상태일 경우 main으로 push합니다.

각자 해주셔야하는 범위는.  develop 브랜치에서 clone으로 각자 코드를 local로 옮기시고
기능을 개발하시면서 feature 브랜치를 따로 생성하셔서 관리 부탁드립니다.
feature 브랜치 이름은 각자 개발하는 기능과 이니셜을 앞에 넣어주시면 감사하겠습니다.
ex) feature/leads-view-yde
ex) feature/leads-yde

main은 개인이 건들지 마시고. 
팀원 모두가 develop 브랜치를 가지고 테스트를 해본 뒤 안정성이 확인되면 그때 main으로 업데이트 합니다.


----------------------------------------------------------------------------------------------------------------

로컬 postgre 사용하기

DBMS는 postgre를 사용하며
SQL SW로는 DBeaver를 사용합니다.
DBeaver : https://dbeaver.io/download/
설치관련 : https://velog.io/@slobber/PostgreSQL-DBeaver-%EC%84%A4%EC%B9%98-%EB%B0%8F-%EA%B8%B0%EC%B4%88-%EC%84%A4%EC%A0%95-%ED%95%98%EA%B8%B0

설치 후 ERD에서 만든 SQL를 적용하시고 (스프링에서 처리해도 무관)
스프링에서 DDL 연결해주시면 됩니다.

모든 설정값 및 DB연동은
application.properties에 적용합니다. (dev, local, yml 사용 지양)

계정은 편의상 id : postgres pw : 1234 로 통일합니다.
postgre 17버전 포트는 5433으로 설정합니다.

서버 name : project
port 5433
Maintenance database = postgres
username = postgres
password = 1234

-----------------------------------------------------------------------------------------------------------------

!!!주의사항
클래스명은 반드시 기능+로직을 명시해줄 것.  단어 첫부분 대문자. 
ex) OpportunitiesEntity
ex) LeadsController
ex) OrderRepository
ex) ProductsDto 
등..

작성 순서는.  엔티티 - 리포지토리 - DTO - 서비스 - 컨트로러 - 뷰 입니다.
(엔티티는 도메인과 같은기능으로, 앤티티로 통일합니다.)



게시물 생성 순서.

1. 엔티티 생성 
src - main - java - com.aivle.project - entity 패키지 안에 엔티티 파일 생성.
ERD에 있는 본인 담당 테이블의 컬럼을 정의합니다.

2. 리포지토리 생성
src - main - java - com.aivle.project - repository 패키지
CRUD의 인터페이스를 정의합니다.

3. DTO 생성
src - main - java - com.aivle.project - dto 패키지
ERD에 있는 본인 담당 테이블의 컬럼을 기준으로 기능별로 정의합니다.
ex) post에는 id, name, password만 전달.
ex) get에는 id, name, phone, email만 전달

4. 서비스 생성
src - main - java - com.aivle.project - service 패키지
CRUD를 정의합니다.
내부 클래스 명은 
opportunitiesCreate
opportunitiesUpdate
와 같이 생성해주세요.

5. 컨트롤러 생성
src - main - java - com.aivle.project - controller 패키지
입력 주소와 반환값을 정의합니다.
model.addAttribute로 뷰 렌더링에 필요한 변수를 담아주세요
동일페이지 데이터 변동으로 새로고침이 필요할 경우 redirect: 를 명시해주세요.


6. 뷰 생성
src - main - resources - templates - 각 기능 폴더

main폴더의 index는 초기 진입페이지.  main폴더의 header, footer는 모든 페이지 공통입니다.

<head><header><body><html>등은 이미 header와 footer에서 정의되어 사용하시면 안됩니다.
기본적으로 모든 기능들의 mustache는 세션으로 감싸고 그 안에서 입력해주세요.
특히 <style>이나 <script>를 삽입하셔야 한다면 꼭 작업 페이지의 세션태그 안에서 넣어주세요.
세션태그 밖에 넣으면 header와 충돌합니다.

