package it.polimi.db2.marketing.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "login", schema = "marketinggame")
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;



    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = null;

    public void setUser(User user) {
        this.user = user;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
