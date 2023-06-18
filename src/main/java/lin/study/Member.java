package lin.study;

import javax.persistence.*;

@Entity
@TableGenerator(name="MEMBER_SEQ_GENERATOR",table = "MY_SEQUENCE",
pkColumnValue = "MEMBER_SEQ", allocationSize = 1
)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

    @Column(name= "name", nullable = false)
    private String name;

    public Member() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
