## 프록시

### 프록시
JPA에서 식별자로 엔티티를 조회 시, EntityManager.find() 를 사용  
EntityManager.find() : 영속성 컨텍스트에 메모리가 없으면 데이터베이스를 조회 

* em.find() vs em.getReference()
* em.find(): 데이터베이스를 통해서 실제 엔티티 객체 조회
* em.getReference() : 데이터베이스를 조회를 미루는 가짜(프록시) 엔티티 객체 조회

* 실제 클래스를 상속 받아서 만들어짐
* 실제 클래스와 겉 모양이 같다.
* 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨(이론상으로)

#### 특징
* 프록시 객체는 **처음 사용할 때 한번만 초기화**
* 프록시 객체를 초기화 활 때, 프록시 객체가 실제로 엔티티로 바뀌는 것은 아님.
* 초기화 되면 프록시 객체를 통해서 실제 엔티티에 접근 가능
* 프록시 객체는 원본 엔티티를 상속 받음, 따라서 타입 체크 시 주의해야 함  
  (== 비교 x, 대신 **instanceOf 사용**)
* 영속성 컨텍스트에 찾는 엔티티가 이미 있으면, em.reference를 호출해도 실제 엔티티 반환

* 영속성 컨텍스트의 도움을 받을 수 있는 준영속 상태일 때, 프록시를 초기화하면 문제 발생

#### 프록시 확인

* 프록시 인스턴스의 초기화 여부 확인
```java
emf.getPersistenceUnitUtil().isLoaded(refMember);
```

* 프록시 클래스 강제 초기화

* 프록시 클래스 확인 방법

###  즉시 로딩, 지연 로딩
즉시 로딩, 지연 로딩

```java
@ManyToOne(fetch=FetchType.???)
@JoinColumn(name = "TEAM_ID")
private Team team;
```

#### 지연 로딩 (Lazy)
- 프록시로 조회
- 연관 내용 조회 시, 연관 내용을 프록시로 가져옴....

```java
@ManyToOne(fetch=FetchType.LAZY)
@JoinColumn(name = "TEAM_ID")
private Team team;
```

```java
Member member = em.find(Member.class, 1L); // 이때는 프록시 객체.
Team team = member.getTeam(); //실제 team을 사용하는 시점에 초기화 (DB 조회) -> 이때 Select 쿼리 날라감
```

#### 즉시 로딩 (Lazy)
```text
💡 Member와 Team을 자주 함께 사용한다면?
```
* 즉시 로딩은 해당 엔티티를 find할 때, 연관관계 엔티티를 다같이 조회한다.


```java
@ManyToOne(fetch=FetchType.EAGER)
@JoinColumn(name = "TEAM_ID")
private Team team;
```


```java
Member member = em.find(Member.class, 1L); // 동시에 Team과 Member Join하여 호출
Team team = member.getTeam(); 
```

### 지연, 즉시 로딩 활용

프록시와 즉시로딩 주의
* **가급적 지연로딩만 사용(실무에서)**
* 즉시 로딩을 적용하면 에상하지 못한 SQL발생
* **즉시 로딩은 JPQL에서 N + 1 문제를 일으킴**
* **@ManyToOne, @OneToOne은 기본이 즉시 로딩 -> Lazy로 설정**
* **@OneToMany, @ManyToMany은 기본이 지연로딩**

지연 로딩 활용
* Member와 Team은 자주 함께 사용 : `즉시로딩`
* Member와 Order은 가끔 함께 사용 : `지연로딩`
* Order와 Product은 자주 함께 사용 : `즉시로딩`

###  영속성 전이 : CASCADE
* 특정 엔티티를 영속 상태로 만들 때, 연관된 엔티티로 함께 영속 상태로 만들고 싶을 때
* ex)부모 엔티티를 저장할 때 자식 엔티티도 함께 저장

#### 영속성 전이 : 저장
**종류**

```text
- ALL : 모두 적용 
- PERSIST : 영속 (저장)
- REMOVE : 삭제
- MERGE : 병합
- REFRESH : 
- DETACH : 
```

#### ALL
Parent 엔티티와 Child 엔티티의 생명주기가 동일 할 때,

### 고아 객체 (orphanRemoval = true)
부모 엔티티와 연관관계가 끊어진 자식 엔티티

* 고아 객체 제거: 부모 엔티티와 연관관계가 끊어진 자식 엔티티 삭제
* orphanRemoval = true
* 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능

```java
Parent parent1 = em.find(Parent.class, id);
parent.getChildren().remove(0); // 자식 엔티티를 컬렉션에서 제거
```

```sql
Delete From Child Where id = ?
```

#### 주의점
* **참조하는 곳이 하나일 때 사용해야 한다.**
* **특정 엔티티가 개인 소유할 때 사용해야한다.**
* @OneToOne, @OneToMany만 가능
* 개념적으로 부모를 제거하면 자식은 고아가 된다.
  * 따라서, 부모를 제거할 때, 자식도 함께 제거된다.
  * `CascadeType.REMOVE`처럼 동작한다.

### 영속성 전이 + 고아 객체 , 생명 주기

* CascadeType.ALL + orphanRemoval=true
* 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거
* 두 옵션을 모두 활성화 하면, 부모 엔티티를 통해서 자식의 생명 주기를 관리할 수 있음
* 도메인 주도 설계 Aggregate Root 개념 구현시 유용

---
### 부록

**참조**  
📕 orphanRemoval vs Cascade.REMOVE
[StackOverFlow](https://stackoverflow.com/questions/18813341/what-is-the-difference-between-cascadetype-remove-and-orphanremoval-in-jpa)  
[티스로리 1](https://kobumddaring.tistory.com/56)  
[티스토리 2](https://modimodi.tistory.com/22)
1. orphanRemoval=true와 Cascade.REMOVE의 차이는?
  
* orphanRemoval
  * 관계가 끊어진 순간 **DB삭제 쿼리 날라감**
  * 부모엔티티가 사라지면서 자식 엔티티의 참조가 끊어져서 삭제됨
* Cascade.REMOVE
  * 관계가 끊어진 child를 당장 바로 자동으로 제거하지 않는다. 
    * flush()가 호출 될 때 쿼리 수행
  * 원래 지정 엔티티 삭제 시, 연관된 엔티티를 전부 삭제하는 옵션