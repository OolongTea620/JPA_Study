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
            Team team = new Team();
            team.setName("TeamA");

            Member member = new Member();
            member.setUsername("member1");
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());
            // 참조 관계를 사용해서 연관관계 조회
            Team findTeam = findMember.getTeam();
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