## 연관관계 매핑시 고려사항

* 다중성
* 단방향, 양방향
* 연관관계의 주인


### 다중성

---
* n : 1 (다대일) - @ManyToOne
* 1 : n (일대다) - @OneToMany
* 1 : 1 (일대일) - @OneToOne
* n : m (다대다) - @ManyToMany

### 단방향, 양방향

---

* **테이블**
  * 외래키 하나로 양쪽 조인 가능
  * 사실 방향이라는 개념이 없음
* **겍체**
  * 참조용 필드가 있는 쪽으로만 참조가능
  * 한쪽만 참조하면 단방향
  * 양쪽이 서로 참조하면 양방향

### 연관관계의 주인

---

* 테이블은 **외래키 하나**로 두 테이블이 연관관계를 맺음
* 객체 양방향 관계는 `A->B`, `B->A` 처럼 참조가 2군데
  * 둘 중 테이블의 외래키를 관리할 곳을 지정해야함
* 연관관계의 주인 : 외래키를 관리하는 참조
* 주인의 반대편 : 외래키에 영향을 주지 않음. 단순 조회만 가능


#### 다대일 연관관계
* `N` 쪽 엔티티에서 연관관계의 주인공이 되는 경우

##### 단방향

##### 양방향

---

#### 일대다 연관관계
* `1` 쪽 엔티티에서 연관관계의 주인공이 되는 경우
* 테이블의 일대다 관계는 항상 `다(N)`쪽에 외래키 있음
* 객체와 테이블의 차이 때문에 반대편 테이블의 외래키를 관리하는 특이한 구조
* `@JoinColumn`을 꼭 사용해야함. 그렇지 않으면 조인 테이블 방식을 사용함(중간에 테이블을 하나 추가)

##### 양방향
* **되도록이면 다대일 양방항을 이용하자**
* 공식적으로 존재하는 매핑은 아님
* @JoinColumn(insert=false, updatable=false)로 주인 지정 필요   
  (readOnly로 만들어버리기)
* 읽기 전용 필드를 사용해서 양방향 처럼 사용하는 방법
---


#### 일대일 연관관계

* 일대일 관계는 그 반대도 일대일
* 주 테이블이나 대상 테이블 중에 외래키 선택 간으
  * 주 테이블에 외래키
  * 대상 테이블에 외래키
* 외래키에 데이터베이스 유니크(UNIQUE) 제약조건 추가

##### 주 테이블에 외래키
* 외래키가 있는 곳이 연관관계의 주인이 된다.
* 반대편은 mappedBy 사용한다.

##### 대상 테이블에 외래키가 있는 경우(Locker Entity에 외래키가 있는 경우)
* **단방향 관계는 지원 X**

* 양방향 관계는 지원

#### 다대다 연관관계
* 관계형 데이터 베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없음
* 중간 테이블을 추가해서 일대다, 다대일 관계로 풀어야한다.

**단방향 추가인 경우,**

Product
```java
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    ... (생략)
}
```

Member
```java
class Member {
  ...(생략)
  @ManyToMany
  @JoinTable(name="MEMBER_PRODUCT")
  private List<Product> products = new ArrayList<>();
}
```

**양방향 추가인 경우,**

Product
```java
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
}
```

Member
```java
class Member {
  ...(생략)
  @ManyToMany
  @JoinTable(name="MEMBER_PRODUCT")
  private List<Product> products = new ArrayList<>();
}
```
