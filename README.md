# JPA 강의 요약

## JPA의 영속성
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

## 객체와 테이블 매핑

### 데이터베이스 스키마 (DDL) 자동 생성 기능 

persistence.xml
```xml
<properties>
    <property name="hibernate.hbm2ddl.auto" value="create" />
</properties>
```
의 값에 따라서 DDL 자동실행 여부 결정

#### hibernate.hbm2ddl.auto 옵션(value)에 따른 상황

| 옵션 명        | 설명                              |
|:------------|:--------------------------------|
| create      | 기존테이블 삭제 후 다시 실행(DROP + CREATE) |
| create-drop | create와 같지만, 종료시점에 테이블 DROP     |
| update      | 변경 분만 방영(운영DB에 상요하면 안됨)         |
| validate    | 엔티티와 테이블이 정상적으로 매핑되었는지만 확인      |
| none        | 사용하지 않음                         |

### @Table 유니크 제약 조건 추가

```java
@Table( uniqueConstraint = 
        { @UniqueConstraint(
            name = "NAME_AGE_UNIQUE", 
            columnNames = {"NAME", "AGE"})
        })
```

### @Entity

- `@Entity`가 붙은 클래스는 JPA가 관리. 엔티티라고 부른다.
- JPA를 사용해서 매핑할 클래스는 **@Entity 필수**

**❗ 주의**

```markdown
- 기본 생성자 필수
- final 클래스, enum, interface, inner 클래스 사용X
- 저장할 필드에 final 사용X
```

#### name 속성

JPA에서 사용할 엔티티 이름을 지정한다.
```java
@Entity( name = "MBR")
public class Member {
    //...
}
```

- 기본값: 클래스 이름을 그대로 사용(ex: Member)
- 값은 클래스 이름이 없으면 가급적 기본값을 사용
``

## 필드와 컬럼 매핑

```java
import javax.persistence.Transient;

@Entity
public class Member {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob // long text;
    private String description;

    @Transient
    private int temp;
    public Member() {
    }
}

```

 @Column
: 컬럼 매핑     

| 속성                     | 설명                                                                                                                             | 기본값                  |
|:-----------------------|:-------------------------------------------------------------------------------------------------------------------------------|:---------------------|
| name                   | 필도와 매핑할 테이블의 컬럼 이름                                                                                                             | 객체의 필드 이름            |
| insertable, updateable | 등록, 변경 가능 여부                                                                                                                   | True                 |
| nullable(DDL)          | null 값의 허용 여부를 결정, false로 설정하면 DDL 생성시에 not null 제약 조건이 붙는다                                                                    |                      |
| unique(DDL)            | @Table의  uniqueConstraints와 값지만 한 컬럼에 간단히 유니크 제약 조건을 걸 때 사용한다.                                                                 |                      |
| columnDefifnition(DDL) | 데이터베이스 컬럼 정보를 집접 줄 수 있다. varchar(100) default 'EMPTY'                                                                          | 필드의 자바 타입과 방언 정보를 사용 |
| length(DDL)            | 문자 길이 제약 조건,String 타입에만 사용한다.                                                                                                  | 225                  |
| precision, scale(DDL)  | BigDecimal 타입에서 사용한다(BigInteger도 사용할 수 있다) Precision은 소수점을 포함한 전체 자릿수, <br/> scale은  소수의 자릿수다 참고로 double, float 타입에는 적용되지 않는다. | precision=19,        |

@Temporal
: 날짜 타입(java.util.Date, java.util.Calendar) 매핑할 때 사용
LocalDate, LocalDateTime을 사용할 때는 생략 가능(최신 하이버네이트 지원)

`value` 속성

| 값                      | 설명                          | 예시                  |
|:-----------------------|:----------------------------|:--------------------|
| TemporalType.DATE      | 날짜 데이터베이스 date 타입과 매핑       | 2023-10-11          |
| TemporalType.TIME      | 시간, 데이터베이스 time 타입과 매핑      | 11:11:11            |
| TemporalType.TIMESTAMP | 시간, 데이터베이스 timestemp 타입과 매핑 | 2023-10-11 11:11:11 |

@Enumerated 
: enum 타입 매핑    

**value 속성 주의점**

ORDINAL 사용 x

| 값                | 설명                  |기본 값|
|:-----------------|:--------------------|:---|
| EnumType.ORDINAL | enum 순서를 데이터베이스에 저장 |EnumType.ORDINAL|
| EnumType.STRING  | enum 이름을 데이터베이스에 저장 |EnumType.ORDINAL|

@Lob 
: 데이터베이스 BLOB, CLOB 타입과 매핑    
@Lob에는 지정할 수 있는 속성이 없다.
매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
CLOB: String, char[], java.sql.CLOB 
BLOB: byte[], java.sql.BLOB 

@Transient 
: 특정 필드를 DDL컬럼 매핑에 제외   
데이터베이스 저장X, 조회X

주로 메모리상에서만, 임시로 어떤 값을 보관하고 싶을 때 사용

## 기본 키 매핑

### 기본 키 매핑 방법 종류
직접 할당

  `@Id`만 사용


자동 생성(@GeneratedValue) 속성 star
  - IDENTITY: 데이터베이스에 위임, MYSQL
  - SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE</br> `@SequenceGenerator` 필요
  - TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용</br> `@TableGenerator` 필요
  - AUTO: 방언에 따라 자동 지정, 기본값

### @GeneratedValue 전략

#### IDENTITY 전략 
- 기본 키 생성을 테이터베이스에 위임
- 주로 MYSQL, PostgreSQL, SQL Server, DB2에 사용 
- JPA는 주로 트랜잭션 커밋 시점에 Insert SQL 실행
- AUTO_INCREASEMENT는 데이터베이스에 INSERT SQL을 실행한 이후에 ID값을 알 수 있음
- IDENTITY 전략은 em.persist 시점에 즉시 Insert SQL 실행, DB에서 식별자를 조회

#### SEQUENCE 전략 

Sequence 전략 시 속성 들

| 속성                | 설명 | 기본 값| 
|:-----------------|:--------------------|:---| 
| sequenceName  | 데이터베이스에 등록되어 있는 시퀀스 이름 |hibernate_sequence| 
| initialValue  | DDL생성 시에만 사용됨, 시퀀스 DDL을 생성할 때 처음 1 시작하는 수를 지정한다 |1| 
| allocationSize  | 시퀀스 한번 호출에 증가하는 수(성능최적화에 사용됨, 데이터 베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설정해야 한다) |**50**| 
| catalog, schema| 데이터베이스 catalog, schema이름 | |

#### Table 전략
키 생성 전용 테이블을 하나 만들어서 데이터베이스 시쿼스를 흉내내는 전략

장점 : 모든 데이터베이스에 적용 가능
단점 : 성능

#### 권장하는 식별자 전략 
- 기본키 제약 조건 : null 아님, **변하면 안된다.**
- 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 대리키 (대체키)를 이용하자
- **권장 : Long형 + 대채키 + 키 생성 전략 사용**

## 데이터 중심 설계의 문제점

- 테이블의 외래키를 객체에 그대로 가져옴
- 객체 그래프 탐색이 불가능
- 참조가 없으므로 UML도 잘못됨
