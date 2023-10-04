package it.polimi.db2.marketing.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "user", schema = "marketinggame")
@NamedQueries({
        @NamedQuery(name = "User.checkCredentials", query = "SELECT r FROM User r  WHERE r.username = ?1 and r.password = ?2"),
        @NamedQuery(name = "User.getLeaderBoard", query = "SELECT u FROM User u ORDER BY u.points DESC"),
        @NamedQuery(name = "User.getUserByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private String email;
    private int points;

    private Integer age;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    @Enumerated(EnumType.STRING)
    private ExpertiseLevel expertise_level;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean offensive;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Answer> answers;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private Collection<Review> reviews;

    public User() {
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addAnswer(Answer answer){
        answers.add(answer);
        answer.setUser(this);
    }

    public void addReview(Review review){
        reviews.add(review);
        review.setUser(this);
    }

    public boolean isOffensive() {
        return offensive;
    }

    public void setOffensive(boolean offensive) {
        this.offensive = offensive;
    }

    public int getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public ExpertiseLevel getExpertise_level() {
        return expertise_level;
    }

    public void setExpertise_level(ExpertiseLevel expertise_level) {
        this.expertise_level = expertise_level;
    }

}
