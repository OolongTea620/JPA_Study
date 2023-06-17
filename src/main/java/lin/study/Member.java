package lin.study;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
public class Member {
    @Id
    private Long id;

    @Column(name= "name", nullable = false)
    private String name;

    private int age;

    @Enumerated(EnumType.STRING) // ORDINAL 사용 금지
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob // long text;
    private String description;

    @Transient
    private int temp;

    public Member() {}
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
