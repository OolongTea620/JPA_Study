# 연관관계 매핑

## 연관관계의 필요성 

객체를 테이블에 맞추어 데이터 중심으로 모델링하면 협력관계를 만들 수 없다.

```markdown
- 테이블은 외래키로 조인을 사용해서 연관된 테이블을 찾는다.
- 객체는 참조를 사용해서 연관된 객체를 찾는다.
- 테이블과 객체 사이에는 큰 간격이 있다.
```

### 1.단방향 연관관계

#### 연관관계 조회 시

```java
Member findMember = em.find(Member.class, member.getId());

// 참조 관계를 사용해서 연관관계 조회
Team findTeam = findMember.getTeam();
```

#### 연관관계 수정 시

```java
Team teamB = new Tema();
teamB.setName("TeamB");
em.persist(teamB);

//회원1에 새로운 팀B 설정
member.setTeam(teamB); -> foreign key update 된다
```

---
### 양방향 연관관계와 연관관계의 주인

#### 1. 기본 - 객체와 테이블간에 연관관계를 맺는 차이
    
    💡 연관관계의 주인이란 외래키를 관리하는 엔티티를 지칭한다.


###### 객체 연관관계 = 2개
- 회원 - > 팀 연관관계 : 1개 (단반향)
- 팀  - >  회원 연관관계 : 1개 (단방향)

    => 총 2개 
###### 테이블 연관관계 1개

회원 < - > 팀 연관관계 : 1개

테이블은 `외래키` 하나로 두 테이블간의 연관관계를 관리

MEMBER.TEAM_ID **외래키 하나로 양방향 관계 연관관계 가짐**
(양쪽으로 JOIN이 가능하다)

### 양방향 매핑 규칙 

- 객체의 두 관계 중 하나를 연관관계의 주인으로 지정
- **연관관계의 주인만이 외래키를 관리(등록, 수정)**
- **주인이 아닌 쪽은 읽기만 가능**
- 주인은 mappedBy 속성 사용 X
- **주인이 아니면 mappedBy속성으로 주인 지정**


    💡 연관관계의 주인이란 외래키를 관리하는 쪽이다

<br>

    💡 외래키가 있는 곳을 주인으로 정해라

---
#### + 양방향 매핑 시 가장 많이 하는 실수
##### Case 1 : 연관관계 주인에 값을 입력하지 않음

```java
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member():
member.setname(member1);


team.getMembers().add(member); //역방향 (주인이 아닌 방향) 만 연관관계 설정
        
// 결론적으로 team Entity에서만 member가 추가됨 -> 외래키에 외래키값이 없는 상황
em.persist(member);
```

**솔루션**
1. 양쪽 Entity 둘다 값을 넣어주자

```java
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member():
member.setname(member1);

team.getMembers().add(member); // 팀 Entity에 팀원 Entity 추가
member.setTeam(team); // // 주인객체에 team 추가 (외래키에 값 넣기와 동일)

em.persist(member);
```

++ 추가하기
```java
class Member {
  ...(생략)
  
    public void setTeam(Team team) {
    this.team = team;
    team.getMembers().add(this); // team 내부 members에 값 추가
  }
}
```
```java
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member():
member.setname(member1);

member.setTeam(team); // setTeam을 할 때 알아서 추가됨

em.persist(member);
```

++ Team에서 Member를 넣는 케이스
```java
class Team {
  ...(생략)
  public void addMember(Member member) {
    member.setTeam(this);
    this.members.add(member);
  }
}
```

```java
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member():
member.setname(member1);

team.addMember(member); // Team Entity에서 member 추가됨

em.persist(member);
```
##### 무한 루프
- 순수 객체 상태를 고려하여 **항상 양쪽에 값을 설정**하자
- 연관관계 편의 메소드를 생성하자
- 양방향 매핑 시 **무한 루프 주의**
  - toString(), lombok, Json 생성 라이브러리

#### 결론
+ 단방향 매핑만으로도 이미 연관관계 매핑은 완료
+ 양방향 매핑은 반대 방향으로 조회(객체 그래프 탐색)기능이 추가된 것
+ JPQL에서 역방향 검색할 일이 잦음
+ 단방향 매핑을 먼저 하고 나서 필요시 추가 (테이블에 영향을 주지 않음)

