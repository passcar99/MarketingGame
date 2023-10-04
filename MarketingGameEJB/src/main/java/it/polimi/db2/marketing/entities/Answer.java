package it.polimi.db2.marketing.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "answer", schema = "marketinggame")
@NamedQuery(name = "Answer.getAnswerByQuestionId", query = "SELECT a FROM Answer a WHERE a.question = :questionId")
public class Answer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    @ManyToOne
    @JoinColumn(name = "question")
    private Question question;
    @JoinColumn(name = "text")
    private String text;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
