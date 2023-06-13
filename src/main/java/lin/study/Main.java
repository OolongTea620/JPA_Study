package lin.study;

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
            //code : Member 객체 저장
            //DB 저장은 트렌젝션 관리
            
            Member member = new Member();
            member.setId(1L);
            member.setName("myname"); // 비영속 상태

            //  Member 테이블에 인스턴스 저장
            em.persist(member); // 영속 상태, DB에 저장 안됨
            
            tx.commit(); // 트랜젝션 커밋


            // 데이터 찾기
            Member findMember = em.find(Member.class, 1L); // pk가 1인 인스턴스 가져옴
            System.out.println("findMember = " + findMember.getId());
            System.out.println("findMember = " + findMember.getName());

            tx.commit();

            // 삭젝하기
            em.remove(findMember);

            // 수정하기
            Member member2 = new Member();
            member2.setId(2L);
            member2.setName("test");
            em.persist(member2);

            tx.commit();

            Member findMember2 = em.find(Member.class, 2L);
            System.out.println("id = "+ findMember2.getId());
            System.out.println("name = "+ findMember2.getName());
            findMember2.setName("test2"); // jpa가 자동으로 수정 반영
            System.out.println("=====================");
            System.out.println("id = "+ findMember2.getId());
            System.out.println("name = "+ findMember2.getName());

            tx.commit();


        }
        catch (Exception e) {
            tx.rollback(); // 트랜젝션 롤백
        }finally {
            em.close();
        }
        emf.close();

    }
}