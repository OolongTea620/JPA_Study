package lin.study;

import lin.study.example.Child;
import lin.study.example.Parent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {
    
    /*
        jpa를 이해하는데 가장 중요한 용어
        엔티티를 영구 저장하는 환경 이라는 의미
     */
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 트랜젝션 단위마다 EntityManager를 생성해야 한다.
        EntityManager em =  emf.createEntityManager();

        // EntityManager에서 트랜젝션 하나 가져옴
        EntityTransaction tx = em.getTransaction();
        // 데이터베이스 트랜젝션 시작
        tx.begin();

        try {
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(child1);
            em.persist(child2);
            em.persist(parent);
        }
        catch (Exception e) {
            tx.rollback(); // 트랜젝션 롤백
        }finally {
            em.close();
        }
        emf.close();

    }
}