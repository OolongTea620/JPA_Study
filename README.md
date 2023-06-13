## JPA 강의 요약

- 영속성 컨텍스트는 논리적이 개념
- 눈에 보이지 않는다.
- ```EntityManger```를 통해서 영속성 컨텍스트에 접근

### 엔티티의 생명주기

- 비영속 : 객체를(만) 생성한 상태
- 영속 : 생성한 객체를 EntityManager에 집어넣은(저장한) 상태
```java
    // 비영속
    Member member = new Member();
    member.setId(1L);
    member.setName("Test");

    // 영속
    em.persist(member);
```

### 영속성 컨텍스트의 이점 


1. 1차 캐시

```java
    Member member = new Member();
    member.setId(1L);
    member.setName("Test");

    // 1차 캐시에서 저장됨
    em.persist(member);
    
    // 1차 캐시에서 우선 조회
    Member findMember = em.find(Member.class, "member1");
```
- 데이터베이스가 아닌 1차 캐시에서 우선적으로 조회     
- 1차 캐시에 없다면 DB에서 조회하고 1차 캐시에 저장 뒤 반환
-> **DB접근 줄어듦**

2. 영속엔티티 동일성(identity) 보장

```java
    Member findMember1 = em.find(Member.class, 101L);
    Member findMember2 = em.find(Member.class, 101L);
    
    // true
    System.out.println(findMember1 == findMemeber2)p; 
```


3. 트랜잭션을 지원하는 쓰기 연산 (transactional write-behind)

```java
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction(); 
// 엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다
        
em.persist(memberA);
em.persist(memberB);
// 여기까지도 INSERT SQL을 보내지 않는다.
        
// 커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다
transaction.commit(); 
```
- `transaction.commit();` 순간 flush와 SQL 실행, 그리고 commit
- 옵션만으로 커밋 시점 조절이 가능하다.

4. 변경 감지 (Dirty Checking)

```java
Member findMember = em.find(Member.class, "member1");
// 데이터 변경
member.setName("ZZZZZZZ"); 

transaction.commit();
```

- JPA는 1차캐시에서 `데이터 스냅샷`을 가지고 있다가 변경이 감지되면,  

엔티티 삭제라면?

```java
Member memberA = em.find(Member.class, "MemberA");

em.remove(memberA); //  엔티티 삭제 -> 변형감지
```

#### 플러시

    영속성 컨테스트의 변경내용을 데이터베이스에 반영 (동기화의 개념)

- 변경 감지 
- 수정된 엔티티 쓰기 지연 SQL 저장소에 기록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송(등록, 수정, 삭제 쿼리)

#### 영속성 컨텍스트를 플러시 하기
- em.flush() : 직접호출
- 트랜잭션 커밋 : 플러시 자동 호출
- JPQL 쿼리 실행 : 플러시 자동 호출

5. 지연 로딩(Lazy Loading)


### 준영속 상태

`영속` -> `준영속`

영속 상태의 엔티티가 영속성 컨텍스트에서 분리 (detached)

영속성 컨텍스트가 제공하는 기능을 사용 못함

```java
Member findMember = em.find(Member.class, "member1");

em.detache(member); // 영속 - > 비영속 상태로 변경, update 쿼리가 나가지 않음

// 데이터 변경
member.setName("ZZZZZZZ"); 

transaction.commit();
```

- em.detach(entity) : 특정 엔티티를 준 영속 상태로 전환
- em.clear() : 영속성 컨텍스트를 완전히 초기화
- em.close() : 영속성 컨테스트 종료
