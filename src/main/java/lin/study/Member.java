package lin.study;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name= "USERNAME")
    private String username;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
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

}
