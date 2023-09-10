package lin.study;

import javax.persistence.*;

@Entity
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name= "USERNAME")
    private String username;

    // 테이블에 따른 매핑
//    @Column(name = "TEAM_ID")
//    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY) // Member : Team <-> n : 1, Member 클래스 안에 선언이 되었으므로, Member의 기준에서 어노테이션을 달아준다.
    @JoinColumn(name  = "TEAM_ID") // 조인하는 컬럼명 -> 그래서 주인이 된다.
    private Team team;
    public Member() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
